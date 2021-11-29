package com.example.byespy.ui.contact

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.byespy.data.dao.ContactActivityDao
import java.lang.IllegalArgumentException

class ContactViewModel(
    private val conversationId: Long,
    private val contactActivityDao: ContactActivityDao
    ) : ViewModel() {

    fun getEmail(): String {
        return contactActivityDao.getEmail(conversationId)
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