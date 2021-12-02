package com.example.byespy.network.requests

import com.squareup.moshi.Json

data class ChangePasswordRequest(
    @Json(name = "user") val user: ChangePasswordUser
)
