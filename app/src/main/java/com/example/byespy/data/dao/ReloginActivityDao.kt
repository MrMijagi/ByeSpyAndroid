package com.example.byespy.data.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface ReloginActivityDao {
    @Query("DELETE FROM device_table")
    fun deleteDeviceTable()

    @Query("DELETE FROM message_table")
    fun deleteMessageTable()

    @Query("DELETE FROM contact_table")
    fun deleteContactTable()

    @Query("DELETE FROM conversation_table")
    fun deleteConversationTable()
}