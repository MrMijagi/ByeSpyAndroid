package com.example.byespy.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.byespy.ui.adapter.ContactItemAdapter
import com.example.byespy.data.model.ContactItem
import com.example.byespy.databinding.FragmentContactsBinding

class ContactsFragment : Fragment() {
    private lateinit var binding: FragmentContactsBinding
    private val mainViewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentContactsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.contactsRecyclerView
        val recyclerViewAdapter = ContactItemAdapter()
        recyclerView.adapter = recyclerViewAdapter

        mainViewModel.contactsLiveData.observe(viewLifecycleOwner, {
            it?.let {
                recyclerViewAdapter.submitList(it as MutableList<ContactItem>)
            }
        })
    }
}