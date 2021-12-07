package com.example.byespy.ui.invitations

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.*
import com.example.byespy.data.dao.MainActivityDao
import com.example.byespy.data.entity.Contact
import com.example.byespy.data.entity.Conversation
import com.example.byespy.data.entity.Message
import com.example.byespy.data.model.InvitationItem
import com.example.byespy.network.Api
import com.example.byespy.network.requests.InvitationStatusRequest
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class InvitationsViewModel(
    private val mainActivityDao: MainActivityDao
) : ViewModel() {

    private val _sentInvitationsLiveData = MutableLiveData<MutableList<InvitationItem>>()
    val sentInvitationsLiveData = _sentInvitationsLiveData

    private val _receivedInvitationsLiveData = MutableLiveData<MutableList<InvitationItem>>()
    val receivedInvitationLiveData = _receivedInvitationsLiveData

    init {
        _sentInvitationsLiveData.value = ArrayList()
        _receivedInvitationsLiveData.value = ArrayList()
    }

    fun getInvitations(context: Context) {
        _sentInvitationsLiveData.value = ArrayList()
        _receivedInvitationsLiveData.value = ArrayList()

        viewModelScope.launch {
            try {
                val response = Api.getApiService(context).getInvitations()

                for (invitation in response.sent) {
                    when (invitation.status) {
                        "accepted" -> {
                            // add new conversation
                            addContact(Contact(
                                serverId = invitation.invitee.id.toLong(),
                                email = invitation.invitee.email,
                                username = null
                            ))

                            // delete invitation from server
                            Api.getApiService(context).cancelInvitation(
                                invitation.id
                            )
                        }
                        "rejected" -> {
                            // delete invitation from server
                            Api.getApiService(context).cancelInvitation(
                                invitation.id
                            )
                        }
                    }

                    _sentInvitationsLiveData.value?.add(InvitationItem(
                        invitation.id,
                        0,
                        invitation.invitee.email,
                        invitation.status
                    ))
                }

                // notify recyclerview
                _sentInvitationsLiveData.value = _sentInvitationsLiveData.value

                for (invitation in response.received) {
                    _receivedInvitationsLiveData.value?.add(InvitationItem(
                        invitation.id,
                        invitation.inviter.id,
                        invitation.inviter.email,
                        invitation.status
                    ))
                }

                // notify recyclerview
                _receivedInvitationsLiveData.value = _receivedInvitationsLiveData.value
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    e.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun acceptInvitation(context: Context, id: Int, userId: Int, email: String) {
        viewModelScope.launch {
            try {
                val response = Api.getApiService(context)
                    .acceptOrRejectInvitation(id, InvitationStatusRequest("accepted"))

                if (response.code() != 204) {
                    Toast.makeText(
                        context,
                        "Failed to accept invitation",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    addContact(Contact(
                        serverId = userId.toLong(),
                        email = email,
                        username = null
                    ))
                }
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    e.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        getInvitations(context)
    }

    fun rejectInvitation(context: Context, id: Int) {
        viewModelScope.launch {
            try {
                val response = Api.getApiService(context)
                    .acceptOrRejectInvitation(id, InvitationStatusRequest("rejected"))

                if (response.code() != 204) {
                    Toast.makeText(
                        context,
                        "Failed to reject invitation",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    e.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        getInvitations(context)
    }

    fun cancelInvitation(context: Context, id: Int) {
        viewModelScope.launch {
            try {
                val response = Api.getApiService(context)
                    .cancelInvitation(id)

                if (response.code() != 204) {
                    Toast.makeText(
                        context,
                        "Failed to cancel invitation",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    e.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        getInvitations(context)
    }

    private fun addContact(contact: Contact) {
        val contactId = mainActivityDao.insert(contact)
        val conversationId = mainActivityDao.insert(Conversation(
            contactId
        ))
        mainActivityDao.insert(Message(
            content = "Say hi!",
            sentAt = Calendar.getInstance().time,
            isOwnMessage = true,
            conversationId = conversationId
        ))
    }
}

class InvitationsViewModelFactory(
    private val mainActivityDao: MainActivityDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InvitationsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InvitationsViewModel(mainActivityDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}