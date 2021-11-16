package com.example.byespy.network.websocket

import com.squareup.moshi.Json
import java.util.*

data class Message(
    @Json(name = "type") val type: String,
    @Json(name = "content") val content: String,
    @Json(name = "mail_to") val senderEmail: String,
    @Json(name = "created_at") val createdAt: Date
)
