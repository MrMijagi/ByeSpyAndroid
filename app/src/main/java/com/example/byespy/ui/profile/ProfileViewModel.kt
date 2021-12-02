package com.example.byespy.ui.profile

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.*
import com.example.byespy.network.Api
import com.example.byespy.network.requests.ChangePasswordRequest
import com.example.byespy.network.requests.ChangePasswordUser
import com.example.byespy.network.requests.ChangeUsernameRequest
import com.example.byespy.network.requests.ChangeUsernameUser
import com.example.byespy.network.response.ProfileResponse
import kotlinx.coroutines.launch
import java.lang.Exception

class ProfileViewModel : ViewModel() {

    private val _profileResponse = MutableLiveData<ProfileResponse>()

    fun getProfile(context: Context) {
        viewModelScope.launch {
            try {
                val response = Api.getApiService(context).getProfile()
                _profileResponse.value = response
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    e.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun saveUsername(context: Context, username: String) {
        viewModelScope.launch {
            try {
                val response = Api.getApiService(context).editUsername(ChangeUsernameRequest(
                    user = ChangeUsernameUser(
                        id = _profileResponse.value?.id ?: -1,
                        email = _profileResponse.value?.email ?: "",
                        username = username
                    )
                ))

                val message = if (response.code() == 200) {
                    "Username saved"
                } else {
                    "Username wasn't saved"
                }

                Toast.makeText(
                    context,
                    message,
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    e.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun savePassword(context: Context, currentPassword: String, password: String, passwordConfirm: String) {
        viewModelScope.launch {
            try {
                // first validate password
                val response = Api.getApiService(context).validatePassword(
                    _profileResponse.value?.id ?: -1,
                    currentPassword
                )

                if (response.body() == true) {       // not tautology since body() can return null
                    // password validated, change password now
                    val profileResponse = Api.getApiService(context).editPassword(ChangePasswordRequest(
                        user = ChangePasswordUser(
                            id = _profileResponse.value?.id ?: -1,
                            email = _profileResponse.value?.email ?: "",
                            currentPassword = currentPassword,
                            password = password,
                            passwordConfirm = passwordConfirm,
                            changePassword = true
                        ))
                    )

                    val message = if (profileResponse.code() == 200) {
                        "Password saved"
                    } else {
                        "Password wasn't saved"
                    }

                    Toast.makeText(
                        context,
                        message,
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        "Current password incorrect",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    e.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}

class ProfileViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}