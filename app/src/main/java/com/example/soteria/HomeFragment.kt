package com.example.soteria

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.nio.file.Paths
import java.util.*
import android.widget.TimePicker
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.soteria.room.viewmodels.HomeViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class HomeFragment : Fragment(), View.OnClickListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var startBtn : Button
    private lateinit var timer : CountDownTimer
    private lateinit var timeEditText : EditText
    private lateinit var homeTV : TextView
    private lateinit var output: String
    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var mediaPlayer: MediaPlayer
    private var isRunning : Boolean = false
    // this should be a setting that can change on the settings page
    private var initialTime : Long = 0
    private lateinit var audioPath : String

    private val homeModel : HomeViewModel by viewModels()
    private val timerRec = TimerReceiver()
    private lateinit var setTimeBtn : Button
    private lateinit var homeTv : TextView
    private lateinit var timeTv : TextView
    private lateinit var notificationBuilder : NotificationCompat.Builder

    companion object {
        const val TAG = "HomeFragment"
        const val CHANNEL_ID = "DefaultNotification"
        const val ACTION_START_RECORDING = "com.example.soteria.ACTION_START_RECORDING"
        const val ACTION_STOP_TIMER = "com.example.soteria.ACTION_STOP_TIMER"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        activity?.registerReceiver(timerRec, IntentFilter(TimerService.ACTION_TICK))
        activity?.registerReceiver(timerRec, IntentFilter(TimerService.ACTION_FINISHED))
        activity?.registerReceiver(timerRec, IntentFilter(ACTION_STOP_TIMER))
        activity?.registerReceiver(timerRec, IntentFilter(ACTION_START_RECORDING))
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        activity?.unregisterReceiver(timerRec)
        with (NotificationManagerCompat.from(requireContext())) {
            cancel(1)
        }
        super.onDestroy()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(requireContext())
        } else {
            MediaRecorder()
        }
        checkAndAskPermissions()

        audioPath = requireContext().getExternalFilesDir(null).toString() + "/recording.mp3"
        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder?.setOutputFile(audioPath)

        setTimeBtn = view.findViewById(R.id.setTimeBtn)
        setTimeBtn.setOnClickListener(this)
        timeTv = view.findViewById(R.id.timeTv)

        startBtn = view.findViewById(R.id.startBtn)
        startBtn.textSize = 24F
        startBtn.setOnClickListener(this)

        homeTV = view.findViewById(R.id.tvHome)
        homeTV.requestFocus()

        if (homeModel.timerRunning) {
            setTimeBtn.text = "Start Recording"
            startBtn.text = "Stop"
        }

        return view
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.startBtn -> startBtnClicked()
            R.id.setTimeBtn -> setTimeBtnClicked()
        }
    }

    private fun setTimeBtnClicked() {
        if (homeModel.timerRunning) {
            // do recording stuff here
        } else {
            openTimePickerDialog()
        }
    }

    private fun openTimePickerDialog() {
        val hourMin = getTimeFromTv()
        val timePicker = TimePickerDialog(context, this, hourMin[0].toInt(), hourMin[1].toInt(), true)
        timePicker.show()
    }

    private fun getTimeFromTv() : LongArray {
        val arr = LongArray(3)

        val timeText = timeTv.text.toString()
        val firstColon = timeText.indexOf(':')
        arr[0] = timeText.substring(0, firstColon).toLong()
        val secondColon = timeText.indexOf(':', firstColon + 1)
        arr[1] = timeText.substring(firstColon + 1, secondColon).toLong()
        arr[2] = timeText.substring(secondColon + 1).toLong()
        return arr
    }

    private fun startBtnClicked() {
        if (homeModel.timerRunning) {
            stopTimer()
        } else {
            val time = getTimeFromTv()
            // hours to ms
            time[0] = time[0] * 3600000
            // min to ms
            time[1] = time[1] * 60000
            // sec to ms
            time[2] = time[2] * 1000
            startTimer(time)
        }
    }


    private fun stopTimer() {
        activity?.stopService(Intent(context, TimerService::class.java))
        homeModel.timerRunning = false
        timeTv.text = "00:30:00"
        setTimeBtn.text = "Set Time"
        startBtn.text = "Start"
        with (NotificationManagerCompat.from(requireContext())) {
            cancel(1)
        }
    }

    private fun startTimer(time : LongArray) {
        homeModel.timerRunning = true
        setTimeBtn.text = "Start Recording"
        val intent = Intent(context, TimerService::class.java)
        intent.putExtra("hour", time[0])
        intent.putExtra("min", time[1])
        activity?.startService(intent)
        createNotification()
        startBtn.text = "Stop"
    }

    private fun createNotification() {
        val launchActivityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, launchActivityIntent, PendingIntent.FLAG_IMMUTABLE)

        val startRecordingIntent = Intent(ACTION_START_RECORDING)
        val startRecordPendingIntent: PendingIntent = PendingIntent.getBroadcast(context,0, startRecordingIntent,PendingIntent.FLAG_IMMUTABLE)

        val stopTimerIntent = Intent(ACTION_STOP_TIMER)
        val stopTimerPendingIntent: PendingIntent = PendingIntent.getBroadcast(context, 0, stopTimerIntent, PendingIntent.FLAG_IMMUTABLE)

        notificationBuilder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Soteria Safety Notification")
            .setContentText("Press start to start recording")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .addAction(0, "Start Recording", startRecordPendingIntent)
            .addAction(0, "Stop Timer", stopTimerPendingIntent)

        with(NotificationManagerCompat.from(requireContext())) {
            notify(1, notificationBuilder.build())
        }
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val time: String = when (hourOfDay) {
            0 -> {
                if (minute < 10) {
                    "00:0$minute:00"
                } else {
                    "00:$minute:00"
                }
            }

            else -> {
                if (minute < 10) {
                    "$hourOfDay:0$minute:00"
                } else {
                    "$hourOfDay:$minute:00"
                }
            }
        }
        timeTv.text = time
    }

    private fun timerFinished() {
        recordAudio()
    }


    private fun updateTimeTv(timeLeft: Long) {
        val hLeft = timeLeft / 3600000 % 24
        var mLeft = (timeLeft / 60000 % 60).toString()
        var sLeft = (timeLeft / 1000 % 60).toString()

        mLeft = "$mLeft"
        if (mLeft.toInt() < 10) {
            mLeft = "0$mLeft"
        }
        sLeft = "$sLeft"
        if (sLeft.toInt() < 10) {
            sLeft = "0$sLeft"
        }

        val time = "$hLeft:$mLeft:$sLeft"

        timeTv.text = time
        notificationBuilder.setContentText("Time Left: $time | Expand for options")
        with (NotificationManagerCompat.from(requireContext())) {
            notify(1, notificationBuilder.build())
        }
    }


    private inner class TimerReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null) return

            when (intent.action) {
                TimerService.ACTION_TICK -> {
                    intent.extras?.let { updateTimeTv(it.getLong(TimerService.TIME_LEFT_KEY,0)) }
                }
                TimerService.ACTION_FINISHED -> timerFinished()
                ACTION_START_RECORDING -> timerFinished() // assume there will be some method for starting to record
                ACTION_STOP_TIMER -> stopTimer()
            }
        }

    }

    fun recordAudio() = runBlocking {
        launch {
            startAudioRecording()
            delay(5000)
            stopAudioRecording()
            playAudio()
        }
    }

    fun startAudioRecording() {
        mediaRecorder.prepare()
        mediaRecorder.start()
        Toast.makeText(requireContext(), "recording started", Toast.LENGTH_SHORT).show()
    }

    fun stopAudioRecording() {
        mediaRecorder.stop()
        mediaRecorder.release()
        Toast.makeText(requireContext(), "recording stopped", Toast.LENGTH_SHORT).show()
    }

    fun playAudio() {
        Toast.makeText(requireContext(), "recording playing", Toast.LENGTH_SHORT).show()
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setDataSource(audioPath)
        mediaPlayer?.prepare()
        mediaPlayer?.start()
    }

    // Move into and finish PermissionHelper class
    private val requestPermissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            permissions ->
        permissions.entries.forEach {
            Log.d("DEBUG", "${it.key} = ${it.value}")
        }

    }

    /*
    Name: checkAndAskPermission():
    Description: Check for each permission in the list and if any are missing, ask for them
    (Android will only ask the user for the specific missing permissions)
     */
    fun checkAndAskPermissions() {
        Log.d(MainActivity.TAG, "Checking permissions")

        val permissionsList = arrayOf(
            Manifest.permission.SEND_SMS,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_PHONE_NUMBERS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.POST_NOTIFICATIONS)

        if (!hasPermissions(requireContext(), permissionsList)) {
            requestPermissionsLauncher.launch(permissionsList)
        }
    }

    /*
    Name: hasPermissions():
    Description: Helper function to quickly check if all permissions are granted or if 1 or more are missing
     */
    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

}