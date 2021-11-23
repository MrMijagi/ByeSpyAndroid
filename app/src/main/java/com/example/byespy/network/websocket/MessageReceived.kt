package com.example.byespy.network.websocket

import com.squareup.moshi.Json
import java.util.*

data class MessageReceived (
    @Json(name = "id") val id: Int,
    @Json(name = "sender_id") val userFromId: Int,
    @Json(name = "receiver_id") val userToId: Int,
    @Json(name = "message_type") val messageType: String,
    @Json(name = "content") val content: String,
    @Json(name = "sent_at") val createdAt: Date
)
