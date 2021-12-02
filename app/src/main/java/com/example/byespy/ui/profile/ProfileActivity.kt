package com.example.byespy.ui.profile

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.byespy.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val profileViewModel by viewModels<ProfileViewModel> {
        ProfileViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val usernameInput = binding.usernameInput
        val currentPassword = binding.currentPassword
        val newPassword = binding.newPassword
        val newPasswordConfirm = binding.newPasswordConfirm
        val usernameButton = binding.usernameButton
        val passwordButton = binding.passwordButton

        usernameButton.setOnClickListener {
            val usernameValue = usernameInput.text.toString()

            if (usernameValue != "") {
                profileViewModel.saveUsername(this, usernameValue)
            }
        }

        passwordButton.setOnClickListener {
            val currentPasswordValue = currentPassword.text.toString()
            val newPasswordValue = newPassword.text.toString()
            val newPasswordConfirmValue = newPasswordConfirm.text.toString()

            if (currentPasswordValue != ""
                && newPasswordValue != ""
                && newPasswordConfirmValue != "") {
                profileViewModel.savePassword(
                    this,
                    currentPasswordValue,
                    newPasswordValue,
                    newPasswordConfirmValue
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()

        profileViewModel.getProfile(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                setResult(Activity.RESULT_OK)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}