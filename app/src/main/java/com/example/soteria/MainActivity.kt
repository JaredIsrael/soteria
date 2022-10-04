package com.example.soteria

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sharedPrefs = getSharedPreferences("com.android.soteria", Context.MODE_PRIVATE)
        // check if user has entered the app before
        if (sharedPrefs.getBoolean("first_time", true)) {
            sharedPrefs.edit().apply {
                putBoolean("first_time", false)
            }.apply()

            // ask the user to agree to eula
            EulaDialogFragment().show(
                supportFragmentManager, EulaDialogFragment.TAG)
        } else if (!sharedPrefs.getBoolean("eula", false)){
            EulaDialogFragment().show(
                supportFragmentManager, EulaDialogFragment.TAG)
        } else { // need an else if to check for permissions

        }
        Toast.makeText(this, "onCreate MainActivity", Toast.LENGTH_SHORT).show()
        Log.d(TAG,"Entered the on resume lifecycle stage.")
    }

    override fun onResume() {
        Toast.makeText(this, "onResume MainActivity", Toast.LENGTH_SHORT).show()
        Log.d(TAG,"Entered the on resume lifecycle stage.")
        super.onResume()
    }

    override fun onStart() {
        Toast.makeText(this, "onStart MainActivity", Toast.LENGTH_SHORT).show()
        Log.d(TAG,"Entered the on start lifecycle stage.")
        super.onStart()
    }

    override fun onPause() {
        Toast.makeText(this, "onPause MainActivity", Toast.LENGTH_SHORT).show()
        Log.d(TAG,"Entered the on pause lifecycle stage.")
        super.onPause()
    }
    override fun onStop() {
        Toast.makeText(this, "onStop MainActivity", Toast.LENGTH_SHORT).show()
        Log.d(TAG,"Entered the on stop lifecycle stage.")
        super.onStop()
    }
    override fun onRestart() {
        Toast.makeText(this, "onRestart MainActivity", Toast.LENGTH_SHORT).show()
        Log.d(TAG,"Entered the on restart lifecycle stage.")
        super.onRestart()
    }

    override fun onDestroy() {
        Toast.makeText(this, "onDestroy MainActivity", Toast.LENGTH_SHORT).show()
        Log.d(TAG,"Entered the on destroy lifecycle stage.")
        super.onDestroy()
    }

    fun userAgreedToEula() {
        val sharedPrefs = getSharedPreferences("com.android.soteria", Context.MODE_PRIVATE)
        sharedPrefs.edit().apply {
            putBoolean("eula", true)
        }.apply()
        // go to get permissions
    }


}