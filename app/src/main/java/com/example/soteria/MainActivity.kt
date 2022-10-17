package com.example.soteria

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    private val requestPermissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        permissions ->
            permissions.entries.forEach {
                Log.d("DEBUG", "${it.key} = ${it.value}")
            }

    }


    /*
    If the user denies permissions, tell them why the permissions are needed

    if(ActivityCompat.shouldShowRequestPermissionRationale(this, the_permissions)) {
        showRationaleDialog(
            getString(R.string.rationale_title),
            getString(R.string.rationale_desc)
        )
    }
     */

    /*
    onCreate():
    Asking user to agree to EULA and accept permissions
    (EulaDialogFragment.setEulaAccepted() calls checkAndAskPermissions)

    Check if the user has entered the app before ->
    If it's their first time, ask them to agree to the EULA

    Check if the user has agreed to the EULA ->
    If they haven't agreed to the EULA, ask them to agree to the EULA

    If it's not their first time and they have agreed to the EULA ->
    Check and ask for the appropriate permissions
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sharedPrefs = getSharedPreferences(resources.getString(R.string.org), Context.MODE_PRIVATE)

        if (sharedPrefs.getBoolean(resources.getString(R.string.first_time), true)) {
            sharedPrefs.edit().apply {
                putBoolean(resources.getString(R.string.first_time), false)
            }.apply()
            EulaDialogFragment().show(supportFragmentManager, EulaDialogFragment.TAG)
        } else if (!sharedPrefs.getBoolean(resources.getString(R.string.eula), false)){
            EulaDialogFragment().show(supportFragmentManager, EulaDialogFragment.TAG)
        } else {
            checkAndAskPermissions()
        }
        Log.d(TAG,"Entered the on resume lifecycle stage.")
    }

    /*
    checkAndAskPermission():
    Check for each permission in the list and if any are missing, launch requestPermissionsLauncher
    (Android will only ask the user for the specific missing permissions)
     */
    fun checkAndAskPermissions() {
        Log.d(TAG, "Checking permissions")

        val permissionsList = arrayOf(
            Manifest.permission.SEND_SMS,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_PHONE_NUMBERS)

        if (!hasPermissions(this, permissionsList)) {
            requestPermissionsLauncher.launch(permissionsList)
        }
    }

    /*
    hasPermissions():
    Helper function to quickly check if all permissions are granted or if 1 or more are missing
     */
    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onResume() {
        Log.d(TAG,"Entered the on resume lifecycle stage.")
        super.onResume()
    }

    override fun onStart() {
        Log.d(TAG,"Entered the on start lifecycle stage.")
        super.onStart()
    }

    override fun onPause() {
        Log.d(TAG,"Entered the on pause lifecycle stage.")
        super.onPause()
    }
    override fun onStop() {
        Log.d(TAG,"Entered the on stop lifecycle stage.")
        super.onStop()
    }
    override fun onRestart() {
        Log.d(TAG,"Entered the on restart lifecycle stage.")
        super.onRestart()
    }

    override fun onDestroy() {
        Log.d(TAG,"Entered the on destroy lifecycle stage.")
        super.onDestroy()
    }

}