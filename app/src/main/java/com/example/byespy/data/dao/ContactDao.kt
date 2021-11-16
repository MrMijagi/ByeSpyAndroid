package com.example.byespy.data.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.byespy.data.entity.Contact
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

interface ContactDao {
    @Query("SELECT * FROM contact_table")
    fun getContacts(): Flow<List<Contact>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(contact: Contact)

    @Query("DELETE FROM contact_table")
    suspend fun deleteAll()
}