package com.example.byespy.data.model

data class MessageItem(
    val id: Long,
    val otherEmail: String,
    val content: String,
    val ownMessage: Boolean
)
