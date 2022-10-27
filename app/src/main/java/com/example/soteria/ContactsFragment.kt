package com.example.soteria

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.example.soteria.room.models.Contact
import com.example.soteria.room.viewmodels.ContactViewModel


/**
 * A simple [Fragment] subclass.
 * Use the [ContactsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ContactsFragment : Fragment(), RecyclerViewAdapter.RowClickListener {

    private val mContactViewModel : ContactViewModel by viewModels()
    lateinit var recyclerViewAdapter: RecyclerViewAdapter
    lateinit var name : EditText
    lateinit var phone : EditText
    lateinit var saveBtn : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mContactViewModel.getAllContactObservers().observe(this, Observer {
            recyclerViewAdapter.setListData(ArrayList(it))
            recyclerViewAdapter.notifyDataSetChanged()
        })


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_contacts, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        name = view.findViewById<EditText>(R.id.nameInput)
        phone = view.findViewById<EditText>(R.id.numInput)
        saveBtn = view.findViewById<Button>(R.id.saveButton)

        saveBtn.setOnClickListener {
            val text = name.text.split(' ')
            val firstName = text[0]
            val lastName = text[1]
            val number = phone.text.toString()
            if (saveBtn.text.equals("Save")) {
                val cont = Contact(0, firstName, lastName, 0, number)
                mContactViewModel.insertContactInfo(cont)
            } else {
                val cont = Contact(name.getTag(name.id).toString().toInt(), firstName, lastName, 0, number)
                mContactViewModel.updateContactInfo(cont)
                saveBtn.setText("Save")
            }
            name.setText("")
            phone.setText("")
        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            recyclerViewAdapter = RecyclerViewAdapter(this@ContactsFragment)
            adapter = recyclerViewAdapter
            val divider = DividerItemDecoration(context.applicationContext , VERTICAL)

            addItemDecoration(divider)
        }

        return view
    }

    override fun onDeleteUserClickListener(contact: Contact) {
        mContactViewModel.deleteContactInfo(contact)
    }

    override fun onItemClickListener(contact: Contact) {
        name.setText(contact.first_name + " " + contact.last_name)
        phone.setText(contact.phone_number)
        name.setTag(name.id, contact.id)
        saveBtn.setText("Update")
    }


}