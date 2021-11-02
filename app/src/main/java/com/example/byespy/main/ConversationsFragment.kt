package com.example.byespy.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.byespy.adapter.ConversationItemAdapter
import com.example.byespy.data.model.ConversationItem
import com.example.byespy.databinding.FragmentConversationsBinding

class ConversationsFragment : Fragment() {
    private lateinit var binding: FragmentConversationsBinding
    private val mainViewModel by activityViewModels<MainViewModel>()

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
        val recyclerViewAdapter = ConversationItemAdapter()
        recyclerView.adapter = recyclerViewAdapter

        mainViewModel.conversationsLiveData.observe(viewLifecycleOwner, {
            it?.let {
                recyclerViewAdapter.submitList(it as MutableList<ConversationItem>)
            }
        })
    }
}