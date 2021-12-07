package com.example.byespy.ui.start

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.*
import com.example.byespy.network.Api
import com.example.byespy.network.SessionManager
import com.example.byespy.network.requests.RefreshTokenRequest
import com.example.byespy.network.response.RefreshTokenResponse
import kotlinx.coroutines.launch
import java.lang.Exception

class StartViewModel : ViewModel() {

    private val _refreshTokenResponse = MutableLiveData<RefreshTokenResponse>()
    val refreshTokenResponse: LiveData<RefreshTokenResponse> = _refreshTokenResponse

    fun refreshToken(context: Context) {
        viewModelScope.launch {
            try {
                val sessionManager = SessionManager(context)
                val response = Api.getApiService(context).refreshToken(
                    RefreshTokenRequest(sessionManager.fetchAuthToken() ?: "")
                )

                sessionManager.saveAuthToken(response.accessToken)
                _refreshTokenResponse.value = response
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    e.message,
                    Toast.LENGTH_LONG
                ).show()
                _refreshTokenResponse.value = RefreshTokenResponse("")
            }
        }
    }
}

class StartViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StartViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}