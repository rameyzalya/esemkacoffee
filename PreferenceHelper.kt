package com.example.ezemkofi.Tools

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray

class PreferenceHelper(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    fun setToken(token: String) {
        sharedPreferences.edit().putString("auth_token", token).apply()
    }
    fun getToken(): String? {
        return sharedPreferences.getString("auth_token", null)
    }
    fun clearToken() {
        sharedPreferences.edit().remove("auth_token").apply()
    }
    fun saveCart(cart: JSONArray) {
        sharedPreferences.edit().putString("cart", cart.toString()).apply()
    }
    fun getCart(): JSONArray {
        val cartString = sharedPreferences.getString("cart", null)
        return if (cartString != null) {
            JSONArray(cartString)
        } else {
            JSONArray()
        }
    }
    fun clearCart() {
        sharedPreferences.edit().remove("cart").apply()
    }
}