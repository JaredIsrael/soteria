package com.example.soteria.room.livedatas

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.soteria.room.models.Contact

/*
Name: ContactsLiveData
Description: Class to link Room repository data to updatable UI elements
 */
class ContactLiveData(private val mContext: Context) : LiveData<List<Contact>>() {
}