package com.example.byespy.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contact_table")
data class Contact(
    @ColumnInfo(name = "server_id")
    val serverId: Long,
    val username: String?,
    val email: String,
//    val image: String?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
