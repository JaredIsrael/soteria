package com.example.soteria

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class EulaDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setMessage("Pseudo Eula for now")
            .setPositiveButton("Yes", null)
            .setNegativeButton("No", DialogInterface.OnClickListener {
                _,_ -> activity?.finish()
            })
            .create()
    companion object {
        const val TAG = "EulaDialog"
    }
}