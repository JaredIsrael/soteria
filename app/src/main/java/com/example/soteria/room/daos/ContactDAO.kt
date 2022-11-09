package com.example.soteria.room.daos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.example.soteria.room.models.Contact

/*
Name: ContactDAO
Description: A Room-compatible database access object class that enables and implements database transactions
for the Contact table.

Details:
    - getAll(): Retrieves all entries in the table
    - findByName(): Retrieves table entries based on first and last name
    - insertAll(): Inserts all the parameter objects into the table
    - deleteAll(): Deletes all the parameter objects' entries from the table
 */

@Dao
interface ContactDAO {
    @Query("SELECT * FROM contact")
    fun getAll(): List<Contact>?

    @Query("SELECT * FROM contact WHERE first_name LIKE :first AND last_name LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): LiveData<Contact>

    @Insert
    fun insertAll(vararg contacts: Contact)

    @Delete
    fun deleteAll(vararg contacts: Contact)

    @Update
    fun updateContact(contact: Contact?)


}