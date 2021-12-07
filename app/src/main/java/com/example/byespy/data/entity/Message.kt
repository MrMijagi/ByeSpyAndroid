package com.example.byespy.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "message_table")
data class Message(
    val content: String,
    @ColumnInfo(name = "sent_at")
    val sentAt: Date,
    @ColumnInfo(name = "is_own_message")
    val isOwnMessage: Boolean,
    @ColumnInfo(name = "conversation_id")
    val conversationId: Long
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
