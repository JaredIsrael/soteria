package com.example.soteria

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_settings, container, false)
        GlobalScope.launch { setupSpinners(v) }
        GlobalScope.launch { setupCheckboxes(v) }
        return v
    }

    private suspend fun setupSpinners(v: View){
        val triggerSpinner = v.findViewById<Spinner>(R.id.trigger_spinner)
        val formatSpinner = v.findViewById<Spinner>(R.id.message_spinner)
        context?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.trigger_array,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                triggerSpinner.adapter = adapter
                val currentTrigger:String? = (activity as MainActivity).readStringFromDatastore("currentTrigger")
                currentTrigger.let {
                    triggerSpinner.setSelection(adapter.getPosition(currentTrigger))
                }
                triggerSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        var currentItem = parent?.getItemAtPosition(position)
                        currentItem.let {
                            GlobalScope.launch { setTriggerType(currentItem.toString()) }
                        }
                    }

                }
            }


            ArrayAdapter.createFromResource(
                it,
                R.array.format_array,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                formatSpinner.adapter = adapter
                val currentMessageFormat:String? = (activity as MainActivity).readStringFromDatastore("currentMessageFormat")
                currentMessageFormat.let {
                    formatSpinner.setSelection(adapter.getPosition(currentMessageFormat))
                }
                formatSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        var currentItem = parent?.getItemAtPosition(position)
                        currentItem.let {
                            GlobalScope.launch { setMessageFormat(currentItem.toString()) }
                        }
                        setCurrentMessageTextView(v.findViewById(R.id.currentMessageTextView), position)
                    }

                }
            }
        }

    }

    private suspend fun setupCheckboxes(v:View){
        val recordAudio:Boolean? = (activity as MainActivity).readBoolFromDatastore("recordAudio")
        recordAudio.let {
            val recordAudioCheckbox:CheckBox = v.findViewById(R.id.record_audio_checkbox)
            recordAudioCheckbox.isChecked=(recordAudio==true)
            recordAudioCheckbox.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                    GlobalScope.launch { setAudioSettings(isChecked) }
                }
            )
        }
        val recordVideo:Boolean? = (activity as MainActivity).readBoolFromDatastore("recordVideo")
        recordVideo.let {
            val recordVideoCheckbox:CheckBox = v.findViewById(R.id.record_video_checkbox)
            recordVideoCheckbox.isChecked = (recordVideo==true)
            recordVideoCheckbox.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                GlobalScope.launch { setVideoSettings(isChecked) }
            }
            )
        }

    }

    private suspend fun setAudioSettings(isOn: Boolean){
        (activity as MainActivity).writeBoolToDatastore("recordAudio", isOn)
    }

    private suspend fun setVideoSettings(isOn: Boolean){
        (activity as MainActivity).writeBoolToDatastore("recordVideo", isOn)
    }

    private suspend fun setTriggerType(triggerType:String){
        (activity as MainActivity).writeStringToDatastore("currentTrigger", triggerType)

    }

    private suspend fun setMessageFormat(messageFormat:String){
        (activity as MainActivity).writeStringToDatastore("currentMessageFormat", messageFormat)
    }

    private fun setCurrentMessageTextView(textView:TextView, position: Int){
        val message:String = resources.getStringArray(R.array.message_array)[position]
        textView.setText(message)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
