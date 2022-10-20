package com.example.soteria.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
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

    companion object {

        private var dbInstance: AppDatabase? = null

        /*
        Name: getDatabase()
        Description: Method to get database object or make one if there isn't already one.
         */
        fun getDatabase(context: Context): AppDatabase {
            if (dbInstance == null) {
                dbInstance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database").build()
                )
            }
            return dbInstance!!
        }

        fun deleteDatabase() {
            dbInstance = null
        }
    }

}