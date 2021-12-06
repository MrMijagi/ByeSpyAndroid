package com.example.byespy.ui.login

import android.R
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.byespy.databinding.ActivityLoginBinding
import com.example.byespy.network.SessionManager

import com.example.byespy.ui.main.MainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding
    private lateinit var startCodeVerificationActivity: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val username = binding.username
        val password = binding.password
        val login = binding.login

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())[LoginViewModel::class.java]

        login.setOnClickListener {
            // try to login with login and password
            loginViewModel.authorizeUser(
                applicationContext,
                username.text.toString(),
                password.text.toString()
            )
        }

        startCodeVerificationActivity = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            when (result.resultCode) {
                RESULT_OK -> {
                    loginViewModel.login(
                        applicationContext,
                        username.text.toString(),
                        password.text.toString(),
                        result.data?.getStringExtra("code") ?: ""
                    )
                }
                RESULT_CANCELED -> Toast.makeText(
                    this,
                    "Code verification canceled",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        loginViewModel.authorizationResult.observe(this@LoginActivity, Observer {
            val authorizationResult = it ?: return@Observer

            if (authorizationResult) {
                // get verification code
                val intent = Intent(this, CodeVerificationActivity::class.java)
                intent.putExtra("email", username.text.toString())
                intent.putExtra("password", password.text.toString())

                startCodeVerificationActivity.launch(intent)
            }
        })

        loginViewModel.toastMessage.observe(this@LoginActivity, Observer {
            val toastMessage = it ?: return@Observer

            Toast.makeText(
                this,
                toastMessage,
                Toast.LENGTH_LONG
            ).show()
        })

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
                // save tokens to session manager
                val sessionManager = SessionManager(applicationContext)
                sessionManager.saveAuthToken(loginResult.success.accessToken)
                sessionManager.saveRefreshToken(loginResult.success.refreshToken)

                // save user profile if not saved yet
                if (sessionManager.fetchUserId() == -1) {
                    loginViewModel.saveProfile(this)
                }

                // go to main activity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

                //Complete and destroy login activity once successful
                setResult(RESULT_OK)
                finish()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.home -> {
                finish()
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}