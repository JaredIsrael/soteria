package com.example.soteria.room.livedatas

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.soteria.room.models.Contact

class ContactLiveData(private val mContext: Context) : LiveData<List<Contact>>() {
}