package com.example.ezemkofi.Tools

import android.icu.util.Output
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class ConnectionManager {
    val BASE_URL = "http://10.0.2.2:5000/api"

    private suspend inline fun <reified  T> request (
        url: String,
        method: String = "GET",
        requestBody: Any? = null,
        authToken: String? = null
    ): T {
        return withContext(Dispatchers.IO) {
            val conn = URL(url).openConnection() as  HttpURLConnection
            conn.requestMethod = method
            conn.setRequestProperty("Content-Type", "application/json")
            authToken?.let { conn.setRequestProperty("Authorization", "Bearer $it") }

            if (requestBody != null) {
                conn.doOutput = true
                OutputStreamWriter(conn.outputStream).use {
                    it.write(requestBody.toString())
                }
            }
            val code = conn.responseCode
            val body = conn.inputStream?.bufferedReader()?.use { it.readText() }
            if (code in 200 until 300) {
                when (T::class) {
                    String::class -> body as T
                    JSONObject::class -> JSONObject(body) as T
                    JSONArray::class -> JSONArray(body) as T
                    else -> throw  IllegalStateException("Unsupported Type")
                }
            } else {
                val errorBody = conn.errorStream?.bufferedReader()?.use { it.readText() }
                val errorMessage = JSONObject (errorBody?: "").optString(
                    "message",
                    "Request failed with code $code"
                )
                throw handleHttpException(code, errorMessage)
            }
        }
    }

    private fun handleHttpException(code: Int, message: String): Exception {
        return when (code) {
            401, 403 -> NetworkException.AuthException()
            else -> NetworkException.IgnorableException(message)
        }
    }
    suspend fun login (username: String, password: String): String {
        val requestBody = JSONObject().apply {
            put("username", username)
            put("password", password)
        }
        return request("$BASE_URL/auth", "POST", requestBody)
    }
    suspend fun getCoffeCategory(): JSONArray {
        return request("$BASE_URL/coffee-category", "GET")
    }
    suspend fun getCoffeeBySearch(Search : String):JSONArray {
        return request("$BASE_URL/coffee?search=$Search", "GET")
    }
    suspend fun getCoffeeByCategory(categoryId : Int):JSONArray {
        return request("$BASE_URL/coffee?coffeeCategoryID=$categoryId", "GET")
    }
    suspend fun getCoffeeTopPicks() : JSONArray {
        return request("$BASE_URL/coffee/top-picks", "GET")
    }
    suspend fun getCoffeeDetail(coffeeID: Int): JSONObject {
        return request("$BASE_URL/coffee/$coffeeID")
    }
    suspend fun getMe(token: String) : JSONObject {
        return request("$BASE_URL/me", "GET", null, token)
    }
    suspend fun register(username: String, fullname: String, email: String, password: String) : String {
        val requestBody = JSONObject().apply {
            put("username", username)
            put("fullname", fullname)
            put("email", email)
            put("password", password)
        }
        return request("$BASE_URL/register", "POST", requestBody)
    }
    suspend fun checkout(cart: JSONArray, token : String): String {
        return request("$BASE_URL/checkout", "POST", cart, token)
    }
}