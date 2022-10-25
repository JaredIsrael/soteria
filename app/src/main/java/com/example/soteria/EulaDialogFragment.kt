package com.example.soteria

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.fragment.app.DialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class EulaDialogFragment : DialogFragment() {

    companion object {
        const val TAG = "EulaDialog"
    }

    /*
    Name: onCreateDialog():
    Description: Define functionality for EULA dialog buttons
    (User must accept EULA to use app)

    Details:
    Positive (accept) button calls setEulaAccepted()
    Negative (reject) button closes the app
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
            .setMessage(readTextFile())
            .setPositiveButton(resources.getString(R.string.eula_accept), DialogInterface.OnClickListener {
                    _,_ -> GlobalScope.launch { setEulaAccepted() }
            })
            .setNegativeButton(resources.getString(R.string.eula_decline), DialogInterface.OnClickListener {
                    _,_ -> GlobalScope.launch { setEulaDeclined() }
            })
            .create()

        return builder
    }


    /*
    Name: setEulaAccepted():
    Description: Sets variable in shared preferences indicating that the EULA was accepted and
    then calls MainActivity.checkAndAskPermissions()
     */
    private suspend fun setEulaAccepted() {
        withContext(Dispatchers.IO) {
            (activity as MainActivity).writeBoolToDatastore(
                resources.getString(R.string.eula),
                true
            )
            (activity as MainActivity).checkAndAskPermissions()
        }
    }

    /*
    Name: setEulaDeclined():
    Description: Sets variable in DS indicating that the EULA was declined and
    then finishes activity
     */
    private suspend fun setEulaDeclined() {
        withContext(Dispatchers.IO) {
            (activity as MainActivity).writeBoolToDatastore(
                resources.getString(R.string.eula),
                false
            )
            activity?.finish()
        }
    }





    /*
    Name: readTextFile()
    Description: Helper function to read and create a String from a text file
     */

    private fun readTextFile(): String {
        var string: String? = ""
        val stringBuilder = StringBuilder()
        val `is`: InputStream = this.resources.openRawResource(R.raw.eula_text)
        val reader = BufferedReader(InputStreamReader(`is`))
        while (true) {
            try {
                if (reader.readLine().also { string = it } == null) break
            } catch (e: IOException) {
                e.printStackTrace()
            }
            stringBuilder.append(string).append("\n")
        }
        `is`.close()
        return stringBuilder.toString()
    }

    override fun onStart() {
        Log.d(TAG,"Entered the on start lifecycle stage.")
        super.onStart()
    }

    override fun onResume() {
        Log.d(TAG,"Entered the on resume lifecycle stage.")
        super.onResume()
    }

    override fun onPause() {
        Log.d(TAG,"Entered the on pause lifecycle stage.")
        super.onPause()
    }

    override fun onStop() {
        Log.d(TAG,"Entered the on stop lifecycle stage.")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d(TAG,"Entered the on destroy lifecycle stage.")
        super.onDestroy()
    }

}