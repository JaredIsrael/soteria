package com.example.soteria

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class EulaDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setMessage(readTextFile())
            .setPositiveButton("Yes", DialogInterface.OnClickListener {
                _,_ -> (activity as MainActivity).userAgreedToEula()
            })
            .setNegativeButton("No", DialogInterface.OnClickListener {
                _,_ -> activity?.finish()
            })
            .create()
    companion object {
        const val TAG = "EulaDialog"
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