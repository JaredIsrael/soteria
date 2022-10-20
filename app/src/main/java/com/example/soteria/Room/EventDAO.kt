package com.example.soteria.Room

import androidx.room.Dao
import androidx.room.Query

/*
Name: EventDAO
Description: A Room-compatible database access object class that enables and implements database transactions
for the Event table.

Details:
    - getAll(): Retrieves all entries in the table
 */

@Dao
interface EventDAO {
    @Query("SELECT * FROM event")
    fun getAll(): List<Event>

}