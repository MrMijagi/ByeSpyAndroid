package com.example.byespy.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.byespy.ui.chat.ChatActivity
import com.example.byespy.databinding.StartActivityBinding
import com.example.byespy.network.SessionManager
import com.example.byespy.ui.login.LoginActivity
import com.example.byespy.ui.main.MainActivity
import com.example.byespy.ui.register.RegisterActivity

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = StartActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // check if user is already logged in
        val sessionManager = SessionManager(applicationContext)

        if (sessionManager.fetchRefreshToken() != null) {
            // go to main activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            //Complete and destroy login activity once successful
            setResult(RESULT_OK)
            finish()
        }

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