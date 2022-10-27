package com.example.soteria.room.viewmodels

import android.app.Application
import androidx.annotation.WorkerThread
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.soteria.room.AppDatabase
import com.example.soteria.room.ContactRepository
import com.example.soteria.room.models.Contact

class ContactViewModel(application: Application) : AndroidViewModel(application!!) {

//    val allContacts: LiveData<List<Contact>> = repository.allContacts

    var allContacts : MutableLiveData<List<Contact>?> = MutableLiveData()


    fun getAllContactObservers() : MutableLiveData<List<Contact>?> {
        return allContacts
    }

    fun getAllContacts() {
        val contactDao = AppDatabase.getDatabase(getApplication()).contactDao()
        val list = contactDao?.getAll()

        allContacts.postValue(list)
    }

    fun insertContactInfo(entity: Contact) {
        val contactDao = AppDatabase.getDatabase(getApplication()).contactDao()
        contactDao?.insertAll(entity)
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