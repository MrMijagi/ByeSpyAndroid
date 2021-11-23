package com.example.byespy.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.byespy.data.model.MessageItem
import com.example.byespy.databinding.MessageOtherItemBinding
import com.example.byespy.databinding.MessageOtherItemNextBinding
import com.example.byespy.databinding.MessageOwnItemBinding
import com.example.byespy.databinding.MessageOwnItemNextBinding

class MessageItemAdapter()
    : ListAdapter<MessageItem,
        RecyclerView.ViewHolder>(MessageItemDiffCallback) {

    // for messages send by the user of the app
    class OwnMessageItemViewHolder(private val binding: MessageOwnItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: MessageItem) {
            with(binding) {
                messageText.text = message.content
            }
        }
    }

    class OwnNextMessageItemViewHolder(private val binding: MessageOwnItemNextBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: MessageItem) {
            with(binding) {
                messageText.text = message.content
            }
        }
    }

    // for messages send by others
    class OtherMessageItemViewHolder(private val binding: MessageOtherItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: MessageItem) {
            with(binding) {
                messageText.text = message.content
                messageAuthor.text = message.otherEmail
            }
        }
    }

    class OtherNextMessageItemViewHolder(private val binding: MessageOtherItemNextBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: MessageItem) {
            with(binding) {
                messageText.text = message.content
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val ownMessage = getItem(position).ownMessage

        // check if this author send more than one message in a row
        // 0 - own message, 1 - own next message
        // 2 - other message, 3 - other next message
        return if (position > 0 && getItem(position - 1).ownMessage == ownMessage) {
            if (ownMessage) 1 else 3
        } else {
            if (ownMessage) 0 else 2
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when(viewType) {
            0 -> OwnMessageItemViewHolder(MessageOwnItemBinding.inflate(inflater, parent, false))
            1 -> OwnNextMessageItemViewHolder(MessageOwnItemNextBinding.inflate(inflater, parent, false))
            2 -> OtherMessageItemViewHolder(MessageOtherItemBinding.inflate(inflater, parent, false))
            else -> OtherNextMessageItemViewHolder(MessageOtherItemNextBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType) {
            0 -> {
                val correctHolder = holder as OwnMessageItemViewHolder
                correctHolder.bind(getItem(position))
            }
            1 -> {
                val correctHolder = holder as OwnNextMessageItemViewHolder
                correctHolder.bind(getItem(position))
            }
            2 -> {
                val correctHolder = holder as OtherMessageItemViewHolder
                correctHolder.bind(getItem(position))
            }
            else -> {
                val correctHolder = holder as OtherNextMessageItemViewHolder
                correctHolder.bind(getItem(position))
            }
        }
    }
}

object MessageItemDiffCallback : DiffUtil.ItemCallback<MessageItem>() {
    override fun areItemsTheSame(oldItem: MessageItem, newItem: MessageItem): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: MessageItem, newItem: MessageItem): Boolean {
        return oldItem.id == newItem.id
    }
}