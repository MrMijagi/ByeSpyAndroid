package com.example.byespy.login

import com.example.byespy.network.LoginResponse

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginResult(
        val success: LoginResponse? = null,
        val error: Int? = null
)