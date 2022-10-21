package com.example.soteria

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

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
    Name: onCreate():
    Description: Asking user to agree to EULA and accept permissions
    (EulaDialogFragment.setEulaAccepted() calls checkAndAskPermissions)

    Details:
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

        val bnv = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                                as NavHostFragment
        val navController = navHostFragment.navController
        bnv.setupWithNavController(navController)

        if (sharedPrefs.getBoolean(resources.getString(R.string.first_time), true)) {
            sharedPrefs.edit().apply {
                putBoolean(resources.getString(R.string.first_time), false)
            }.apply()
            EulaDialogFragment().show(supportFragmentManager, EulaDialogFragment.TAG)
            checkAndAskPermissions()
        } else if (!sharedPrefs.getBoolean(resources.getString(R.string.eula), false)){
            EulaDialogFragment().show(supportFragmentManager, EulaDialogFragment.TAG)
            checkAndAskPermissions()
        } else {
            checkAndAskPermissions()
        }
        Log.d(TAG,"Entered the on create lifecycle stage.")
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
            Manifest.permission.ACCESS_FINE_LOCATION)

        if (!hasPermissions(this, permissionsList)) {
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