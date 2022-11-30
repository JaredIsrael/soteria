package com.example.soteria

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat

class PermissionsHelper(context: Context) {

//    private val mContext = context
//    private val requestPermissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
//            permissions ->
//        permissions.entries.forEach {
//            Log.d("DEBUG", "${it.key} = ${it.value}")
//        }
//
//    }
//
//    /*
//    Name: checkAndAskPermission():
//    Description: Check for each permission in the list and if any are missing, ask for them
//    (Android will only ask the user for the specific missing permissions)
//     */
//    fun checkAndAskPermissions() {
//        Log.d(MainActivity.TAG, "Checking permissions")
//
//        val permissionsList = arrayOf(
//            Manifest.permission.SEND_SMS,
//            Manifest.permission.CAMERA,
//            Manifest.permission.RECORD_AUDIO,
//            Manifest.permission.READ_CONTACTS,
//            Manifest.permission.READ_PHONE_NUMBERS,
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.ACCESS_COARSE_LOCATION,
//            Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.POST_NOTIFICATIONS)
//
//        if (!hasPermissions(mContext, permissionsList)) {
//            requestPermissionsLauncher.launch(permissionsList)
//        }
//    }
//
//    /*
//    Name: hasPermissions():
//    Description: Helper function to quickly check if all permissions are granted or if 1 or more are missing
//     */
//    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean = permissions.all {
//        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
//    }
}