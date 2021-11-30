package com.example.byespy.network.response

import com.squareup.moshi.Json
import java.util.*

data class InvitationSent(
    @Json(name = "id") val id: Int,
    @Json(name = "created_at") val createdAt: Date,
    @Json(name = "status") val status: String,
    @Json(name = "invitee") val invitee: InvitationUser
)