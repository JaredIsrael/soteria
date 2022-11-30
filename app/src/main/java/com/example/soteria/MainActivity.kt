package com.example.soteria

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "prefs")


class MainActivity : AppCompatActivity() {

//    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "prefs")
    private lateinit var navController : NavController

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
        val bnv = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                as NavHostFragment
        navController = navHostFragment.navController
        bnv.setupWithNavController(navController)

        lifecycleScope.launch { checkIfFirstTime() }

        createNotificationChannel()

        Log.d(TAG,"Entered the on resume lifecycle stage.")
    }

    public suspend fun writeBoolToDatastore(key: String, value: Boolean){
        val dataStoreKey = booleanPreferencesKey(key)
        dataStore.edit { settings ->
            settings[dataStoreKey] = value
        }
    }

    public suspend fun writeStringToDatastore(key: String, value: String){
        val dataStoreKey = stringPreferencesKey(key)
        dataStore.edit { settings ->
            settings[dataStoreKey] = value
        }
    }

    public suspend fun readBoolFromDatastore(key: String): Boolean? {
        val dataStoreKey = booleanPreferencesKey(key)
        val prefs = dataStore.data.first()
        return prefs[dataStoreKey]
    }

    public suspend fun readStringFromDatastore(key: String): String? {
        val dataStoreKey = stringPreferencesKey(key)
        val prefs = dataStore.data.first()
        return prefs[dataStoreKey]
    }


    private suspend fun checkIfFirstTime() {

        withContext(Dispatchers.Main) {
            val firsTimeKey = booleanPreferencesKey(resources.getString(R.string.first_time))
            val acceptedEula = readBoolFromDatastore(resources.getString(R.string.eula))
            var ds = dataStore.data.first();

            if (!ds.contains(firsTimeKey)) {
                writeBoolToDatastore(resources.getString(R.string.first_time), false)
                EulaDialogFragment().show(supportFragmentManager, EulaDialogFragment.TAG)
                // ask user permissions
                checkAndAskPermissions()
                // get user name for home fragment
                getUserName()
                // have user add a contact
                moveToContactsFragment()
            } else if (acceptedEula == false) {
                EulaDialogFragment().show(supportFragmentManager, EulaDialogFragment.TAG)
                checkAndAskPermissions()
                getUserName()
                moveToContactsFragment()
            } else {
                checkAndAskPermissions()
                if (readBoolFromDatastore(getString(R.string.first_time))!!) {
                    getUserName()
                    moveToContactsFragment()
                }
            }
        }
    }

    private suspend fun moveToContactsFragment() {
        navController.navigate(R.id.action_homeFragment_to_contactsFragment)
        AlertDialog.Builder(this).setMessage("Please add a contact to send an emergency message to")
            .setPositiveButton("Ok", DialogInterface.OnClickListener { _, _ ->
                // do nothing
            })
            .show()
    }

    private suspend fun getUserName() {
        val builder = AlertDialog.Builder(this)
        val editText = EditText(this)
        editText.hint = "Your name"
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.addView(editText)
        builder.setView(layout)
        builder.setTitle("Enter your name")
        builder.setPositiveButton("Continue") { d, _ ->
            if (!collectInput(editText)) {
                d.cancel()
                builder.show()
            }
        }
        builder.setNegativeButton("Decline") { d, _ ->
            d.cancel()
            builder.show()

        }
        builder.create()
        builder.show()
    }

    private fun collectInput(editText: EditText) : Boolean {
        val input = editText.text.toString()
        var validInput = false

        if (input == null || input.trim() == "") {
//            Toast.makeText(this, "Please input your name", Toast.LENGTH_LONG).show()
        } else {
            GlobalScope.launch(Dispatchers.IO) { writeStringToDatastore("name", input) }
            validInput = true
        }

        return validInput
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

        if (!hasPermissions(this, permissionsList)) {
            requestPermissionsLauncher.launch(permissionsList)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(HomeFragment.CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
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