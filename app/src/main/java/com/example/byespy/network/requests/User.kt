package com.example.byespy.network.requests

import com.squareup.moshi.Json

data class User(
    @Json(name = "email") val email: String,
    @Json(name = "username") val username: String,
    @Json(name = "password") val password: String
)
