package com.example.soteria.room

import android.app.Application
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.soteria.room.models.Contact

/*
Name: ContactRepository
Description: A Room-compatible repository class to act as SSOT for UI elements displaying
emergency contacts

Details:
 */

class ContactRepository (application: Application){

    private val db: AppDatabase = AppDatabase.getDatabase(application)
    private var mContactDAO = db.contactDao()
    val allContacts: LiveData<List<Contact>> = mContactDAO.getAll()

    @WorkerThread
    suspend fun findContactByName(contact: Contact): LiveData<Contact> {
        return mContactDAO.findByName(contact.first_name, contact.first_name)
    }

    @WorkerThread
    suspend fun insertAll(vararg contact: Contact) {
        mContactDAO.insertAll(*contact)
    }

}