package com.example.byespy.ui.main

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.byespy.R
import com.example.byespy.databinding.ActivityNewConversationBinding

class NewConversationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewConversationBinding
    private lateinit var startImageChoiceActivity: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewConversationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val email = binding.contactEmailInput
        val name = binding.conversationNameInput
        val image = binding.profileImageView
        val imageButton = binding.conversationImageButton
        val addButton = binding.newConversationButton

        startImageChoiceActivity = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            when (result.resultCode) {
                RESULT_OK -> {
                    val uri = result.data?.data
                    image.setImageURI(result.data?.data)
                    // remember the uri to send it later
                    image.tag = uri.toString()
                }
                RESULT_CANCELED -> Toast.makeText(
                    this,
                    "Image wasn't chosen",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        imageButton.setOnClickListener {
            getImageUri()
        }

        addButton.setOnClickListener {
            if (checkInputs(
                    email.text.toString(),
                    name.text.toString())
                && image.tag != null
            ) {
                this.intent.putExtra("email", email.text.toString())
                this.intent.putExtra("name", name.text.toString())
                this.intent.putExtra("image", image.tag.toString())

                setResult(Activity.RESULT_OK, this.intent)
                finish()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                setResult(Activity.RESULT_CANCELED)
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun checkInputs(email: String, name: String): Boolean {
        return email != "" && name != ""
    }

    private fun getImageUri() {
        val imageIntent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }

        startImageChoiceActivity.launch(imageIntent)
    }
}