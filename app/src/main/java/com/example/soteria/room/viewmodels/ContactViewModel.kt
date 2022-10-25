package com.example.soteria.room.viewmodels

import android.app.Application
import androidx.annotation.WorkerThread
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.soteria.room.ContactRepository
import com.example.soteria.room.models.Contact

class ContactViewModel(private val repository: ContactRepository, application: Application) : AndroidViewModel(application!!) {

    val allContacts: LiveData<List<Contact>> = repository.allContacts
}