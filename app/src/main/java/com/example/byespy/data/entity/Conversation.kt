package com.example.byespy.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversation_table")
data class Conversation(
    @ColumnInfo(name = "contact_id")
    val contactId: Long,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
