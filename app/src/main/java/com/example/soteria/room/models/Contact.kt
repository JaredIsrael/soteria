package com.example.soteria.room.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/*
Name: Contact
Description: A Room-compatible data class for emergency contacts.

Details:
    - phone_number: Primary key of String containing phone number (with country and area code) of the emergency contact
    - first_name: String containing first name of the emergency contact
    - last_name: String containing last name of the emergency contact
    - recording_access: Integer representing the level of access this emergency contact has to audio/video recordings
        -- recording_access = 0: Emergency contact does not have access to any recordings
        -- recording_access = 1: Emergency contact has access to audio recordings
        -- recording_access = 2: Emergency contact has access to video recordings
        -- recording_access = 3: Emergency contact has access to audio AND video recordings

 */
@Entity
data class Contact (
    @PrimaryKey val phone_number: String,
    @ColumnInfo(name = "first_name") val first_name: String,
    @ColumnInfo(name = "last_name") val last_name: String,
    @ColumnInfo(name = "recording_access") val recording_access: Int
)