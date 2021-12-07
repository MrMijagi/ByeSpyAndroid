package com.example.byespy.ui.relogin

import android.content.Context
import androidx.lifecycle.*
import com.example.byespy.data.dao.ReloginActivityDao
import com.example.byespy.network.Api
import com.example.byespy.network.SessionManager
import com.example.byespy.network.requests.LoginRequest
import com.example.byespy.network.response.LoginResponse
import com.example.byespy.ui.login.LoginResult
import kotlinx.coroutines.launch
import java.lang.Exception

class ReloginViewModel(
    private val reloginActivityDao: ReloginActivityDao
) : ViewModel() {

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

    fun changeUser(sessionManager: SessionManager) {
        reloginActivityDao.deleteDeviceTable()
        reloginActivityDao.deleteMessageTable()
        reloginActivityDao.deleteContactTable()
        reloginActivityDao.deleteConversationTable()

        sessionManager.logout()
        sessionManager.clearUser()
    }
}

class ReloginViewModelFactory(
    private val reloginActivityDao: ReloginActivityDao
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReloginViewModel::class.java)) {
            return ReloginViewModel(reloginActivityDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}