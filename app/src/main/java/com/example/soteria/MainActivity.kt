package com.example.soteria

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sharedPrefs = getSharedPreferences(resources.getString(R.string.org), Context.MODE_PRIVATE)
        // check if user has entered the app before
        if (sharedPrefs.getBoolean(resources.getString(R.string.first_time), true)) {
            sharedPrefs.edit().apply {
                putBoolean(resources.getString(R.string.first_time), false)
            }.apply()

            // ask the user to agree to eula
            EulaDialogFragment().show(
                supportFragmentManager, EulaDialogFragment.TAG)
        } else if (!sharedPrefs.getBoolean(resources.getString(R.string.eula), false)){
            EulaDialogFragment().show(
                supportFragmentManager, EulaDialogFragment.TAG)
        } else { // need an else if to check for permissions

        }
    }

}