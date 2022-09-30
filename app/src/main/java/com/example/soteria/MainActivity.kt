package com.example.soteria

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sharedPrefs = getSharedPreferences("eula", Context.MODE_PRIVATE)
        // check if user has entered the app before
        sharedPrefs.edit().apply {
            putBoolean("first_time", true)
        }.apply()
        if (sharedPrefs.getBoolean("first_time", true)) {
            val editor = sharedPrefs.edit()
            editor.apply {
                putBoolean("first_time", false)
            }.apply()

            // ask the user to agree to eula
            EulaDialogFragment().show(
                supportFragmentManager, EulaDialogFragment.TAG)
        } else {

        }
    }
}