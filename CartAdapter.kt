package com.example.ezemkofi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.ezemkofi.DetailActivity
import com.example.ezemkofi.Tools.PreferenceHelper
import com.example.ezemkofi.Tools.loadImage
import com.example.myapplication.databinding.ItemCheckoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class CartAdapter(private val preferenceHelper: PreferenceHelper) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {
    private var cartItems: JSONArray = JSONArray()

    inner class CartViewHolder(private val binding: ItemCheckoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: JSONObject) {

            val name = item.getString("name")
            val size = DetailActivity.CoffeeSize.valueOf(item.getString("size"))
            val price = item.getDouble("price")
            val quantity = item.getInt("quantity")
            val category = item.getString("category")

            binding.tvName.text = name
            binding.tvSize.text = "Size ${size.name}"
            binding.tvPrice.text =
                String.format("$%.2f", price * quantity * getPriceMultiplier(size))
            binding.quantity.text = quantity.toString()
            binding.tvSubname.text = category
            CoroutineScope(Dispatchers.IO).launch {
                binding.ivCoffee.loadImage(item.getString("imagePath"))
            }
            binding.plusBtn.setOnClickListener {
                item.put("quantity", quantity + 1)
                updateItem(item)
            }
            binding.minusBtn.setOnClickListener {
                if (quantity > 1) {
                    item.put("quantity", quantity - 1)
                    updateItem(item)
                } else {
                    removeCartItem(item)
                }
            }

        }

        private fun getPriceMultiplier(size: DetailActivity.CoffeeSize): Double {
            return when (size) {
                DetailActivity.CoffeeSize.SMALL -> 0.85
                DetailActivity.CoffeeSize.MEDIUM -> 1.0
                DetailActivity.CoffeeSize.LARGE -> 1.15
            }
        }
    }

    private fun removeCartItem(item: JSONObject) {
        val itemIndex = getItemIndex(item)
        if (itemIndex >= 0) {
            val newCartItems = JSONArray()
            for (i in 0 until cartItems.length()) {
                if (i != itemIndex) {
                    newCartItems.put(cartItems.getJSONObject(i))
                }
            }
            cartItems = newCartItems
            preferenceHelper.saveCart(cartItems)
            notifyItemRemoved(itemIndex)
        }
    }

    private fun updateItem(item: JSONObject) {
        val index = getItemIndex(item)
        if (index >= 0) {
            cartItems.put(index, item)
            preferenceHelper.saveCart(cartItems)
            notifyItemChanged(index)
        }
    }

    private fun getItemIndex(item: JSONObject): Int {
        for (i in 0 until cartItems.length()) {
            val itemDB = cartItems.getJSONObject(i)
            if (itemDB.getInt("coffeID") == item.getInt("coffeID") && itemDB.getString("size") == item.getString("size"))
                return i
        }
        return -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemCheckoutBinding.inflate(layoutInflater, parent, false)
        return CartViewHolder(binding)
    }

    override fun getItemCount() = cartItems.length()

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems.getJSONObject(position)
        holder.bind(cartItem)
    }

    fun setCartItems(items: JSONArray) {
        cartItems = items
        notifyDataSetChanged()
    }
}