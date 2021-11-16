package com.example.byespy.network.response

import com.squareup.moshi.Json

data class LoginResponse (
    @Json(name = "access_token") val accessToken: String,
    @Json(name = "refresh_token") val refreshToken: String
)