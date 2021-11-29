package com.example.byespy.ui.contact

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import com.example.byespy.ByeSpyApplication
import com.example.byespy.R
import com.example.byespy.databinding.ActivityContactBinding
import com.example.byespy.ui.main.MainActivity

class ContactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContactBinding
    private val contactViewModel by viewModels<ContactViewModel> {
        ContactViewModelFactory(
            intent.getLongExtra("conversationId", 0),
            (application as ByeSpyApplication).database.contactActivityDao()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityContactBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val email = binding.contactEmail
        email.text = contactViewModel.getEmail()

        val clearMessages = binding.clearMessagesButton
        clearMessages.setOnClickListener {
            contactViewModel.clearMessages()
        }

        val deleteContact = binding.deleteContactButton
        deleteContact.setOnClickListener {
            contactViewModel.deleteContact()

            // go to main activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            //Complete and destroy login activity once successful
            setResult(RESULT_OK)
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.home -> {
                setResult(RESULT_OK)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}