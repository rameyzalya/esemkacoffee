package com.example.ezemkofi

import android.content.Intent
import android.os.Bundle
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
import com.example.ezemkofi.Tools.getSize
import com.example.ezemkofi.adapter.CartAdapter
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityCartBinding
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private lateinit var preferenceHelper: PreferenceHelper
    private lateinit var adapter: CartAdapter
    private var connectionManager= ConnectionManager()
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCartBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        preferenceHelper = PreferenceHelper(this)
        adapter = CartAdapter(preferenceHelper)
        binding.rvCart.layoutManager = LinearLayoutManager(this)
        binding.rvCart.adapter = adapter
        getCartList()
        binding.backBtn.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
        binding.btnCo.setOnClickListener {
            checkout()
        }
    }

    private fun checkout() {
        val cart = preferenceHelper.getCart()
        val jsonArrayReq = JSONArray()
        for (i in 0 until cart.length()){
            val itemPref = cart.getJSONObject(i)
             val newItem = JSONObject().apply {
                put("coffeeId", itemPref.getInt("coffeID"))
                put("size", getSize(itemPref.getString("size")))
                put("qty", itemPref.getInt("quantity"))
            }
            jsonArrayReq.put(newItem)
        }


        if (cart.length() == 0) {
            Toast.makeText(this, "Cart is Empty", Toast.LENGTH_SHORT).show()
            return
        }
        lifecycleScope.launch {
            try {
                val token = preferenceHelper.getToken()

                token?.let { connectionManager.checkout(jsonArrayReq, it) }
                Toast.makeText(this@CartActivity, "Checkout succesfull", Toast.LENGTH_SHORT).show()
                preferenceHelper.clearCart()
                adapter.setCartItems(JSONArray())
            } catch (e: Exception) {
                Toast.makeText(this@CartActivity, "Checkout failed ${e.message}", Toast.LENGTH_SHORT).show()
            } catch (e: NetworkException.AuthException) {
                Toast.makeText(this@CartActivity, "Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCartList() {
        val cartList = preferenceHelper.getCart()
        adapter.setCartItems(cartList)
    }
}