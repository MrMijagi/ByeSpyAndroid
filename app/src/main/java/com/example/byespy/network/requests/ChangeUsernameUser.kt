package com.example.byespy.network.requests

import com.squareup.moshi.Json

data class ChangeUsernameUser(
    @Json(name = "id") val id: Int,
    @Json(name = "email") val email: String,
    @Json(name = "username") val username: String
)
