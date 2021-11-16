package com.example.byespy.network.websocket

import com.squareup.moshi.Json
import java.util.*

data class MessageReceived (
    @Json(name = "id") val id: Int,
    @Json(name = "user_from_id") val userFromId: Int,
    @Json(name = "user_to_id") val userToId: Int,
    @Json(name = "message_type") val messageType: String,
    @Json(name = "content") val content: String,
    @Json(name = "sent_at") val createdAt: Date
)
