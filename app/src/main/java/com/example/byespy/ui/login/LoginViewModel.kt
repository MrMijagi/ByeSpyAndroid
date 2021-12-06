package com.example.byespy.ui.login

import android.content.Context
import android.se.omapi.Session
import android.widget.Toast
import androidx.lifecycle.*

import com.example.byespy.network.Api
import com.example.byespy.network.SessionManager
import com.example.byespy.network.requests.LoginRequest
import com.example.byespy.network.response.LoginResponse
import com.example.byespy.network.response.ProfileResponse
import kotlinx.coroutines.launch
import java.lang.Exception

class LoginViewModel : ViewModel() {

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _authorizationResult = MutableLiveData<Boolean>()
    val authorizationResult: LiveData<Boolean> = _authorizationResult

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    fun authorizeUser(context: Context, username: String, password: String) {
        viewModelScope.launch {
            try {
                val response = Api.getApiService(context).authorizeUser(username, password)

                _authorizationResult.value = response.userExists
            } catch (e: Exception) {
                _toastMessage.value = e.message
            }
        }
    }

    fun login(context: Context, username: String, password: String, authCode: String) {
        //_loginResult.value = LoginResult(success = LoginResponse("token", "refresh_token"))

        viewModelScope.launch {
            try {
                val loginResponse = Api.getApiService(context).signIn(LoginRequest(username, password, authCode, "password"))
                _loginResult.value = LoginResult(success = loginResponse)
            } catch (e: Exception) {
                _loginResult.value = LoginResult(error = 1, success =  LoginResponse(e.message?: "", ""))
            }
         }
    }

    // no need to check for token since this is called after user logs in
    fun saveProfile(context: Context) {
        viewModelScope.launch {
            try {
                val response = Api.getApiService(context).getProfile()

                val sessionManager = SessionManager(context)
                sessionManager.saveUserId(response.id)
                sessionManager.saveUserEmail(response.email)
                sessionManager.saveDeviceId(1)         // for now
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Couldn't save profile data!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}

class LoginViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}