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
import androidx.fragment.app.DialogFragment
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class EulaDialogFragment : DialogFragment() {

    companion object {
        const val TAG = "EulaDialog"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
            .setMessage(readTextFile())
            .setPositiveButton(resources.getString(R.string.eula_accept), DialogInterface.OnClickListener {
                    _,_ -> setEulaAccepted()
            })
            .setNegativeButton(resources.getString(R.string.eula_decline), DialogInterface.OnClickListener {
                    _,_ -> activity?.finish()
            })
            .create()

        return builder
    }

    private fun setEulaAccepted() {
        val prefs = requireActivity().getSharedPreferences(resources.getString(R.string.org), Context.MODE_PRIVATE)
        prefs.edit().apply {
            putBoolean(resources.getString(R.string.eula), true)
        }.apply()
    }

    override fun onStart() {
        Toast.makeText(requireContext(), "onStart EulaDialogFragment", Toast.LENGTH_SHORT).show()
        Log.d(TAG,"Entered the on start lifecycle stage.")
        super.onStart()
    }

    override fun onResume() {
        Toast.makeText(requireContext(), "onResume EulaDialogFragment", Toast.LENGTH_SHORT).show()
        Log.d(TAG,"Entered the on resume lifecycle stage.")
        super.onResume()
    }

    override fun onPause() {
        Toast.makeText(requireContext(), "onPause EulaDialogFragment", Toast.LENGTH_SHORT).show()
        Log.d(TAG,"Entered the on pause lifecycle stage.")
        super.onPause()
    }

    override fun onStop() {
        Toast.makeText(requireContext(), "onStop EulaDialogFragment", Toast.LENGTH_SHORT).show()
        Log.d(TAG,"Entered the on stop lifecycle stage.")
        super.onStop()
    }

    override fun onDestroy() {
        Toast.makeText(requireContext(), "onDestroy EulaDialogFragment", Toast.LENGTH_SHORT).show()
        Log.d(TAG,"Entered the on destroy lifecycle stage.")
        super.onDestroy()
    }

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
}