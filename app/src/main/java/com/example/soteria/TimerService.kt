package com.example.soteria

import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder

class TimerService : Service() {

    private val finishIntent = Intent(ACTION_FINISHED)
    private val tickIntent = Intent(ACTION_TICK)
    private lateinit var timer: CountDownTimer

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            createTimer(intent.extras?.getLong("hour") as Long, intent.extras?.getLong("min") as Long)
        }
        timer?.start()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        if (timer != null) {
            timer.cancel()
        }
        super.onDestroy()
    }

    private fun createTimer(hour: Long, min: Long) {
        timer = object : CountDownTimer(hour + min, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tickIntent.putExtra(TIME_LEFT_KEY, millisUntilFinished)
                sendBroadcast(tickIntent)
            }

            override fun onFinish() {
                sendBroadcast(finishIntent)
                stopSelf()
            }

        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_FINISHED: String = "com.example.soteria.ACTION_FINISHED"
        const val ACTION_TICK: String = "com.example.soteria.ACTION_TICK"
        const val TIME_LEFT_KEY: String = "timeLeft"
    }
}