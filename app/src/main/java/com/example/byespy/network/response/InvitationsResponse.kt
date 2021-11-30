package com.example.byespy.network.response

import com.squareup.moshi.Json

data class InvitationsResponse(
    @Json(name = "sent") val sent: List<InvitationSent>,
    @Json(name = "received") val received: List<InvitationReceived>
)
