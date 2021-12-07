package com.example.byespy.libsignal

import com.squareup.moshi.Json
import java.util.*

data class ReceivedMessage(
    @Json(name = "id") val id: Int,
    @Json(name = "content") val content: String,
    @Json(name = "message_type") val messageType: String,
    @Json(name = "sent_at") val sentAt: Date,
    @Json(name = "sender") val sender: MessageUser,
    @Json(name = "receiver") val receiver: MessageUser
)

data class MessageUser(
    @Json(name = "user_id") val userId: Int,
    @Json(name = "device_id") val deviceId: Int
)