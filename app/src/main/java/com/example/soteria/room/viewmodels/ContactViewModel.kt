package com.example.soteria.room.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.soteria.room.AppDatabase
import com.example.soteria.room.models.Contact

class ContactViewModel(application: Application) : AndroidViewModel(application!!) {

    var allContacts : MutableLiveData<List<Contact>?> = MutableLiveData()


    fun getAllContactObservers() : MutableLiveData<List<Contact>?> {
        return allContacts
    }

    fun getAllContacts() {
        val contactDao = AppDatabase.getDatabase(getApplication()).contactDao()
        val list = contactDao?.getAll()

        allContacts.postValue(list)
    }

    fun getAllContactsList() : List<Contact> {
        val contactDao = AppDatabase.getDatabase(getApplication()).contactDao()
        return contactDao?.getAll()!!
    }

    fun insertContactInfo(entity: Contact) {
        val contactDao = AppDatabase.getDatabase(getApplication()).contactDao()
        contactDao?.insertAll(entity)
        getAllContacts()
    }

    fun insertAllContactInfo(vararg entity: Contact) {
        val contactDao = AppDatabase.getDatabase(getApplication()).contactDao()
        contactDao?.insertAll(*entity)
        getAllContacts()
    }

    fun updateContactInfo(entity: Contact) {
        val contactDao = AppDatabase.getDatabase(getApplication()).contactDao()
        contactDao?.updateContact(entity)
        getAllContacts()
    }

    fun deleteContactInfo(entity : Contact) {
        val contactDao = AppDatabase.getDatabase(getApplication()).contactDao()
        contactDao?.deleteAll(entity)
        getAllContacts()
    }
}