package com.example.byespy.ui.login

import com.example.byespy.network.response.LoginResponse

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginResult(
    val success: LoginResponse? = null,
    val error: Int? = null
)