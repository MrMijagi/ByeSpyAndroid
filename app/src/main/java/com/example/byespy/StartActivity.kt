package com.example.byespy

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.byespy.databinding.StartActivityBinding
import com.example.byespy.ui.login.LoginActivity
import com.example.byespy.ui.register.RegisterActivity

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = StartActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val signInButton = binding.SignInButton
            signInButton.setOnClickListener {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }

        val signUpButton = binding.SignUpButton
            signUpButton.setOnClickListener {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            }
    }
}