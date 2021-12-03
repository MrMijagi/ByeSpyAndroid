package com.example.byespy.ui.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.coroutineScope
import com.example.byespy.ByeSpyApplication
import com.example.byespy.R
import com.example.byespy.ui.adapter.MessageItemAdapter
import com.example.byespy.data.model.MessageItem
import com.example.byespy.databinding.ActivityChatBinding
import com.example.byespy.network.websocket.Message
import com.example.byespy.network.websocket.MessageListener
import com.example.byespy.network.websocket.MessageReceived
import com.example.byespy.network.websocket.WebSocketManager
import com.example.byespy.ui.contact.ContactActivity
import com.example.byespy.ui.main.MainActivity
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*

class ChatActivity : AppCompatActivity(), MessageListener {

    private lateinit var binding: ActivityChatBinding
    private val chatViewModel by viewModels<ChatViewModel> {
        ChatViewModelFactory(
            intent.getLongExtra("conversationId", 0),
            (application as ByeSpyApplication).database.chatActivityDao()
        )
    }

    companion object {
        const val WEB_SOCKET_URL = "ws://192.168.8.109:4000/cable"
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

        lifecycle.coroutineScope.launch {
            chatViewModel.messages().collect {
                recyclerViewAdapter.submitList(it)
            }
        }

        // setup sending message through button
        val sendButton = binding.sendMessageButton
        val inputText = binding.outlinedTextFieldInside

        sendButton.setOnClickListener {
            chatViewModel.insertOwnMessage(inputText.text.toString())

            // send message to server
            chatViewModel.sendMessage(
                applicationContext,
                inputText.text.toString(),
                chatViewModel.getServerIdByConversationId()
            )

            inputText.text?.clear()

            recyclerView.scrollToPosition(recyclerViewAdapter.itemCount - 1)
        }

        WebSocketManager.init(WEB_SOCKET_URL, this)
        WebSocketManager.connect()
    }

    override fun onDestroy() {
        super.onDestroy()

        WebSocketManager.close()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.chat_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.contact_info -> {
                val intent = Intent(this, ContactActivity::class.java)
                intent.putExtra("conversationId", chatViewModel.conversationId)

                startActivity(intent)
                true
            }
            R.id.home -> {
                setResult(RESULT_OK)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun receiveMessage(message: String?) {
        Log.d(TAG, message ?: "nothing")

        message?.let {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .add(Date::class.java, Rfc3339DateJsonAdapter())
                .build()
            val adapter: JsonAdapter<MessageReceived> =
                moshi.adapter(MessageReceived::class.java)
            val messageObject = adapter.fromJson(it)

            if (messageObject != null) {
                chatViewModel.insertOtherMessage(
                    messageObject.content,
                    messageObject.createdAt
                )
            }
        }
    }

    private fun subscribe() {
        val text =
            "{\n" +
            "   \"command\": \"subscribe\",\n" +
            "   \"identifier\": \"{\\\"channel\\\": \\\"MessagesChannel\\\"}\"\n" +
            "}"

        WebSocketManager.sendMessage(text)
    }

    override fun onConnectSuccess() {
        Log.d(TAG, "onConnectSuccess")
        subscribe()
    }

    override fun onConnectFailed() {
        Log.d(TAG, "onConnectFailed")
    }

    override fun onClose() {
        Log.d(TAG, "onClose")
    }

    override fun onMessage(text: String?) {
        val jsonObject = JSONObject(text ?: "")

        when {
            jsonObject.has("type") -> {
                Log.d(TAG, jsonObject.getString("type"))
            }
            jsonObject.has("message") -> {
                // receive messages
                val message = jsonObject.getJSONObject("message")

                if (jsonObject.has("message")) {
                    val innerMessage = message.getJSONObject("message")
                    receiveMessage(innerMessage.toString())
                } else {            // messages
                    val messages = message.getJSONArray("messages")

                    // iterate through each message
                    for (i in 0..messages.length()) {
                        receiveMessage(messages.getString(i))
                    }
                }
            }
            else -> {
                Log.d(TAG, "unhandledMessage")
            }
        }

        when (val type = jsonObject.get("type") as String) {
            "welcome" -> Log.d(TAG, "welcome")
            "ping" -> Unit
            else -> Log.d(TAG, type)
        }
        //receiveMessage(text)
    }
}