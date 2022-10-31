package com.example.soteria

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.example.soteria.room.models.Contact
import com.example.soteria.room.viewmodels.ContactViewModel
import android.widget.*
import androidx.core.app.ActivityCompat

/**
 * A simple [Fragment] subclass.
 * Use the [ContactsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ContactsFragment : Fragment(), RecyclerViewAdapter.RowClickListener {

    private val mContactViewModel : ContactViewModel by viewModels()
    lateinit var recyclerViewAdapter: RecyclerViewAdapter
    lateinit var importAdapter: ImportRecyclerViewAdapter
    lateinit var name : EditText
    lateinit var phone : EditText
    lateinit var saveBtn : Button
    lateinit var importBtn : Button

    var cols = listOf<String>(
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER,
        ContactsContract.CommonDataKinds.Phone._ID
    ).toTypedArray()


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
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView1)
        val importRecyclerView = view.findViewById<RecyclerView>(R.id.recyclerView2)
        name = view.findViewById<EditText>(R.id.nameInput)
        phone = view.findViewById<EditText>(R.id.numInput)
        saveBtn = view.findViewById<Button>(R.id.saveButton)
        importBtn = view.findViewById<Button>(R.id.importButton)

        importBtn.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {

                var importedContacts : ArrayList<Contact> = readContacts()

                importRecyclerView?.apply {
                    layoutManager = LinearLayoutManager(context)
                    importAdapter = ImportRecyclerViewAdapter(importedContacts)
                    adapter = importAdapter
//                    importAdapter.onItemClick = {
//                        Toast.makeText(requireContext(), "importedItem click", Toast.LENGTH_SHORT).show()
//                    }
                    val divider = DividerItemDecoration(context.applicationContext , VERTICAL)
                    addItemDecoration(divider)

                    if (importedContacts.isEmpty()) {
                        importRecyclerView!!.visibility = View.GONE
                    } else {
                        importRecyclerView!!.visibility = View.VISIBLE
                        importAdapter.update(importedContacts)
                    }
                }
            }
        }

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

    private fun readContacts() : ArrayList<Contact> {

        var importedContacts = ArrayList<Contact>()
        var rs = context?.contentResolver?.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, cols,
            null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)

        if (rs != null) {
            while (rs.moveToNext()) {
                var name = rs.getString(0)
                var firstName = name.split(' ')[0].toString()
                var lastName = name.split(' ')[1].toString()
                var num = rs.getString(1)

                importedContacts.add(Contact(0, firstName, lastName, 0, num))
            }
        } else {
            importedContacts.add(Contact(0, "not_found", "not_found", 0, "not_found"))
        }
        rs?.close()

        return importedContacts

        //var adapter = SimpleCursorAdapter(context, R.id.linearLayout2, rs, from, to, 0)

//        if (rs != null) {
//            if (rs.moveToNext()) {
//                Toast.makeText(context, rs.getString(0), Toast.LENGTH_LONG).show()
//            }
//        }
    }


}