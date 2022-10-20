package com.example.soteria.room.daos

import androidx.room.Dao
import androidx.room.Query
import com.example.soteria.room.models.Recording

/*
Name: RecordingDAO
Description: A Room-compatible database access object class that enables and implements database transactions
for the Recording table.

Details:
    - getAll(): Retrieves all entries in the table
 */

@Dao
interface RecordingDAO {
    @Query("SELECT * FROM recording")
    fun getAll(): List<Recording>

}