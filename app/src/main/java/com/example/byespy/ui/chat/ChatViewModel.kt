package com.example.byespy.ui.chat

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.byespy.data.datasource.MessageItemDataSource
import com.example.byespy.data.model.MessageItem
import com.example.byespy.network.Api
import com.example.byespy.network.CustomDateAdapter
import com.example.byespy.network.requests.SaveMessageRequest
import com.example.byespy.network.websocket.Message
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.util.*

class ChatViewModel(val messageItemDataSource: MessageItemDataSource) : ViewModel() {

    val messagesLiveData = messageItemDataSource.getMessageList()

    // add own message to list
    fun insertOwnMessage(content: String) {
        val message = MessageItem(
            messageItemDataSource.getNewId(),
            "dawid@gmail.com",
            content
        )
        MessageItemDataSource.getMessageItemDataSource().addMessage(message)
    }

    // add other message to list
    fun insertOtherMessage(content: String, from: String) {
        val message = MessageItem(
            messageItemDataSource.getNewId(),
            from,
            content
        )
        MessageItemDataSource.getMessageItemDataSource().addMessage(message)
    }

    // send request to server
    fun sendMessage(context: Context, content: String) {
        val message = SaveMessageRequest(
            type = "type",
            content = content,
            mailTo = "tabaluga@mm.pl",
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


    fun getMessagesSize(): Int {
        return messageItemDataSource.getSize()
    }
}

class ChatViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(
                messageItemDataSource = MessageItemDataSource.getMessageItemDataSource()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}