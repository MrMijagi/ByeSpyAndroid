package com.example.byespy.ui.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.byespy.ui.adapter.MessageItemAdapter
import com.example.byespy.data.model.MessageItem
import com.example.byespy.databinding.ActivityChatBinding
import com.example.byespy.network.websocket.Message
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private val chatViewModel by viewModels<ChatViewModel> {
        ChatViewModelFactory()
    }

    companion object {
        const val WEB_SOCKET_URL = "ws://192.168.8.109/cable"
        const val TAG = "chat"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // setup recyclerView
        val recyclerView = binding.chatRecyclerView
        val recyclerViewAdapter = MessageItemAdapter()
        recyclerView.adapter = recyclerViewAdapter

        chatViewModel.messagesLiveData.observe(this, {
            it?.let {
                recyclerViewAdapter.submitList(it as MutableList<MessageItem>)
            }
        })

        // setup sending message through button
        val sendButton = binding.sendMessageButton
        val inputText = binding.outlinedTextFieldInside

        sendButton.setOnClickListener {
            chatViewModel.insertOwnMessage(inputText.text.toString())
            // send message
            chatViewModel.sendMessage(applicationContext, inputText.text.toString())
            inputText.text?.clear()

            recyclerView.scrollToPosition(chatViewModel.getMessagesSize() - 1)
        }
    }

    private fun receiveMessage(message: String?) {
        message?.let {
            val moshi = Moshi.Builder().build()
            val adapter: JsonAdapter<Message> =
                moshi.adapter(Message::class.java)
            val messageObject = adapter.fromJson(it)

            if (messageObject != null) {
                chatViewModel.insertOtherMessage(
                    messageObject.content,
                    messageObject.senderEmail
                )
            }
        }
    }
}