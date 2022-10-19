package com.example.soteria

import androidx.room.Database
import androidx.room.RoomDatabase

/*
Name: AppDatabase
Description: A Room-compatible database class containing the Contact, Event, and Recording entities (tables).

Details:
 */
@Database(entities = [Contact::class, Event::class, Recording::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDAO
    abstract fun eventDao(): EventDAO
    abstract fun recordingDao(): RecordingDAO

}