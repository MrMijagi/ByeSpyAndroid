package com.example.byespy.data.dao

import androidx.room.*
import com.example.byespy.data.entity.Contact
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Query("SELECT * FROM contact_table")
    fun getAll(): Flow<List<Contact>>

    @Query("SELECT * FROM contact_table WHERE id = :id")
    fun getById(id: Long): Contact

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(contact: Contact): Long

    @Delete
    fun delete(contact: Contact)

    @Query("DELETE FROM contact_table")
    fun deleteAll()
}