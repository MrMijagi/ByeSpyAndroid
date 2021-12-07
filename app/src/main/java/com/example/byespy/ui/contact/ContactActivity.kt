package com.example.byespy.ui.contact

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
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

        val contact = contactViewModel.getContact()

        val email = binding.contactEmail
        email.text = contact.email

        val username = binding.contactUsername
        username.text = contact.username

        val imageView = binding.imageView

        if (contact.image != null) {
            val decodedString = Base64.decode(contact.image, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            imageView.setImageBitmap(decodedByte)
            //imageView.clipToOutline = true
        } else {
            imageView.setImageResource(R.drawable.ic_person_small)
            //imageView.clipToOutline = true
        }

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

    override fun onResume() {
        super.onResume()

        contactViewModel.updateImage(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                setResult(RESULT_OK)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}