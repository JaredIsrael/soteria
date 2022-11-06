package com.example.soteria

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
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
import androidx.recyclerview.selection.*
import kotlin.properties.Delegates

/**
 * A simple [Fragment] subclass.
 * Use the [ContactsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ContactsFragment : Fragment(), RecyclerViewAdapter.RowClickListener {

    private val mContactViewModel : ContactViewModel by viewModels()
    var importRecyclerView : RecyclerView? = null
    lateinit var importedContacts : ArrayList<Contact>
    lateinit var selectedContacts : ArrayList<Contact>
    lateinit var recyclerViewAdapter: RecyclerViewAdapter
    lateinit var importAdapter: ImportRecyclerViewAdapter
    lateinit var name : EditText
    lateinit var phone : EditText
    public var accessNum = 0
    public var accessString = ""
    lateinit var saveBtn : Button
    lateinit var importBtn : Button
    lateinit var saveToAppBtn : Button
    lateinit var deleteBtn : Button
    lateinit var accessSpinner : Spinner

    var cols = listOf<String>(
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER,
        ContactsContract.CommonDataKinds.Phone._ID
    ).toTypedArray()

    val accessStringToNum = mapOf("No recording access" to 0,"Audio recording access" to 1,
        "Video recording access" to 2, "Audio and video recording access" to 3)
    val accessNumToString = mapOf(0 to "No recording access", 1 to "Audio recording access",
        2 to "Video recording access", 3 to "Audio and video recording access")


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
        saveToAppBtn = view.findViewById<Button>(R.id.saveToAppButton)
        deleteBtn = view.findViewById<Button>(R.id.deleteButton)
        accessSpinner = view.findViewById<Spinner>(R.id.accessSpinner)

        saveToAppBtn.visibility = View.INVISIBLE
        deleteBtn.visibility = View.INVISIBLE

        val accessOptions: Array<String> = resources.getStringArray(R.array.recording_access_array)
        setupSpinner(accessOptions)

        saveBtn.setOnClickListener {

            val text = name.text.split(' ')
            val firstName = text[0]
            val lastName = text[1]
            val number = phone.text.toString()
            accessSpinner.setSelection(accessStringToNum[accessString]!!)

            if (saveBtn.text.equals("Save")) {
                val cont = Contact(0, firstName, lastName, accessNum, number)
                mContactViewModel.insertContactInfo(cont)
            } else {
                val cont = Contact(name.getTag(name.id).toString().toInt(), firstName, lastName, accessNum, number)

                mContactViewModel.updateContactInfo(cont)
                saveBtn.setText("Save")
                deleteBtn.visibility = View.INVISIBLE
            }
            name.setText("")
            phone.setText("")
            accessSpinner.setSelection(0)
        }

        deleteBtn.setOnClickListener {
            val text = name.text.split(' ')
            val firstName = text[0]
            val lastName = text[1]
            val number = phone.text.toString()
            val cont =
                Contact(name.getTag(name.id).toString().toInt(), firstName, lastName, 0, number)
            mContactViewModel.deleteContactInfo(cont)
            name.setText("")
            phone.setText("")
            accessSpinner.setSelection(0)
        }

        importBtn.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {

                importedContacts = readContacts()

                if (importRecyclerView.adapter == null) {
                    setupImportRecycler(importRecyclerView!!)
                }

            }
        }

        saveToAppBtn.setOnClickListener {
            mContactViewModel.insertAllContactInfo(*selectedContacts.toTypedArray())
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

    override fun onItemClickListener(contact: Contact) {
        name.setText(contact.first_name + " " + contact.last_name)
        phone.setText(contact.phone_number)
        name.setTag(name.id, contact.id)
        saveBtn.setText("Update")
        accessSpinner.setSelection(contact.recording_access)
        deleteBtn.visibility = View.VISIBLE
    }

    private fun setupImportRecycler(importRecyclerView: RecyclerView) {
        importRecyclerView?.apply {
            layoutManager = LinearLayoutManager(context)
            importAdapter = ImportRecyclerViewAdapter(importedContacts)
            adapter = importAdapter
            importAdapter.onItemClick = {
                Toast.makeText(requireContext(), "importedItem click", Toast.LENGTH_SHORT).show()
            }
            val divider = DividerItemDecoration(context.applicationContext , VERTICAL)
            addItemDecoration(divider)

            importAdapter.tracker = setupTracker(importRecyclerView)

        }
    }

    private fun setupSpinner(accessOptions : Array<String>){

        if (accessSpinner != null) {
            val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, accessOptions)
            accessSpinner.adapter = spinnerAdapter

            accessSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    accessString = accessOptions[position]
                    accessNum = accessStringToNum[accessString]!!
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
        }
    }

    private fun setupTracker(importRecyclerView: RecyclerView): SelectionTracker<Long> {

        var tracker = SelectionTracker.Builder(
            "mySelection",
            importRecyclerView,
            StableIdKeyProvider(importRecyclerView),
            ItemLookup(importRecyclerView),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()

        tracker?.addObserver(
            object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    super.onSelectionChanged()
                    val nItems : Int? = tracker?.selection?.size()

                    if (nItems != null && nItems > 0) {
                        saveToAppBtn.visibility = View.VISIBLE
                        val selections = tracker?.selection!!
                        var selectionList = selections.map {
                            importAdapter.items[it.toInt()]
                        }.toList()
                        selectedContacts = ArrayList(selectionList)
                    } else {
                        saveToAppBtn.visibility = View.INVISIBLE
                    }
                }
            })

        return tracker
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

    }

    inner class ItemIdKeyProvider(private val recyclerView: RecyclerView)
        : ItemKeyProvider<Long>(SCOPE_MAPPED) {

        override fun getKey(position: Int): Long? {
            return recyclerView.adapter?.getItemId(position)
                ?: throw IllegalStateException("RecyclerView adapter is not set!")
        }

        override fun getPosition(key: Long): Int {
            val viewHolder = recyclerView.findViewHolderForItemId(key)
            return viewHolder?.layoutPosition ?: RecyclerView.NO_POSITION
        }
    }

    inner class ItemLookup(private val rv: RecyclerView)
        : ItemDetailsLookup<Long>() {
        override fun getItemDetails(event: MotionEvent)
                : ItemDetails<Long>? {

            val view = rv.findChildViewUnder(event.x, event.y)
            if(view != null) {
                return (rv.getChildViewHolder(view) as ImportRecyclerViewAdapter.ImportContactViewHolder)
                    .getItemDetails()
            }
            return null
        }
    }

}