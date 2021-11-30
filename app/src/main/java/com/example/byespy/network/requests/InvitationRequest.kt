package com.example.byespy.network.requests

import com.squareup.moshi.Json

data class InvitationRequest(
    @Json(name = "invitee_email") val inviteeEmail: String
)
