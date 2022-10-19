package com.example.soteria

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/*
Name: Contact
Description: A Room-compatible data class for emergency contacts.

Details:
    - id: Simple integer PrimaryKey
    - first_name: String containing first name of the emergency contact
    - last_name: String containing last name of the emergency contact
    - phone_number: String containing phone number (with country and area code) of the emergency contact
    - recording_access: Integer representing the level of access this emergency contact has to audio/video recordings
        -- recording access = 0: Emergency contact has no access to any recordings
        -- recording access = 1: Emergency contact has access to audio recordings
        -- recording access = 2: Emergency contact has access to video recordings
        -- recording access = 3: Emergency contact has access to audio AND video recordings

 */
@Entity(tableName = "contacts")
data class Contact (
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "first_name") val first_name: String?,
    @ColumnInfo(name = "last_name") val last_name: String?,
    @ColumnInfo(name = "phone_number") val phone_number: String?,
    @ColumnInfo(name = "recording_access") val recording_access: Int?
)