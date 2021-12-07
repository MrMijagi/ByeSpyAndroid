package com.example.byespy.ui.register

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.byespy.databinding.RegisterActivityBinding
import com.example.byespy.ui.login.LoginActivity
import com.example.byespy.ui.login.LoginViewModel
import com.example.byespy.ui.login.LoginViewModelFactory

class RegisterActivity : AppCompatActivity() {

    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var binding: RegisterActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = RegisterActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val email = binding.email
        val username = binding.username
        val password = binding.password
        val passwordCheck = binding.confirmPassword
        val button = binding.login

        registerViewModel = ViewModelProvider(this, RegisterViewModelFactory())[RegisterViewModel::class.java]

        button.setOnClickListener {
            if (password.text.toString() == passwordCheck.text.toString()) {
                registerViewModel.signUp(
                    applicationContext,
                    email.text.toString(),
                    username.text.toString(),
                    password.text.toString()
                )
            } else {
                Toast.makeText(
                    this,
                    "Passwords must match!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        registerViewModel.signUpResult.observe(this, Observer {
            val signUpResult = it ?: return@Observer

            if (signUpResult) {
                Toast.makeText(
                    applicationContext,
                    "Activate account",
                    Toast.LENGTH_LONG
                ).show()
            }

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}