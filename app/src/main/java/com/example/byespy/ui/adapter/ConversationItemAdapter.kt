package com.example.byespy.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.byespy.data.model.ConversationItem
import com.example.byespy.databinding.ConversationItemBinding

class ConversationItemAdapter(private val onClick: (ConversationItem) -> Unit)
    : ListAdapter<ConversationItem,
        ConversationItemAdapter.ConversationItemViewHolder>(ConversationDiffCallback) {

    class ConversationItemViewHolder(private val binding: ConversationItemBinding, val onClick: (ConversationItem) -> Unit)
        : RecyclerView.ViewHolder(binding.root) {

        private var currentConversationItem: ConversationItem? = null

        init {
            binding.root.setOnClickListener {
                currentConversationItem?.let {
                    onClick(it)
                }
            }
        }

        fun bind(conversation: ConversationItem) {
            currentConversationItem = conversation

            with(binding) {
                conversationTitle.text = conversation.username ?: conversation.email

                conversationLastMessage.text = if (conversation.lastMessage != null) {
                    if (conversation.lastMessageOwn == true) {
                        "You: ${conversation.lastMessage}"
                    } else {
                        conversation.lastMessage
                    }
                } else {
                    ""
                }
//                profileImage.setImageURI(Uri.parse(conversation.image))
//                profileImage.clipToOutline = true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationItemViewHolder {
        val inflater = LayoutInflater
            .from(parent.context)
        val binding = ConversationItemBinding.inflate(inflater, parent, false)

        return ConversationItemViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: ConversationItemViewHolder, position: Int) {
        val conversation = getItem(position)
        holder.bind(conversation)
    }
}

object ConversationDiffCallback : DiffUtil.ItemCallback<ConversationItem>() {
    override fun areItemsTheSame(oldItem: ConversationItem, newItem: ConversationItem): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: ConversationItem, newItem: ConversationItem): Boolean {
        return oldItem.id == newItem.id
    }
}