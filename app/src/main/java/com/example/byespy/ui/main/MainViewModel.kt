package com.example.byespy.ui.main

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.*
import com.example.byespy.data.datasource.ContactItemDataSource
import com.example.byespy.data.datasource.ConversationItemDataSource
import com.example.byespy.network.Api
import com.example.byespy.network.SessionManager
import com.example.byespy.network.requests.RefreshTokenRequest
import com.example.byespy.network.response.ProfileResponse
import kotlinx.coroutines.launch
import java.lang.Exception

class MainViewModel : ViewModel() {

    private val _profileResponse = MutableLiveData<ProfileResponse>()
    val profileResponse: LiveData<ProfileResponse> = _profileResponse

    val conversationsLiveData = ConversationItemDataSource
        .getConversationItemDataSource()
        .getConversationList()

    val contactsLiveData = ContactItemDataSource
        .getContactItemDataSource()
        .getContactList()

    fun getProfile(context: Context) {
        val sessionManager = SessionManager(context)

        if (sessionManager.fetchAuthToken() == null) {
            refreshToken(context)
        }

        viewModelScope.launch {
            try {
                val response = Api.getApiService(context).getProfile()
                _profileResponse.value = response
            } catch (e: Exception) {
                _profileResponse.value = ProfileResponse(-1, "Error while loading email.")
            }
        }
    }

    fun refreshToken(context: Context) {
        viewModelScope.launch {
            try {
                val sessionManager = SessionManager(context)
                val response = Api.getApiService(context).refreshToken(
                    RefreshTokenRequest(sessionManager.fetchAuthToken() ?: "")
                )

                sessionManager.saveAuthToken(response.accessToken)
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

class MainViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}