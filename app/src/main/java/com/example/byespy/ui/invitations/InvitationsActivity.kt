package com.example.byespy.ui.invitations

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.byespy.ByeSpyApplication
import com.example.byespy.R
import com.example.byespy.data.entity.Contact
import com.example.byespy.data.model.InvitationItem
import com.example.byespy.databinding.ActivityInvitationsBinding
import com.example.byespy.network.response.InvitationReceived
import com.example.byespy.ui.adapter.InvitationReceivedAdapter
import com.example.byespy.ui.adapter.InvitationSentAdapter
import com.example.byespy.ui.contact.ContactActivity

class InvitationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInvitationsBinding
    private val invitationsViewModel by viewModels<InvitationsViewModel> {
        InvitationsViewModelFactory(
            (application as ByeSpyApplication).database.mainActivityDao()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInvitationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val sentRecyclerView = binding.sentRv
        val sentRecyclerViewAdapter = InvitationSentAdapter {
            invitationItem -> adapterOnCancel(invitationItem)
        }
        sentRecyclerView.adapter = sentRecyclerViewAdapter

        val receivedRecyclerView = binding.receivedRv
        val receivedRecyclerViewAdapter = InvitationReceivedAdapter(
            this::adapterOnAccept,
            this::adapterOnReject
        )
        receivedRecyclerView.adapter = receivedRecyclerViewAdapter

        invitationsViewModel.sentInvitationsLiveData.observe(this, Observer {
            sentRecyclerViewAdapter.submitList(null)
            sentRecyclerViewAdapter.submitList(it)
        })

        invitationsViewModel.receivedInvitationLiveData.observe(this, Observer {
            receivedRecyclerViewAdapter.submitList(null)
            receivedRecyclerViewAdapter.submitList(it)
        })

        invitationsViewModel.getInvitations(applicationContext)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.invitations_menu, menu)
        return true
    }

    private fun adapterOnCancel(invitationItem: InvitationItem) {
        invitationsViewModel.cancelInvitation(applicationContext, invitationItem.id)
    }

    private fun adapterOnAccept(invitationItem: InvitationItem) {
        invitationsViewModel.acceptInvitation(
            applicationContext,
            invitationItem.id,
            invitationItem.userId,
            invitationItem.email
        )
    }

    private fun adapterOnReject(invitationItem: InvitationItem) {
        invitationsViewModel.rejectInvitation(
            applicationContext,
            invitationItem.id
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.refresh_invitations -> {
                invitationsViewModel.getInvitations(applicationContext)
                true
            }
            android.R.id.home -> {
                setResult(RESULT_OK)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}