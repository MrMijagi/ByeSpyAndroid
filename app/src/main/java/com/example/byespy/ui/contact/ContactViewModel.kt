package com.example.byespy.ui.contact

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.byespy.data.dao.ContactActivityDao
import com.example.byespy.data.entity.Contact
import com.example.byespy.network.Api
import com.example.byespy.network.response.ProfileResponse
import kotlinx.coroutines.launch
import java.lang.Exception
import java.lang.IllegalArgumentException

class ContactViewModel(
    private val conversationId: Long,
    private val contactActivityDao: ContactActivityDao
    ) : ViewModel() {

    fun updateImage(context: Context) {
        viewModelScope.launch {
            try {
                val serverId = contactActivityDao.getServerId(conversationId)
                val contactId = contactActivityDao.getId(conversationId)
                val response = Api.getApiService(context).getAvatar(serverId)
                val image = response.body()?.substring((response.body()?.indexOf(',') ?: -1) + 1)

                contactActivityDao.updateImage(image, contactId)
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "updateImage fail",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun getContact(): Contact {
        return contactActivityDao.getContact(conversationId)
    }

    fun clearMessages() {
        Log.d("CONTACT", conversationId.toString())
        contactActivityDao.clearMessages(conversationId)
    }

    fun deleteContact() {
        contactActivityDao.deleteContact(conversationId)
        contactActivityDao.deleteConversation(conversationId)
        contactActivityDao.clearMessages(conversationId)
    }
}

class ContactViewModelFactory(
    private val conversationId: Long,
    private val contactActivityDao: ContactActivityDao
    ) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContactViewModel(conversationId, contactActivityDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}