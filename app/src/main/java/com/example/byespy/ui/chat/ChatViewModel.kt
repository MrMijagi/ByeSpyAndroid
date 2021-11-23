package com.example.byespy.ui.chat

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.byespy.data.dao.MessageDao
import com.example.byespy.data.entity.Message
import com.example.byespy.data.model.MessageItem
import com.example.byespy.network.Api
import com.example.byespy.network.requests.SaveMessageRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.util.*

class ChatViewModel(private val conversationId: Long,
                    private val messageDao: MessageDao) : ViewModel() {

    fun messages(): Flow<List<MessageItem>> = messageDao.getMessagesByConversationId(conversationId)

    fun getEmailFromConversationId() = messageDao.getEmailByConversationId(conversationId)

    // add own message to list
    fun insertOwnMessage(content: String) {
        messageDao.insert(Message(
            content = content,
            sentAt = Calendar.getInstance().time,
            isOwnMessage = true,
            conversationId = conversationId,
            threadId = 0
        ))
    }

    // add other message to list
    fun insertOtherMessage(content: String, createdAt: Date) {
        messageDao.insert(Message(
            content = content,
            sentAt = createdAt,
            isOwnMessage = false,
            conversationId = conversationId,
            threadId = 0
        ))
    }

    // send request to server
    fun sendMessage(context: Context, content: String, mailTo: String) {
        val message = SaveMessageRequest(
            type = "type",
            content = content,
            receiverId = 2,
            sendAt = Calendar.getInstance().time
        )

        viewModelScope.launch {
            try {
                val response = Api.getApiService(context).saveMessage(message)
                Log.d("chat", response.isSuccessful.toString())
            } catch (e: Exception) {
                Log.e("chat", "error on saveMessage")
                Log.e("chat", e.toString())
            }
         }
    }
}

class ChatViewModelFactory(private val conversationId: Long,
                           private val messageDao: MessageDao) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(conversationId, messageDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}