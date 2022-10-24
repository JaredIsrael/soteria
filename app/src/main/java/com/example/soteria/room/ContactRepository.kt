package com.example.soteria.room

import android.app.Application
import android.content.Context
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.soteria.room.daos.ContactDAO
import com.example.soteria.room.models.Contact
import com.example.soteria.room.AppDatabase


/*
Name: ContactRepository
Description: A Room-compatible repository class to act as SSOT for UI elements displaying
emergency contacts

Details:
 */

class ContactRepository {

    companion object {

        private var db: AppDatabase? = null

        fun insert(number: String, fName: String, lName: String, access: Int, context: Context) {
            val contactDetails = Contact(number, fName, lName, access)
            val mContactDAO : ContactDAO = getDatabase(context)!!.contactDao()
            mContactDAO.insert(contactDetails)
        }

        fun getOne(context: Context): LiveData<Contact> {
            val mContactDAO : ContactDAO = getDatabase(context)!!.contactDao()
            return mContactDAO.getOne()
        }

        fun getAll(context: Context): LiveData<List<Contact>> {
            val mContactDAO : ContactDAO = getDatabase(context)!!.contactDao()
            return mContactDAO.getAll()
        }

        private fun getDatabase(context: Context): AppDatabase? {
            db = AppDatabase.getDatabase(context, db)!!
            return db
        }
    }

    /*

    @WorkerThread
    suspend fun findContactByName(contact: Contact): LiveData<Contact> {
        return mContactDAO.findByName(contact.first_name, contact.first_name)
    }

    fun insertAll(vararg contact: Contact) {
        mContactDAO.insertAll(*contact)
    }


     */

}