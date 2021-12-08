package com.example.byespy.data.dao

import androidx.room.*
import com.example.byespy.data.entity.Contact
import com.example.byespy.data.entity.Device
import com.example.byespy.data.entity.Message
import com.example.byespy.data.model.MessageItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatActivityDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(message: Message)

    @Query("SELECT message_table.id AS id, " +
            "contact_table.email AS otherEmail, " +
            "message_table.content AS content, " +
            "message_table.is_own_message AS ownMessage " +
            "FROM message_table " +
            "INNER JOIN conversation_table ON conversation_table.id = message_table.conversation_id " +
            "INNER JOIN contact_table ON contact_table.id = conversation_table.contact_id " +
            "WHERE message_table.conversation_id = :id")
    fun getMessagesByConversationId(id: Long): Flow<List<MessageItem>>

    @Query("SELECT contact_table.server_id " +
            "FROM conversation_table " +
            "INNER JOIN contact_table ON contact_table.id = conversation_table.contact_id " +
            "WHERE conversation_table.id = :id")
    fun getServerIdByConversationId(id: Long): Long

    @Query("SELECT contact_table.id " +
            "FROM conversation_table " +
            "INNER JOIN contact_table ON contact_table.id = conversation_table.contact_id " +
            "WHERE conversation_table.id = :id")
    fun getContactByByConversationId(id: Long): Long

    @Query("SELECT device_table.device_id " +
            "FROM device_table " +
            "INNER JOIN conversation_table ON conversation_table.contact_id = device_table.contact_id " +
            "WHERE conversation_table.id = :id")
    fun getDevicesByConversationId(id: Long): List<Long>

    @Insert
    fun addDevice(device: Device): Long

    @Delete
    fun deleteDevice(device: Device): Int
}