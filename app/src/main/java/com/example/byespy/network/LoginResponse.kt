package com.example.byespy.network

import com.squareup.moshi.Json

data class LoginResponse (
    @Json(name = "access_token") val accessToken: String,
    @Json(name = "refresh_token") val refreshToken: String
)