package com.example.ezemkofi

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ezemkofi.Tools.ConnectionManager
import com.example.ezemkofi.adapter.TopAdapter
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivitySearchBinding
import kotlinx.coroutines.launch
import org.json.JSONArray

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private val connectionManager = ConnectionManager()
    private lateinit var topAdapter: TopAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySearchBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        binding.search.requestFocus()
        searchCoffee()
        setContentView(binding.root)
        binding.backBtn.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
    }

    private fun searchCoffee() {
        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                getDataSearch(p0)
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }

    private fun getDataSearch(p0: CharSequence?) {
        try {
            lifecycleScope.launch {
                val resultSearch = connectionManager.getCoffeeBySearch(p0.toString())
                setupListRecyclerView(resultSearch)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Failed ${e.message}", Toast.LENGTH_SHORT).show()

        }
    }

    private fun setupListRecyclerView(resultSearch: JSONArray) {
        topAdapter = TopAdapter(resultSearch)
        binding.rvSearch.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = topAdapter
        }
    }
}