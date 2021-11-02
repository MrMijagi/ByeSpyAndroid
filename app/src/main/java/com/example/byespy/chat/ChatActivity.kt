package com.example.byespy.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.byespy.adapter.MessageItemAdapter
import com.example.byespy.data.model.MessageItem
import com.example.byespy.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatBinding
    private val chatViewModel by viewModels<ChatViewModel> {
        ChatViewModelFactory()
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
            inputText.text?.clear()

            recyclerView.scrollToPosition(chatViewModel.getMessagesSize() - 1)
        }
    }
}