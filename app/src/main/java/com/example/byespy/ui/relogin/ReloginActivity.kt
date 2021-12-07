package com.example.byespy.ui.relogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.example.byespy.ByeSpyApplication
import com.example.byespy.databinding.ActivityReloginBinding
import com.example.byespy.network.SessionManager
import com.example.byespy.ui.login.CodeVerificationActivity
import com.example.byespy.ui.main.MainActivity
import com.example.byespy.ui.start.StartActivity

class ReloginActivity : AppCompatActivity() {

    private val reloginViewModel by viewModels<ReloginViewModel> {
        ReloginViewModelFactory(
            (application as ByeSpyApplication).database.reloginActivityDao()
        )
    }
    private lateinit var binding: ActivityReloginBinding
    private lateinit var startCodeVerificationActivity: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReloginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email = binding.email
        val password = binding.password
        val login = binding.login
        val changeUser = binding.changeUser

        val sessionManager = SessionManager(this)
        sessionManager.logPreferences("RELOGIN")

        val userEmail = sessionManager.fetchUserEmail() ?: ""

        email.text = userEmail

        login.setOnClickListener {
            // try to login with login and password
            reloginViewModel.authorizeUser(
                applicationContext,
                userEmail,
                password.text.toString()
            )
        }

        changeUser.setOnClickListener {
            // show dialog before deleting all user data
            val builder = AlertDialog.Builder(this@ReloginActivity)
            builder.setMessage("If you change user all previous user data will be deleted. Continue?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    // delete tables and all shared preferences
                    reloginViewModel.changeUser(sessionManager)

                    // launch start activity
                    val intent = Intent(this, StartActivity::class.java)
                    startActivity(intent)

                    setResult(RESULT_OK)
                    finish()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }

            val alert = builder.create()
            alert.show()
        }

        reloginViewModel.authorizationResult.observe(this@ReloginActivity, Observer {
            val authorizationResult = it ?: return@Observer

            if (authorizationResult) {
                // get verification code
                val intent = Intent(this, CodeVerificationActivity::class.java)
                intent.putExtra("email", userEmail)
                intent.putExtra("password", password.text.toString())

                startCodeVerificationActivity.launch(intent)
            }
        })

        startCodeVerificationActivity = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            when (result.resultCode) {
                RESULT_OK -> {
                    reloginViewModel.login(
                        applicationContext,
                        userEmail,
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

        reloginViewModel.toastMessage.observe(this@ReloginActivity, Observer {
            val toastMessage = it ?: return@Observer

            Toast.makeText(
                this,
                toastMessage,
                Toast.LENGTH_LONG
            ).show()
        })

        reloginViewModel.loginResult.observe(this@ReloginActivity, Observer {
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
                sessionManager.saveAuthToken(loginResult.success.accessToken)
                sessionManager.saveRefreshToken(loginResult.success.refreshToken)

                // go to main activity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

                //Complete and destroy login activity once successful
                setResult(RESULT_OK)
                finish()
            }
        })
    }
}