package com.example.byespy.ui.chat

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.byespy.data.dao.ChatActivityDao
import com.example.byespy.data.entity.Message
import com.example.byespy.data.model.MessageItem
import com.example.byespy.network.Api
import com.example.byespy.network.requests.SaveMessageRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.util.*

class ChatViewModel(
    val conversationId: Long,
    private val chatActivityDao: ChatActivityDao
    ) : ViewModel() {

    fun messages(): Flow<List<MessageItem>> = chatActivityDao.getMessagesByConversationId(conversationId)

    fun getServerIdByConversationId() = chatActivityDao.getServerIdByConversationId(conversationId)

    // add own message to list
    fun insertOwnMessage(content: String) {
        chatActivityDao.insert(Message(
            content = content,
            sentAt = Calendar.getInstance().time,
            isOwnMessage = true,
            conversationId = conversationId,
            threadId = 0
        ))
    }

    // add other message to list
    fun insertOtherMessage(content: String, createdAt: Date) {
        chatActivityDao.insert(Message(
            content = content,
            sentAt = createdAt,
            isOwnMessage = false,
            conversationId = conversationId,
            threadId = 0
        ))
    }
}

class ChatViewModelFactory(
    private val conversationId: Long,
    private val chatActivityDao: ChatActivityDao
    ) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(conversationId, chatActivityDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}