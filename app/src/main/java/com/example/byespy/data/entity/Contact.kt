package com.example.byespy.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contact_table")
data class Contact(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "profile_resource") val profileResource: Int
)
