package com.example.ezemkofi.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ezemkofi.DetailActivity
import com.example.ezemkofi.Tools.loadImage
import com.example.myapplication.databinding.ItemTopPicksBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

//ini butuh constructor karena datanya hanya dikasi 1 kali tidak ada perubahan data
// constructur hanya dipanggil ketika inisialiasi
class TopAdapter(private val coffeeList : JSONArray) : RecyclerView.Adapter<TopAdapter.TopPicksViewHolder>(){

    inner class TopPicksViewHolder(private val binding: ItemTopPicksBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(coffee: JSONObject) {
            binding.tvName.text = coffee.getString("name")
            binding.tvSubname.text = coffee.getString("category")
            binding.tvPrice.text = "$${coffee.getDouble("price")}"
            binding.tvRate.text = coffee.getDouble("rating").toString()
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopPicksViewHolder {
        val binding = ItemTopPicksBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TopPicksViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return coffeeList.length()
    }

    override fun onBindViewHolder(holder: TopPicksViewHolder, position: Int) {
        holder.bind((coffeeList.getJSONObject(position)))
    }
}