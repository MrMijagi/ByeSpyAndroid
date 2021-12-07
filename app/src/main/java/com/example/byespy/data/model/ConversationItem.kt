package com.example.byespy.data.model

data class ConversationItem(
    val id: Long,
    val email: String,
    val username: String?,
    val lastMessage: String?,
    val lastMessageOwn: Boolean?,
    val image: String?
)
