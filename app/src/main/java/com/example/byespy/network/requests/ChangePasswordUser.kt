package com.example.byespy.network.requests

import com.squareup.moshi.Json

data class ChangePasswordUser(
    @Json(name = "id") val id: Int,
    @Json(name = "email") val email: String,
    @Json(name = "change_password") val changePassword: Boolean,
    @Json(name = "current_password") val currentPassword: String,
    @Json(name = "password") val password: String,
    @Json(name = "password_confirm") val passwordConfirm: String
)
