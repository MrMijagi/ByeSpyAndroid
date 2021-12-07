package com.example.byespy.ui.start

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.byespy.databinding.StartActivityBinding
import com.example.byespy.network.SessionManager
import com.example.byespy.ui.login.LoginActivity
import com.example.byespy.ui.main.MainActivity
import com.example.byespy.ui.main.MainViewModel
import com.example.byespy.ui.register.RegisterActivity
import com.example.byespy.ui.relogin.ReloginActivity

class StartActivity : AppCompatActivity() {

    private val startViewModel by viewModels<StartViewModel> {
        StartViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = StartActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // check if user is already logged in
        val sessionManager = SessionManager(applicationContext)
        sessionManager.logPreferences("START")

        if (sessionManager.fetchUserId() != -1) {
            startViewModel.refreshToken(this)
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

        startViewModel.refreshTokenResponse.observe(this, Observer {
            if (it.accessToken != "") {
                // go to main activity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

                //Complete and destroy login activity once successful
                setResult(RESULT_OK)
                finish()
            } else {
                // ask user to login again
                val intent = Intent(this, ReloginActivity::class.java)
                startActivity(intent)

                //Complete and destroy login activity once successful
                setResult(RESULT_OK)
                finish()
            }
        })
    }
}