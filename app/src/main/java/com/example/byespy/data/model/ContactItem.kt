package com.example.byespy.data.model

import androidx.annotation.DrawableRes

data class ContactItem(
    val id: Long,
    val email: String,
    @DrawableRes val image: Int
)
