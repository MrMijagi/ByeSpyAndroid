package com.example.byespy.network.response

import com.squareup.moshi.Json

data class AvatarResponse(
    @Json(name = "data") val data: String
)
