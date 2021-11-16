package com.example.byespy.ui.login

import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.byespy.databinding.ActivityCodeVerificationBinding

class CodeVerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCodeVerificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCodeVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val code = binding.code
        val login = binding.login

        login.setOnClickListener {
            this.intent.putExtra("code", code.text.toString())

            setResult(Activity.RESULT_OK, this.intent)
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                setResult(Activity.RESULT_CANCELED)
                finish()
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}