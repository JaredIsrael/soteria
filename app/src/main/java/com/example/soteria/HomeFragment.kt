package com.example.soteria

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView


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
    private var isRunning : Boolean = false
    // this should be a setting that can change on the settings page
    private var initialTime : Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

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
            }

        }
        timer.start()
        isRunning = true
        timeEditText.isEnabled = false
        startBtn.text = "Stop"
    }

}