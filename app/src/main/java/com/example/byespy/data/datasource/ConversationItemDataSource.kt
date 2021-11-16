package com.example.byespy.data.datasource

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.byespy.R
import com.example.byespy.data.model.ConversationItem

class ConversationItemDataSource {
    private val initialConversationList = listOf(
        ConversationItem(0, "Michael", "That's what she said", R.drawable.photo10),
        ConversationItem(1, "Mice", "How many roads must a man walk down?", R.drawable.photo11),
        ConversationItem(2, "Mom", "Dinner is ready", R.drawable.photo12),
        ConversationItem(3, "ZPI", "We can make it in time", R.drawable.photo13),
    )

    private val conversationsLiveData = MutableLiveData(initialConversationList)

    fun addConversation(conversation: ConversationItem) {
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