package com.example.byespy.login

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import android.widget.Toast
import com.example.byespy.databinding.ActivityLoginBinding

import com.example.byespy.main.MainActivity
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val username = binding.username
        val password = binding.password
        val login = binding.login

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
                .get(LoginViewModel::class.java)

        login.setOnClickListener {
            // try to login with login and password
            loginViewModel.login(
                username.text.toString(),
                password.text.toString()
            )
        }

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            if (loginResult.error != null) {
                // show that something went wrong
                Toast.makeText(
                    this,
                    loginResult.success?.accessToken,
                    Toast.LENGTH_LONG
                ).show()
            } else if (loginResult.success != null) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

                //Complete and destroy login activity once successful
                setResult(Activity.RESULT_OK)
                finish()
            }
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