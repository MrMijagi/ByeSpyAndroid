package com.example.byespy.network.requests

import com.squareup.moshi.Json

data class RefreshTokenRequest(
    @Json(name = "access_token") val accessToken: String,
)
