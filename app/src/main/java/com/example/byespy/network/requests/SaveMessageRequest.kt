package com.example.byespy.network.requests

import com.squareup.moshi.Json
import java.util.*

data class SaveMessageRequest(
    @Json(name = "sender_device_id") val senderDeviceId: Int,
    @Json(name = "receiver_user_id") val receiverUserId: Int,
    @Json(name = "messages") val messageToSaves: List<MessageToSave>
)

data class MessageToSave(
    @Json(name = "receiver_device_id") val deviceId: Int,
    @Json(name = "type") val type: String,
    @Json(name = "sent_at") val sentAt: Date,
    @Json(name = "content") val content: String
)

data class CiphertextMessage(
    @Json(name = "type") val type: Int,
    @Json(name = "body") val body: String,
    @Json(name = "registrationId") val registrationId: Int
)