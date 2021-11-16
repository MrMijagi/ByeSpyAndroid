package com.example.byespy.ui.register

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.*
import com.example.byespy.network.Api
import com.example.byespy.network.requests.RegistrationRequest
import com.example.byespy.network.requests.User
import com.example.byespy.ui.login.LoginViewModel
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val _signUpResult = MutableLiveData<Boolean>()
    val signUpResult: LiveData<Boolean> = _signUpResult

    fun signUp(context: Context, username: String, password: String) {
        viewModelScope.launch {
            try {
                val response = Api.getApiService(context).signUp(
                    RegistrationRequest(
                        User(username, password)
                    )
                )

                _signUpResult.value = response.isSuccessful
            } catch (e: Exception) {
                _signUpResult.value = false

                // make toast text
                Toast.makeText(
                    context,
                    e.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}

class RegisterViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}