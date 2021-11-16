package com.example.byespy.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.byespy.data.model.ContactItem
import com.example.byespy.databinding.ContactItemBinding

class ContactItemAdapter
    : ListAdapter<ContactItem,
        ContactItemAdapter.ContactItemViewHolder>(ContactDiffCallback) {

    class ContactItemViewHolder(private val binding: ContactItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(contact: ContactItem) {
            with(binding) {
                contactEmail.text = contact.email
                profileImage.clipToOutline = true
                profileImage.setImageResource(contact.image)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactItemViewHolder {
        val inflater = LayoutInflater
            .from(parent.context)
        val binding = ContactItemBinding.inflate(inflater, parent, false)

        return ContactItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactItemViewHolder, position: Int) {
        val contact = getItem(position)
        holder.bind(contact)
    }
}

object ContactDiffCallback : DiffUtil.ItemCallback<ContactItem>() {
    override fun areItemsTheSame(oldItem: ContactItem, newItem: ContactItem): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: ContactItem, newItem: ContactItem): Boolean {
        return oldItem.id == newItem.id
    }
}