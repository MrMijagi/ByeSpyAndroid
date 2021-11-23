package com.example.byespy.data.dao

import androidx.room.*
import com.example.byespy.data.entity.Conversation
import com.example.byespy.data.model.ConversationItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversation_table")
    fun getAll(): Flow<List<Conversation>>

    @Query("SELECT * FROM conversation_table WHERE id = :id")
    fun getById(id: Long): Conversation

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(conversation: Conversation)

    @Delete
    fun delete(conversation: Conversation)

    @Query("DELETE FROM conversation_table")
    fun deleteAll()

    @Query(
    "SELECT conversation_table.id AS id, " +
        "conversation_table.name AS title " +
        "FROM conversation_table"
    )
    fun getAllItems(): Flow<List<ConversationItem>>
}