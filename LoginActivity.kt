package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ezemkofi.HomeActivity
import com.example.ezemkofi.Tools.ConnectionManager
import com.example.ezemkofi.Tools.NetworkException
import com.example.ezemkofi.Tools.PreferenceHelper
import com.example.myapplication.databinding.ActivityDetailBinding
import com.example.myapplication.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val connectionManager = ConnectionManager()
    private lateinit var preferenceHelper : PreferenceHelper

    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.register.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java))
        }
        binding.buttonLogin.setOnClickListener {
            val username = binding.usnInput.text.toString()
            val password = binding.pwInput.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                login(username, password)
            }
        }



        preferenceHelper = PreferenceHelper(this@LoginActivity)

        binding.usnInput.setText("mahdi")
        binding.pwInput.setText("1234")


    }

    private fun login(username: String, password: String) {
        lifecycleScope.launch {
            try {
                val token = connectionManager.login(username, password)
                preferenceHelper.setToken(token)
                startActivity(Intent(this@LoginActivity,HomeActivity::class.java))
            } catch (e: NetworkException.AuthException) {
                Toast.makeText(this@LoginActivity, "Invalid username or password", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Login failed ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getButton(binding : ActivityLoginBinding){
        binding.buttonLogin
    }
}
