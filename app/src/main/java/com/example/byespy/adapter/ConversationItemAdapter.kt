package com.example.byespy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.byespy.data.model.ConversationItem
import com.example.byespy.databinding.ConversationItemBinding

class ConversationItemAdapter
    : ListAdapter<ConversationItem,
        ConversationItemAdapter.ConversationItemViewHolder>(ConversationDiffCallback) {

    class ConversationItemViewHolder(private val binding: ConversationItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(conversation: ConversationItem) {
            with(binding) {
                conversationTitle.text = conversation.title
                conversationLastMessage.text = conversation.lastMessage
                profileImage.setImageDrawable(R.drawable.avatar)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationItemViewHolder {
        val inflater = LayoutInflater
            .from(parent.context)
        val binding = ConversationItemBinding.inflate(inflater, parent, false)

        return ConversationItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConversationItemViewHolder, position: Int) {
        val conversation = getItem(position)
        holder.bind(conversation)
    }
}

object ConversationDiffCallback : DiffUtil.ItemCallback<ConversationItem>() {
    override fun areItemsTheSame(oldItem: ConversationItem, newItem: ConversationItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ConversationItem, newItem: ConversationItem): Boolean {
        return oldItem.id == newItem.id
    }
}