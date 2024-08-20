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
import com.example.ezemkofi.Tools.ConnectionManager
import com.example.ezemkofi.Tools.PreferenceHelper
import com.example.ezemkofi.Tools.loadImage
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityDetailBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val connectionManager = ConnectionManager()
    private lateinit var preferenceHelper: PreferenceHelper
    private var price: Double = 0.0
    private var selectedSize: CoffeeSize = CoffeeSize.MEDIUM
    private var quantity: Int = 1
    private var name : String = ""
    private var imagePath : String = ""
    private var category : String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        preferenceHelper = PreferenceHelper(this)
        setContentView(binding.root)
        val coffeeID = intent.getIntExtra("coffeeID", 0)
        setupActionBar()
        setupButton()
        setupQuantityButton()
        getDetail(coffeeID)
        binding.btnAddCard.setOnClickListener {
            addToCart(coffeeID)
        }
    }

    private fun addToCart(coffeeID: Int) {
        val cart = preferenceHelper.getCart()
        val size = selectedSize.name
        var itemIndex = -1

        for (i in 0 until cart.length()) {
            val item = cart.getJSONObject(i)
            if (item.getInt("coffeID") == coffeeID && item.getString("size") == size) {
                itemIndex = i
                break
            }
        }

        if (itemIndex >= 0) {
            val existingItem = cart.getJSONObject(itemIndex)
            val newQuantity = existingItem.getInt("quantity") + quantity
            existingItem.put("quantity", newQuantity)
            cart.put(itemIndex, existingItem)
        } else {
            val newItem = JSONObject().apply {
                put("coffeID", coffeeID)
                put("size", size)
                put("quantity", quantity)
                put("price", price)
                put("name", name)
                put("category", category)
                put("imagePath", imagePath)
            }
            cart.put(newItem)
        }
        preferenceHelper.saveCart(cart)
        startActivity(Intent(this, CartActivity::class.java))
    }

    private fun setupQuantityButton() {
        binding.plusBtn.setOnClickListener {
            quantity++
            updateQuantity()
            updatePrice()
        }
        binding.minusBtn.setOnClickListener {
            if (quantity > 1) {
                quantity--
                updateQuantity()
                updatePrice()
            }
        }
    }

    private fun updateQuantity() {
        binding.quantity.text = quantity.toString()
    }

    private fun setupButton() {
        binding.btnSmall.setOnClickListener {
            selectSize(CoffeeSize.SMALL)
        }
        binding.btnMedium.setOnClickListener {
            selectSize(CoffeeSize.MEDIUM)
        }
        binding.btnLarge.setOnClickListener {
            selectSize(CoffeeSize.LARGE)
        }
        selectSize(CoffeeSize.MEDIUM)
    }

    private fun selectSize(size: CoffeeSize) {
        selectedSize = size
        updatePrice()
        updateColorButton()
        animateCoffeeImage()
    }

    private fun animateCoffeeImage() {
        val scale = when (selectedSize) {
            CoffeeSize.SMALL -> 0.85f
            CoffeeSize.MEDIUM -> 1.0f
            CoffeeSize.LARGE -> 1.15f
        }
        binding.ivCoffee.animate()
            .rotationBy(360f)
            .scaleX(scale)
            .scaleY(scale)
            .setDuration(300)
            .start()
    }

    private fun updateColorButton() {
        val buttons = listOf(binding.btnSmall, binding.btnMedium, binding.btnLarge)
        buttons.forEach { button ->
            val isSelected = when (button.id) {
                R.id.btn_small -> selectedSize == CoffeeSize.SMALL
                R.id.btn_medium -> selectedSize == CoffeeSize.MEDIUM
                R.id.btn_large -> selectedSize == CoffeeSize.LARGE
                else -> false
            }
            button.setBackgroundColor(if (isSelected) getColor(R.color.ezem_green) else getColor(R.color.ezem_light_gray))
            button.setTextColor(if (isSelected) getColor(R.color.white) else (getColor(R.color.ezem_green)))
        }
    }

    private fun updatePrice() {
        val priceBaseOnSize = when (selectedSize) {
            CoffeeSize.SMALL -> price * 0.85
            CoffeeSize.MEDIUM -> price
            CoffeeSize.LARGE -> price * 1.15
        }
        val totalPrice = priceBaseOnSize * quantity
        binding.tvPrice.text = String.format("$%.2f", totalPrice)
    }

    private fun getDetail(coffeeID: Int) {
        lifecycleScope.launch {
            try {
                val coffee = connectionManager.getCoffeeDetail(coffeeID)
                displayData(coffee)
                price = coffee.getDouble("price")
                name = coffee.getString("name")
                category = coffee.getString("category")
                imagePath = coffee.getString("imagePath")
            } catch (e: Exception) {
                Toast.makeText(this@DetailActivity, "Failed ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun displayData(coffee: JSONObject) {
        binding.tvName.text = coffee.getString("name")
        binding.tvDesc.text = coffee.getString("description")
        binding.tvPrice.text = "$${coffee.getDouble("price")}"
        binding.rate.text = coffee.getDouble("rating").toString()
        CoroutineScope(Dispatchers.IO).launch {
            binding.ivCoffee.loadImage(coffee.getString("imagePath"))
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.appbar.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.appbar.titleToolbar.text = "Details"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    enum class CoffeeSize {
        SMALL, MEDIUM, LARGE
    }
}