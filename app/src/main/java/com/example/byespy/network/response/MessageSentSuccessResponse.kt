package com.example.byespy.network.response

import com.squareup.moshi.Json

data class MessageSentSuccessResponse(
        @Json(name = "message_id") val messageId: Int
    )