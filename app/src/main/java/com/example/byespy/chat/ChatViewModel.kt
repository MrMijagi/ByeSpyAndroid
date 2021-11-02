package com.example.byespy.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.byespy.data.datasource.MessageItemDataSource
import com.example.byespy.data.model.MessageItem
import java.lang.IllegalArgumentException

class ChatViewModel(val messageItemDataSource: MessageItemDataSource) : ViewModel() {

    val messagesLiveData = messageItemDataSource.getMessageList()

    // add message to list
    fun insertOwnMessage(content: String) {
        val message = MessageItem(
            messageItemDataSource.getNewId(),
            "dawid@gmail.com",
            content
        )
        MessageItemDataSource.getMessageItemDataSource().addMessage(message)
    }

    fun getMessagesSize(): Int {
        return messageItemDataSource.getSize()
    }
}

class ChatViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(
                messageItemDataSource = MessageItemDataSource.getMessageItemDataSource()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}