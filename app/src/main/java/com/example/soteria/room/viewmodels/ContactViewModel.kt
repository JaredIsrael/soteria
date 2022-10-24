package com.example.soteria.room.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.soteria.room.ContactRepository
import com.example.soteria.room.models.Contact
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

/*
Name: ContactViewModel
Description: A ViewModel class for displaying emergency contacts with LiveData
 */

class ContactViewModel : ViewModel() {

    var liveDataContact: LiveData<Contact>? = null
    var liveDataContactList: LiveData<List<Contact>>? = null


    fun insert(number: String, fName: String, lName: String, access: Int, context: Context) {
        viewModelScope.launch {
            insertCoroutine(number, fName, lName, access, context)
        }
    }

    fun getAll(context: Context): LiveData<List<Contact>> {
        viewModelScope.launch {
            liveDataContactList = getAllCoroutine(context)
        }
        return liveDataContactList!!
    }

    fun getOne(context: Context): LiveData<Contact> {
        viewModelScope.launch {
            liveDataContact = getOneCoroutine(context)
        }
        return liveDataContact!!
    }

    private suspend fun insertCoroutine(num: String, fName: String, lName: String, access: Int, context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            ContactRepository.insert(num, fName, lName, access, context)
        }
    }

    private suspend fun getOneCoroutine(context: Context) : LiveData<Contact> =  withContext(Dispatchers.IO){
        return@withContext ContactRepository.getOne(context)
    }

    private suspend fun getAllCoroutine(context: Context) : LiveData<List<Contact>> =  withContext(Dispatchers.IO){
        return@withContext ContactRepository.getAll(context)
    }

}

/*
fun insertAll(vararg contact: Contact) {
    repo.insertAll(*contact)
}
fun getAll(context: Context): LiveData<List<Contact>> {
    liveDataContact = ContactRepository.getAll()
    return liveDataContact as LiveData<List<Contact>>
}

*/