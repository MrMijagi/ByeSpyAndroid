package com.example.byespy.data.dao

import androidx.room.*
import com.example.byespy.data.entity.Contact

@Dao
interface ContactActivityDao {
    @Query("SELECT contact_table.id AS id, " +
            "contact_table.server_id AS server_id, " +
            "contact_table.username AS username, " +
            "contact_table.email AS email, " +
            "contact_table.image AS image " +
            "FROM conversation_table " +
            "INNER JOIN contact_table ON contact_table.id = conversation_table.contact_id " +
            "WHERE conversation_table.id = :conversationId")
    fun getContact(conversationId: Long): Contact

    @Query("SELECT contact_table.id " +
            "FROM conversation_table " +
            "INNER JOIN contact_table ON contact_table.id = conversation_table.contact_id " +
            "WHERE conversation_table.id = :conversationId")
    fun getId(conversationId: Long): Long

    @Query("DELETE FROM message_table WHERE conversation_id = :conversationId")
    fun clearMessages(conversationId: Long)

    @Query("DELETE FROM conversation_table WHERE id = :conversationId")
    fun deleteConversation(conversationId: Long)

    @Query("DELETE FROM contact_table " +
            "WHERE ROWID in " +
            "(SELECT contact_table.ROWID " +
            "FROM contact_table " +
            "INNER JOIN conversation_table " +
            "ON contact_table.id = conversation_table.contact_id " +
            "WHERE conversation_table.id = :conversationId)")
    fun deleteContact(conversationId: Long)

    @Query("UPDATE contact_table SET image = :image WHERE id = :id")
    fun updateImage(image: String?, id: Long)
}