package com.example.myapplication

import android.content.Intent
import android.nfc.Tag
import android.os.Bundle
import android.os.Message
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.ezemkofi.HomeActivity
import com.example.ezemkofi.Tools.ConnectionManager
import com.example.ezemkofi.Tools.NetworkException
import com.example.ezemkofi.Tools.PreferenceHelper
import com.example.myapplication.databinding.ActivityLoginBinding
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val connectionManager = ConnectionManager()
    private lateinit var preferenceHelper : PreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.isNestedScrollingEnabled = true

        binding.login.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.buttonSignup.setOnClickListener {
            val username = binding.usnInput.text.toString().trim()
            val fullName = binding.fullnameInput.text.toString().trim()
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()
            val confirmPassword = binding.cpasswordInput.text.toString().trim()

            when {
                username.isEmpty() || fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() -> {
                    Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                }

                password != confirmPassword -> {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    register(username, fullName, email, password)
                }
            }
        }
        preferenceHelper = PreferenceHelper(this@RegisterActivity)
    }

    private fun register(username: String, fullName: String, email: String, password: String) {
        lifecycleScope.launch {
            try {
                val token = connectionManager.register(username, fullName, email, password)
                preferenceHelper.setToken(token)
                startActivity(Intent(this@RegisterActivity, HomeActivity::class.java))

            } catch (e: NetworkException.AuthException) {
                Toast.makeText(this@RegisterActivity, "Invalid", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, "Register failed ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}