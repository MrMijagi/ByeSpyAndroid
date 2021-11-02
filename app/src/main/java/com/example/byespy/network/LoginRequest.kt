package com.example.byespy.network

import com.squareup.moshi.Json

class LoginRequest(
    val email: String,
    val password: String,
    @Json(name = "grant_type") val grantType: String
)
