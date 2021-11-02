package com.example.byespy.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.byespy.network.Api
import com.example.byespy.network.LoginRequest
import com.example.byespy.network.LoginResponse
import kotlinx.coroutines.launch
import java.lang.Exception

class LoginViewModel() : ViewModel() {

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                val loginResponse = Api.retrofitService.signIn(LoginRequest(username, password, "password"))
                _loginResult.value = LoginResult(success = loginResponse)
            } catch (e: Exception) {
                _loginResult.value = LoginResult(error = 1, success =  LoginResponse(e.message?: "", ""))
            }
         }
    }
}