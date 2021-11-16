package com.example.byespy.network.response

import com.squareup.moshi.Json

data class ProfileResponse(
    @Json(name = "id") val id: Int,
    @Json(name = "email") val email: String
)
