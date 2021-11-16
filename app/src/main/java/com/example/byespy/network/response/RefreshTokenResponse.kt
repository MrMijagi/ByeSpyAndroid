package com.example.byespy.network.response

import com.squareup.moshi.Json

data class RefreshTokenResponse(
    @Json(name = "access_token") val accessToken: String
)
