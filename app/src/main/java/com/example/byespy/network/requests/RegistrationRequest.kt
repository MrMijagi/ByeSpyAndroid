package com.example.byespy.network.requests

import com.squareup.moshi.Json

data class RegistrationRequest(
    @Json(name = "user") val user: User,
)
