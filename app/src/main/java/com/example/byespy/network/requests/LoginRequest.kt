package com.example.byespy.network.requests

import com.squareup.moshi.Json

class LoginRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String,
    @Json(name = "authentication_code") val authenticationCode: String,
    @Json(name = "grant_type") val grantType: String
)
