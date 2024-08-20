package com.example.ezemkofi.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ezemkofi.DetailActivity
import com.example.ezemkofi.Tools.loadImage
import com.example.myapplication.databinding.ItemCardCoffeeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class CoffeAdapter : RecyclerView.Adapter <CoffeAdapter.CoffeeViewHolder>(){

    private val coffeeList = mutableListOf<JSONObject>()

    class CoffeeViewHolder(private val binding: ItemCardCoffeeBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(coffee: JSONObject) {
            binding.tvName.text = coffee.getString("name")
            binding.tvRate.text = coffee.getDouble("rating").toString()
            binding.tvPrice.text = "$${coffee.getDouble("price")}"
            CoroutineScope(Dispatchers.IO).launch {
                binding.ivCoffee.loadImage(coffee.getString("imagePath"))
            }
            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, DetailActivity::class.java)
                intent.putExtra("coffeeID", coffee.getInt("id"))
                binding.root.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoffeeViewHolder {
        val binding = ItemCardCoffeeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CoffeeViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return coffeeList.size
    }

    override fun onBindViewHolder(holder: CoffeeViewHolder, position: Int) {
        holder.bind(coffeeList[position])
    }

    fun submitList(coffee: JSONArray) {
        coffeeList.clear()
        for (i in 0 until coffee.length()) {
            coffeeList.add(coffee.getJSONObject((i)))
        }
        notifyDataSetChanged()
    }

}