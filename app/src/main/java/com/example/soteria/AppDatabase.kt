package com.example.soteria

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Contact::class, Event::class, Recording::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDAO
    abstract fun eventDao(): EventDAO
    abstract fun recordingDao(): RecordingDAO

}