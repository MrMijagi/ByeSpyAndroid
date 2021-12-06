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
        const val USER_ID = "user_id"
        const val USER_EMAIL = "user_email"
        const val DEVICE_ID = "device_id"
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

    // save server user id
    fun saveUserId(id: Int) {
        val editor = prefs.edit()
        editor.putInt(USER_ID, id)
        editor.apply()
    }

    // get server user id - returns -1 if not saved (not registered)
    fun fetchUserId(): Int {
        return prefs.getInt(USER_ID, -1)
    }

    // save user email
    fun saveUserEmail(email: String) {
        val editor = prefs.edit()
        editor.putString(USER_EMAIL, email)
        editor.apply()
    }

    // get user email
    fun fetchUserEmail(): String? {
        return prefs.getString(USER_EMAIL, null)
    }

    // save device id
    fun saveDeviceId(id: Int) {
        val editor = prefs.edit()
        editor.putInt(DEVICE_ID, id)
        editor.apply()
    }

    // get device id - returns -1 if not saved
    fun fetchDeviceId(): Int {
        return prefs.getInt(DEVICE_ID, -1)
    }

    // reset keys from prefs
    fun logout() {
        val editor = prefs.edit()
        editor.remove(USER_TOKEN)
        editor.remove(REFRESH_TOKEN)
        editor.apply()
    }
}