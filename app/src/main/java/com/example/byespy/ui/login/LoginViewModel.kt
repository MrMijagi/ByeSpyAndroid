package com.example.byespy.ui.login

import android.content.Context
import androidx.lifecycle.*

import com.example.byespy.network.Api
import com.example.byespy.network.requests.LoginRequest
import com.example.byespy.network.response.LoginResponse
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
                if (response.isSuccessful) {
                    _authorizationResult.value = response.isSuccessful
                } else {
                    _authorizationResult.value = false
                }
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