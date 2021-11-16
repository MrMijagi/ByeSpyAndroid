package com.example.byespy.data.model

import androidx.annotation.DrawableRes

data class ConversationItem(
    val id: Long,
    val title: String,
    val lastMessage: String,
    @DrawableRes val image: Int
)
