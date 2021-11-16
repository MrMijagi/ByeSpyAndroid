package com.example.byespy.network.response

import com.squareup.moshi.Json

data class AuthorizeUserResponse (
    @Json(name = "user_exists") val userExists: Boolean
)