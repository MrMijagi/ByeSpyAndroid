package com.example.byespy.data.dao

import androidx.room.*

@Dao
interface ContactActivityDao {
    @Query("SELECT contact_table.email " +
            "FROM conversation_table " +
            "INNER JOIN contact_table ON contact_table.id = conversation_table.contact_id " +
            "WHERE conversation_table.id = :conversationId")
    fun getEmail(conversationId: Long): String

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
}