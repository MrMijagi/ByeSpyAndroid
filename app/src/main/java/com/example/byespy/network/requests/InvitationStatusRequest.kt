package com.example.byespy.network.requests

import com.squareup.moshi.Json

data class InvitationStatusRequest(
    @Json(name = "status") val status: String
)
