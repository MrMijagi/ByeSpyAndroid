package com.example.byespy.data.dao

import androidx.room.*
import com.example.byespy.data.entity.Contact
import com.example.byespy.data.entity.Message
import com.example.byespy.data.model.MessageItem
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM message_table")
    fun getAll(): Flow<List<Message>>

    @Query("SELECT * FROM message_table WHERE id = :id")
    fun getById(id: Long): Message

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(message: Message)

    @Delete
    fun delete(message: Message)

    @Query("DELETE FROM message_table")
    fun deleteAll()

    @Query("SELECT message_table.id AS id, " +
            "contact_table.email AS otherEmail, " +
            "message_table.content AS content, " +
            "message_table.is_own_message AS ownMessage " +
            "FROM message_table " +
            "INNER JOIN conversation_table ON conversation_table.id = message_table.conversation_id " +
            "INNER JOIN contact_table ON contact_table.id = conversation_table.contact_id " +
            "WHERE message_table.conversation_id = :id")
    fun getMessagesByConversationId(id: Long): Flow<List<MessageItem>>

    @Query("SELECT contact_table.email " +
            "FROM conversation_table " +
            "INNER JOIN contact_table ON contact_table.id = conversation_table.contact_id " +
            "WHERE conversation_table.id = :id")
    fun getEmailByConversationId(id: Long): String
}