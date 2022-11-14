package com.example.soteria

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
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
import androidx.fragment.app.Fragment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.nio.file.Paths
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment(), View.OnClickListener {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
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

        startBtn = view.findViewById(R.id.startBtn)
        startBtn.textSize = 24F
        startBtn.setOnClickListener{
            onClick(it)
        }

        homeTV = view.findViewById(R.id.tvHome)
        homeTV.requestFocus()

        timeEditText = view.findViewById(R.id.timeET)
        timeEditText.setOnEditorActionListener { v, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    validateTimeText()
                    val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                    true
                }
                else -> false
            }
        }

        return view
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.startBtn -> startBtnClicked()
        }
    }

    private fun validateTimeText() {
        var text = timeEditText.text.toString()

        if (!text.contains(':')) {
            text = "$text:00"
        } else if (text[0] == ':' && text.contains(':')) {
            text = "0$text"
        }

        timeEditText.setText(text)
        homeTV.requestFocus()

    }

    private fun startBtnClicked() {
        if (isRunning) {
            stopTimer()
        } else {
            initialTime = getStartTime()
            startTimer(initialTime)
        }
    }

    private fun getStartTime() : Long {
        val time = timeEditText.text.toString()
        val min = time.substring(0, time.indexOf(':')).toLong() * 60000
        val sec = time.substring(time.indexOf(':')+ 1).toLong() * 1000
        return min + sec
    }

    private fun stopTimer() {
        startBtn.text = "Start"
        timeEditText.isEnabled = true
        timer.cancel()
        isRunning = false

    }

    private fun startTimer(timeInMilli : Long) {
//        timer = Timer(timeEditText, timeInMilli, 1000)
        timer = object : CountDownTimer(timeInMilli, 1000) {
            override fun onTick(p0: Long) {
                val min = (p0 / 1000) / 60
                var sec = ((p0 / 1000) % 60).toString()

                if (sec.toLong() < 10) {
                    sec = "0$sec"
                }
                timeEditText.setText("$min:$sec")
            }

            override fun onFinish() {
                timeEditText.inputType = InputType.TYPE_DATETIME_VARIATION_TIME
                timeEditText.setText("Starting recording")
                startBtn.text = "Recording"
                startBtn.isEnabled = false
                startBtn.isClickable = false
                startAudioRecording()
                startRecordingTimer()
            }

        }
        timer.start()
        isRunning = true
        timeEditText.isEnabled = false
        startBtn.text = "Stop"
    }

    fun startRecordingTimer() = runBlocking {
        launch {
            delay(2000)
            stopAudioRecording()
            playAudio()
        }
//        val timer = object: CountDownTimer(20000, 1000) {
//            override fun onTick(millisUntilFinished: Long) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onFinish() {
//                stopAudioRecording()
//            }
//        }
//        timer.start()
    }

    fun startAudioRecording() {
        mediaRecorder.prepare()
        mediaRecorder.start()
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

    fun startRecording(view: View) {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        val pm : PackageManager
        startActivity(intent)
    }

    fun getImage() {
        val root =
            File(Environment.getExternalStorageDirectory(), BuildConfig.APPLICATION_ID + File.separator)
        root.mkdirs()
        val fname = "img_" + System.currentTimeMillis() + ".jpg"
        val sdImageMainDirectory = File(root, fname)
        val imageUri = FileProvider.getUriForFile(requireContext(), context?.applicationContext?.packageName + ".provider", sdImageMainDirectory)
        getImage.launch(imageUri)
    }

    private val getImage =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
            if (success){
                Log.d("HomeFragment","Captured")
            }
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