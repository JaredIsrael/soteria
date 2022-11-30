package com.example.soteria

import android.Manifest
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.location.Geocoder
import android.location.Location
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.telephony.SmsManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import aws.smithy.kotlin.runtime.client.SdkLogMode
import com.amplifyframework.core.Amplify
import com.amplifyframework.storage.StorageAccessLevel
import com.amplifyframework.storage.options.StorageGetUrlOptions
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.soteria.room.viewmodels.ContactViewModel
import com.example.soteria.room.viewmodels.HomeViewModel
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceLikelihood
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.ktx.api.net.awaitFetchPlace
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class HomeFragment : Fragment(), View.OnClickListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var startBtn : Button
    private lateinit var homeTV : TextView
    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var smsManager: SmsManager
    private val mContactViewModel : ContactViewModel by viewModels()
    // this should be a setting that can change on the settings page
    private var initialTime : Long = 0
    private lateinit var audioPath : String

    private val homeModel : HomeViewModel by viewModels()
    private val timerRec = TimerReceiver()
    private lateinit var setTimeBtn : Button
    private lateinit var timeTv : TextView
    private lateinit var notificationBuilder : NotificationCompat.Builder

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lastLat = "none"
    private var lastLong = "none"
    private lateinit var placesClient : PlacesClient
    var currentTinyUrl = ""

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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        Places.initialize(requireContext(), BuildConfig.GOOGLE_MAPS_API_KEY)
        placesClient = Places.createClient(requireContext())

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

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            checkAndAskPermissions()
        }

        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(requireContext())
        } else {
            MediaRecorder()
        }

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

    fun sendMessage(url:String, phoneNumber : String, results : Array<String>) {
        smsManager = SmsManager.getDefault()

        var placeName = results[0]
        var placeAddress = results[1]
        var placeTypes = results[2]

        var tinyUrl = url

        //TODO: Get correct default message
        var defaultMessage = "I'm feeling unsafe right now. This is an automated text from the safety monitoring app Soteria. Please contact me or the authorities as soon as possible."
        var placeMessage = "I am currenltly at this location: "+ placeName
        var addressMessage = "The address is: " + placeAddress
        var typeMessage = "This location is a: "+ placeTypes
        var urlMessage = "I have recorded my surroundings, you can access the recording here: "

        smsManager.sendTextMessage(phoneNumber, null, defaultMessage, null, null)
        smsManager.sendTextMessage(phoneNumber, null, placeMessage, null, null)
        smsManager.sendTextMessage(phoneNumber, null, addressMessage, null, null)
        smsManager.sendTextMessage(phoneNumber, null, typeMessage, null, null)
        smsManager.sendTextMessage(phoneNumber, null,urlMessage, null, null)
        smsManager.sendTextMessage(phoneNumber, null,tinyUrl, null, null)

        Toast.makeText(requireContext(), "message sent", Toast.LENGTH_SHORT).show()

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
        val recordingFile = File(requireContext().getExternalFilesDir(null).toString() + "/recording.mp3")
        Amplify.Storage.uploadFile("RecordingFile.mp3", recordingFile,
            { Log.i("MyAmplifyApp", "Successfully uploaded: ${it.key}")
            getPlacesLocation()
            },
            { Log.e("MyAmplifyApp", "Upload failed", it) }
        )

        Toast.makeText(requireContext(), "recording stopped", Toast.LENGTH_SHORT).show()
    }

    fun playAudio() {
        Toast.makeText(requireContext(), "recording playing", Toast.LENGTH_SHORT).show()
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setDataSource(audioPath)
        mediaPlayer?.prepare()
        mediaPlayer?.start()
    }

    private fun getPlacesLocation() {
        var results = arrayOf<String>()
        val placeFields: List<Place.Field> = listOf(Place.Field.NAME)
        val request: FindCurrentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFields)

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            checkAndAskPermissions()
        }

        val placeResponse = placesClient.findCurrentPlace(request)
        placeResponse.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "found response", Toast.LENGTH_SHORT).show()
                val response = task.result
                val pName = response.placeLikelihoods[0].place.name
                val pAdd = response.placeLikelihoods[0].place.address ?: "no place address"
                val pTypes = response.placeLikelihoods[0].place.types ?: "no place types"
                results += arrayOf<String>(pName, pAdd, pTypes.toString())
            } else {
                val exception = task.exception
                if (exception is ApiException) {
                    Log.e(TAG, "Place not found: ${exception.statusCode}")
                }
            }
            val contactsList = mContactViewModel.getAllContactsList()
            val optionsBuilder = StorageGetUrlOptions.builder()
            optionsBuilder.accessLevel(StorageAccessLevel.PUBLIC)
            //sendMessage("https://www.google.com", "1234567890", arrayOf<String>("", "", "", ""))
            getTinyUrl("https://www.google.com")

            val options:StorageGetUrlOptions = optionsBuilder.build()


            Amplify.Storage.getUrl("RecordingFile.mp3", options,
                {
                    Log.i("Soteria","Amplify URL for recording: "+it.url.toString())
                    Log.i("Soteria", "Trying to send messages")
                    for (contact in contactsList) {
                        Log.i("Soteria","Trying to send message to a contact")
                        //sendMessage(it.url.toString(),contact.phone_number, results)
                    }
                },
                { Log.i("MyAmplifyApp", "Failed to get URL: "+it.message)}
            )
        }

    }

    fun getTinyUrl(fullUrl : String) {
        val requestUrl = "https://api.tinyurl.com/create"
        val queue = Volley.newRequestQueue(requireContext())

        val stringRequest = object : StringRequest(
            Request.Method.POST, requestUrl,
            Response.Listener<String> { response ->
                Log.d("A", "Response is: " + response)
                val json = JSONObject(response)
                val tinyUrl = json.getJSONObject("data").getString("tiny_url")
                Log.i("A", tinyUrl )
                sendMessage(tinyUrl, "1234567890", arrayOf<String>("", "", "", ""))
                Log.i("A" , "Send message with " + tinyUrl)
            },
            Response.ErrorListener { error ->
                Log.d("API", "error => $error")
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] =
                    "Bearer Mb9uZMeI8U5c9MqJAs9WjYe2KmrIYn5m0LgfBFjS58MJBx0X9lp3gSnqARrf"
                return headers
            }

            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["url"] = fullUrl
                return params
            }
        }

        queue.add(stringRequest)
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
        Log.d(TAG, "Checking permissions")

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