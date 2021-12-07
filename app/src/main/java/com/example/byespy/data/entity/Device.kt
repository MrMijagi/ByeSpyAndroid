package com.example.byespy.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "device_table")
data class Device(
    @ColumnInfo(name = "device_id")
    val deviceId: Long,
    @ColumnInfo(name = "contact_id")
    val contactId: Long
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
