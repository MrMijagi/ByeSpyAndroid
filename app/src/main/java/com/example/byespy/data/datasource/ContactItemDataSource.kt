package com.example.byespy.data.datasource

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.byespy.R
import com.example.byespy.data.model.ContactItem

class ContactItemDataSource {
    private val initialContactList = listOf(
        ContactItem(0, "dawid@gmail.com", R.drawable.photo0),
        ContactItem(1, "jarek@gmail.com", R.drawable.photo1),
        ContactItem(2, "norbert@gmail.com", R.drawable.photo2),
        ContactItem(3, "john@gmail.com", R.drawable.photo3),
        ContactItem(4, "doe@gmail.com", R.drawable.photo4),
    )

    private val contactsLiveData = MutableLiveData(initialContactList)

    fun addContact(contact: ContactItem) {
        val currentList = contactsLiveData.value
        if (currentList == null) {
            contactsLiveData.postValue(listOf(contact))
        } else {
            val updatedList = currentList.toMutableList()
            updatedList.add(0, contact)
            contactsLiveData.postValue(updatedList)
        }
    }

    fun removeContact(contact: ContactItem) {
        val currentList = contactsLiveData.value
        if (currentList != null) {
            val updatedList = currentList.toMutableList()
            updatedList.remove(contact)
            contactsLiveData.postValue(updatedList)
        }
    }

    fun getContactList(): LiveData<List<ContactItem>> {
        return contactsLiveData
    }

    companion object {
        private var INSTANCE: ContactItemDataSource? = null

        fun getContactItemDataSource(): ContactItemDataSource {
            return synchronized(ContactItemDataSource::class) {
                val newInstance = INSTANCE ?: ContactItemDataSource()
                INSTANCE = newInstance
                newInstance
            }
        }
    }
}