package com.example.byespy.ui.main

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import com.example.byespy.ByeSpyApplication
import com.example.byespy.data.model.ConversationItem
import com.example.byespy.ui.adapter.ConversationItemAdapter
import com.example.byespy.databinding.FragmentConversationsBinding
import com.example.byespy.ui.chat.ChatActivity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ConversationsFragment : Fragment() {
    private lateinit var binding: FragmentConversationsBinding
    private lateinit var startNewConversationActivity: ActivityResultLauncher<Intent>
    private val mainViewModel by activityViewModels<MainViewModel> {
        MainViewModelFactory(
            (activity?.application as ByeSpyApplication).database.mainActivityDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConversationsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.conversationsRecyclerView
        val recyclerViewAdapter = ConversationItemAdapter {
            conversationItem -> adapterOnClick(conversationItem)
        }
        recyclerView.adapter = recyclerViewAdapter
        val fab = binding.fab

        startNewConversationActivity = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            when (result.resultCode) {
                RESULT_OK -> {
                    mainViewModel.addConversation(
                        result.data?.getStringExtra("email")!!,
                        result.data?.getStringExtra("name")!!,
                        result.data?.getStringExtra("image")!!
                    )
                }
                RESULT_CANCELED -> {
                    Toast.makeText(
                        context,
                        "New conversation canceled",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        fab.setOnClickListener {
            startNewConversationActivity.launch(Intent(
                context, NewConversationActivity::class.java
            ))
        }

        lifecycle.coroutineScope.launch {
            mainViewModel.conversations().collect {
                recyclerViewAdapter.submitList(it)
            }
        }
    }

    private fun adapterOnClick(conversation: ConversationItem) {
        val intent = Intent(
            context,
            ChatActivity::class.java
        )

        intent.putExtra("conversationId", conversation.id)
        startActivity(intent)
    }
}