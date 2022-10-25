package com.example.soteria.room.daos

import androidx.room.Dao
import androidx.room.Query
import com.example.soteria.room.models.Event

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