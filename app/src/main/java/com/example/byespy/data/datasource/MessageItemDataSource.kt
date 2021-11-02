package com.example.byespy.data.datasource

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.byespy.data.model.MessageItem

class MessageItemDataSource {
    private val initialMessageList = listOf(
        MessageItem(0, "dawid@gmail.com", "Hi"),
        MessageItem(1, "piotr@gmail.com", "Hello"),
        MessageItem(2, "piotr@gmail.com", "How are you?"),
        MessageItem(3, "dawid@gmail.com", "I'm fine")
    )

    private val messagesLiveData = MutableLiveData(initialMessageList)
    private var idCounter: Long = 4

    fun addMessage(message: MessageItem) {
        val currentList = messagesLiveData.value
        if (currentList == null) {
            messagesLiveData.postValue(listOf(message))
        } else {
            val updatedList = currentList.toMutableList()
            updatedList.add(message)
            messagesLiveData.postValue(updatedList)
        }
    }

    fun getMessageList(): LiveData<List<MessageItem>> {
        return messagesLiveData
    }

    fun getNewId(): Long {
        return idCounter++
    }

    fun getSize(): Int {
        return idCounter.toInt() - 1
    }

    companion object {
        private var INSTANCE: MessageItemDataSource? = null

        fun getMessageItemDataSource(): MessageItemDataSource {
            return synchronized(MessageItemDataSource::class) {
                val newInstance = INSTANCE ?: MessageItemDataSource()
                INSTANCE = newInstance
                newInstance
            }
        }
    }
}