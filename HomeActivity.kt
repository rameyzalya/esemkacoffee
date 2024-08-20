package com.example.ezemkofi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ezemkofi.Tools.ConnectionManager
import com.example.ezemkofi.Tools.NetworkException
import com.example.ezemkofi.Tools.PreferenceHelper
import com.example.ezemkofi.adapter.CoffeAdapter
import com.example.ezemkofi.adapter.TopAdapter
import com.example.myapplication.LoginActivity
import com.example.myapplication.RegisterActivity
import com.example.myapplication.databinding.ActivityHomeBinding
import com.example.myapplication.databinding.ActivityLoginBinding
import com.example.myapplication.databinding.ItemTabChipBinding
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private val connectionManager = ConnectionManager()
    private lateinit var coffeeAdapter: CoffeAdapter
    private lateinit var topAdapter: TopAdapter
    private lateinit var preferenceHelper: PreferenceHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        preferenceHelper = PreferenceHelper(this@HomeActivity)
        Log.d("TAG", "checkDB: ${preferenceHelper.getCart()}")
        setContentView(binding.root)
        fetchUserData()
        getCategories()
        setupRecyclerView()
        getTopPicks()
        binding.search.isFocusable = false
        binding.search.isClickable = true
        binding.search.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
        binding.ivCart.setOnClickListener {
            startActivity(Intent(this, CartActivity:: class.java))
        }
    }

    private fun fetchUserData() {
        lifecycleScope.launch {
            try {
                val token = preferenceHelper.getToken()
                val user = token?.let { connectionManager.getMe(it) }
                binding.name.text = user?.getString("fullName")
            } catch (e: NetworkException.AuthException) {
                Toast.makeText(this@HomeActivity, "Unauthorized", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                finishAffinity()
            } catch (e: Exception) {
                Toast.makeText(this@HomeActivity, "Failed ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getTopPicks() {
        lifecycleScope.launch {
            try {
                val coffeList = connectionManager.getCoffeeTopPicks()
                setupListVerticalRecyclerView(coffeList)
            } catch (e: Exception) {
                Toast.makeText(this@HomeActivity, "Failed Load ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }

        }
    }

    private fun setupListVerticalRecyclerView(coffeList: JSONArray) {

        //kasi masuk constructornya (JSONArray) yang hanya dikasi masuk 1 x
        topAdapter = TopAdapter(coffeList)
        binding.rvTopPicks.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = topAdapter
        }
    }

    private fun setupRecyclerView() {
        coffeeAdapter = CoffeAdapter()
        binding.rvCoffee.apply {
            layoutManager =
                LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = coffeeAdapter
        }
    }

    private fun getCategories() {
        lifecycleScope.launch {
            try {
                val categories = connectionManager.getCoffeCategory()
                setupTabs(categories)
            } catch (e: NetworkException.AuthException) {
                Toast.makeText(this@HomeActivity, "Something Wrong", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@HomeActivity, "Failed ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupTabs(categories: JSONArray) {
        for (i in 0 until categories.length()) {
            val category = categories.getJSONObject(i)
            val tab = binding.tabs.newTab().apply {
                val itemTabChipBinding = ItemTabChipBinding.inflate(layoutInflater)
                itemTabChipBinding.tabText.text = category.getString("name")
                setCustomView(itemTabChipBinding.root)
                tag = category.getInt("id")
            }
            binding.tabs.addTab(tab)

            if (i == 0) {
                binding.tabs.selectTab(tab)
                getCoffeeByCategory(category.getInt("id"))
            }
        }
        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    val categoryID = tab?.tag as Int
                    //ambilID category
                    //terus masukan datanya ke getCoffeeByCategory
                    getCoffeeByCategory(categoryID)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })
    }

    private fun getCoffeeByCategory(id: Int) {
        lifecycleScope.launch {
            try {
                val coffee = connectionManager.getCoffeeByCategory(id)
                //setelah dapat data coffenya di kasi masuk lagi ke adapter
                coffeeAdapter.submitList(coffee)
            } catch (e: NetworkException.AuthException) {
                Toast.makeText(this@HomeActivity, "Unathorized", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(
                    this@HomeActivity,
                    "Failed to Load Coffee List ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


}
