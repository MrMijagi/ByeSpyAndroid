package com.example.byespy.network.requests

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

data class SaveMessageRequest(
    @Json(name = "type") val type: String,
    @Json(name = "content") val content: String,
    @Json(name = "mail_to") val mailTo: String,
    @Json(name = "sent_at") val sendAt: Date
)