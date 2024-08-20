package com.example.ezemkofi.Tools

import android.graphics.BitmapFactory
import android.media.Image
import android.util.Log
import android.widget.ImageView
import com.example.myapplication.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

suspend fun ImageView.loadImage(imagePath: String) {
    val imageUrl = "http://10.0.2.2:5000/images/$imagePath"
    Log.d("TAG", "imagePath: $imageUrl")

    withContext(Dispatchers.IO) {
        try {
            val inputStream = URL(imageUrl).openStream()
            val bitmap = BitmapFactory.decodeStream(inputStream)
            withContext(Dispatchers.Main) {
                this@loadImage.setImageBitmap(bitmap)
            }
        } catch (e: Exception) {
            Log.d("TAG", "imagePath2: ${e.message}")
            withContext(Dispatchers.Main) {
                this@loadImage.setImageResource(R.drawable.ic_launcher_background)
            }
        }
    }
}

fun getSize(size : String) : String{

    when(size){
        "SMALL" -> return  "S"
        "MEDIUM" -> return  "M"
        "LARGE" -> return  "L"
        else -> return  ""
    }

}