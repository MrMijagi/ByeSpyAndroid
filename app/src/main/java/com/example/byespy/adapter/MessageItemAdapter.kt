package com.example.byespy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.byespy.data.model.MessageItem
import com.example.byespy.databinding.MessageOtherItemBinding
import com.example.byespy.databinding.MessageOwnItemBinding

class MessageItemAdapter
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

    // for messages send by others
    class OtherMessageItemViewHolder(private val binding: MessageOtherItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: MessageItem) {
            with(binding) {
                messageText.text = message.content
                messageAuthor.text = message.author
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val author = getItem(position).author

        return if (author == "dawid@gmail.com") 0 else 1
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when(viewType) {
            0 -> OwnMessageItemViewHolder(MessageOwnItemBinding.inflate(inflater, parent, false))
            else -> OtherMessageItemViewHolder(MessageOtherItemBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType) {
            0 -> {
                val correctHolder = holder as OwnMessageItemViewHolder
                correctHolder.bind(getItem(position))
            }
            else -> {
                val correctHolder = holder as OtherMessageItemViewHolder
                correctHolder.bind(getItem(position))
            }
        }
    }
}

object MessageItemDiffCallback : DiffUtil.ItemCallback<MessageItem>() {
    override fun areItemsTheSame(oldItem: MessageItem, newItem: MessageItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: MessageItem, newItem: MessageItem): Boolean {
        return oldItem.id == newItem.id
    }
}