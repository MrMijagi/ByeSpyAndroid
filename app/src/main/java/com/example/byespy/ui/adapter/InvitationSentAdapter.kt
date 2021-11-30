package com.example.byespy.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.byespy.R
import com.example.byespy.data.model.InvitationItem
import com.example.byespy.databinding.InvitationSentBinding

class InvitationSentAdapter(private val onCancel: (InvitationItem) -> Unit)
    : ListAdapter<InvitationItem,
        InvitationSentAdapter.InvitationSentViewHolder>(InvitationItemDiffCallback) {

    class InvitationSentViewHolder(private val binding: InvitationSentBinding,
                                   val onCancel: (InvitationItem) -> Unit)
        : RecyclerView.ViewHolder(binding.root) {

        private var currentInvitationItem: InvitationItem? = null

        init {
            binding.cancel.setOnClickListener {
                currentInvitationItem?.let {
                    onCancel(it)
                }
            }
        }

        fun bind(invitationItem: InvitationItem) {
            currentInvitationItem = invitationItem

            with(binding) {
                email.text = invitationItem.email
                status.text = invitationItem.status

                when(invitationItem.status) {
                    "pending" -> status.setBackgroundResource(R.drawable.invitation_background_yellow)
                    "accepted" -> {
                        status.setBackgroundResource(R.drawable.invitation_background_green)
                        disableCancelButton()
                    }
                    "rejected" -> {
                        status.setBackgroundResource(R.drawable.invitation_background_red)
                        disableCancelButton()
                    }
                }
            }
        }

        fun disableCancelButton() {
            with(binding.cancel) {
                this.isEnabled = false
                this.isClickable = false
                this.setBackgroundResource(R.color.disabled_cancel_button)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvitationSentViewHolder {
        val inflater = LayoutInflater
            .from(parent.context)
        val binding = InvitationSentBinding.inflate(inflater, parent, false)

        return InvitationSentViewHolder(binding, onCancel)
    }

    override fun onBindViewHolder(holder: InvitationSentViewHolder, position: Int) {
        val invitation = getItem(position)
        holder.bind(invitation)
    }
}