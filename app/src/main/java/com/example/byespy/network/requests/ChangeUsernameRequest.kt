package com.example.byespy.network.requests

import com.squareup.moshi.Json

data class ChangeUsernameRequest(
    @Json(name = "user") val user: ChangeUsernameUser
)
