package com.example.byespy.data.dao

import androidx.room.*
import com.example.byespy.data.entity.Thread
import kotlinx.coroutines.flow.Flow

@Dao
interface ThreadDao {
    @Query("SELECT * FROM thread_table")
    fun getAll(): Flow<List<Thread>>

    @Query("SELECT * FROM thread_table WHERE id = :id")
    fun getById(id: Int): Thread

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(thread: Thread)

    @Delete
    fun delete(thread: Thread)

    @Query("DELETE FROM thread_table")
    fun deleteAll()
}