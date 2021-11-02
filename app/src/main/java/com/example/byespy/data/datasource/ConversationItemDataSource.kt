package com.example.byespy.data.datasource

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.byespy.data.model.ConversationItem

class ConversationItemDataSource {
    private val initialConversationList = listOf(
        ConversationItem(0, "Michael", "That's what she said"),
        ConversationItem(1, "Mice", "How many roads must a man walk down?"),
        ConversationItem(2, "Mom", "Dinner is ready"),
        ConversationItem(3, "ZPI", "We can make it in time"),
    )

    private val conversationsLiveData = MutableLiveData(initialConversationList)

    fun addFlower(conversation: ConversationItem) {
        val currentList = conversationsLiveData.value
        if (currentList == null) {
            conversationsLiveData.postValue(listOf(conversation))
        } else {
            val updatedList = currentList.toMutableList()
            updatedList.add(0, conversation)
            conversationsLiveData.postValue(updatedList)
        }
    }

    fun removeConversation(conversation: ConversationItem) {
        val currentList = conversationsLiveData.value
        if (currentList != null) {
            val updatedList = currentList.toMutableList()
            updatedList.remove(conversation)
            conversationsLiveData.postValue(updatedList)
        }
    }

    fun getConversationList(): LiveData<List<ConversationItem>> {
        return conversationsLiveData
    }

    companion object {
        private var INSTANCE: ConversationItemDataSource? = null

        fun getConversationItemDataSource(): ConversationItemDataSource {
            return synchronized(ConversationItemDataSource::class) {
                val newInstance = INSTANCE ?: ConversationItemDataSource()
                INSTANCE = newInstance
                newInstance
            }
        }
    }
}