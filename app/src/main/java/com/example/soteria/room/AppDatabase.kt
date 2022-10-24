package com.example.soteria.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.soteria.room.daos.ContactDAO
import com.example.soteria.room.daos.EventDAO
import com.example.soteria.room.daos.RecordingDAO
import com.example.soteria.room.models.Contact
import com.example.soteria.room.models.Event
import com.example.soteria.room.models.Recording
import java.util.concurrent.Executors

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
        fun getDatabase(context: Context, dbInstance: AppDatabase?): AppDatabase? {
            var db = dbInstance
            if (db == null) {
                synchronized(AppDatabase::class.java) {
                    db = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "app_database").build()
                }
            }
            return db
        }

        /*
        fun deleteDatabase() {
            dbInstance = null
        }
         */
    }

}