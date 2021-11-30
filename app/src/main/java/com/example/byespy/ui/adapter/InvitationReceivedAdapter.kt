package com.example.byespy.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.byespy.data.model.InvitationItem
import com.example.byespy.databinding.InvitationReceivedBinding

class InvitationReceivedAdapter(private val onAccept: (InvitationItem) -> Unit,
                                private val onReject: (InvitationItem) -> Unit)
    : ListAdapter<InvitationItem,
        InvitationReceivedAdapter.InvitationReceivedViewHolder>(InvitationItemDiffCallback) {

    class InvitationReceivedViewHolder(private val binding: InvitationReceivedBinding,
                                       val onAccept: (InvitationItem) -> Unit,
                                       val onReject: (InvitationItem) -> Unit)
        : RecyclerView.ViewHolder(binding.root) {

        private var currentInvitationItem: InvitationItem? = null

        init {
            binding.accept.setOnClickListener {
                currentInvitationItem?.let {
                    onAccept(it)
                }
            }

            binding.reject.setOnClickListener {
                currentInvitationItem?.let {
                    onReject(it)
                }
            }
        }

        fun bind(invitationItem: InvitationItem) {
            currentInvitationItem = invitationItem

            with(binding) {
                email.text = invitationItem.email
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvitationReceivedViewHolder {
        val inflater = LayoutInflater
            .from(parent.context)
        val binding = InvitationReceivedBinding.inflate(inflater, parent, false)

        return InvitationReceivedViewHolder(binding, onAccept, onReject)
    }

    override fun onBindViewHolder(holder: InvitationReceivedViewHolder, position: Int) {
        val invitation = getItem(position)
        holder.bind(invitation)
    }
}

object InvitationItemDiffCallback : DiffUtil.ItemCallback<InvitationItem>() {
    override fun areItemsTheSame(oldItem: InvitationItem, newItem: InvitationItem): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: InvitationItem, newItem: InvitationItem): Boolean {
        return oldItem.id == newItem.id
    }
}