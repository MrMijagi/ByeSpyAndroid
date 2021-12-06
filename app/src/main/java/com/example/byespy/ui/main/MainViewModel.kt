package com.example.byespy.ui.main

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.*
import com.example.byespy.data.dao.MainActivityDao
import com.example.byespy.data.entity.Contact
import com.example.byespy.data.entity.Conversation
import com.example.byespy.data.model.ConversationItem
import com.example.byespy.network.Api
import com.example.byespy.network.SessionManager
import com.example.byespy.network.requests.InvitationRequest
import com.example.byespy.network.requests.RefreshTokenRequest
import com.example.byespy.network.response.ProfileResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.lang.Exception

class MainViewModel(
    private val mainActivityDao: MainActivityDao
) : ViewModel() {

    private val _profileResponse = MutableLiveData<ProfileResponse>()
    val profileResponse: LiveData<ProfileResponse> = _profileResponse

    private val _sendInvitationLiveData = MutableLiveData<Boolean>()
    val sendInvitationLiveData: LiveData<Boolean> = _sendInvitationLiveData

    fun conversations(): Flow<List<ConversationItem>> = mainActivityDao.getAllItems()

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
                _profileResponse.value = ProfileResponse(-1, "Error while loading email.", "Error while loading username")
            }
        }
    }

    private fun refreshToken(context: Context) {
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

    fun sendInvitation(context: Context, email: String) {
        viewModelScope.launch {
            try {
                val response = Api.getApiService(context).sendInvitation(
                    InvitationRequest(email)
                )

                _sendInvitationLiveData.postValue(response.isSuccessful)
            } catch (e: Exception) {
                _sendInvitationLiveData.postValue(false)
            }
        }
    }
}

class MainViewModelFactory(
    private val mainActivityDao: MainActivityDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(mainActivityDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}