package com.example.byespy.network

import android.content.Context
import android.content.SharedPreferences
import com.example.byespy.R

class SessionManager(context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences(
        context.getString(R.string.app_name), Context.MODE_PRIVATE
    )

    companion object {
        const val USER_TOKEN = "user_token"
        const val REFRESH_TOKEN = "refresh_token"
    }

    // save token to shared preferences
    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    // get token from shared preferences
    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    // save refresh token to shared preferences
    fun saveRefreshToken(token: String) {
        val editor = prefs.edit()
        editor.putString(REFRESH_TOKEN, token)
        editor.apply()
    }

    // get refresh token from shared preferences
    fun fetchRefreshToken(): String? {
        return prefs.getString(REFRESH_TOKEN, null)
    }

    // reset keys from prefs
    fun logout() {
        val editor = prefs.edit()
        editor.remove(USER_TOKEN)
        editor.remove(REFRESH_TOKEN)
        editor.apply()
    }
}