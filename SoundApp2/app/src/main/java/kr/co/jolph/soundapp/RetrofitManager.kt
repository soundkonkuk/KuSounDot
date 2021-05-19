package kr.co.jolph.soundapp

import android.util.Log
import com.google.gson.JsonElement
import com.squareup.okhttp.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import java.util.concurrent.TimeUnit

class RetrofitManager {
    val TAG: String = "LOG"
    var KUSOUNDOT: String =""
    companion object {
        val instance = RetrofitManager()
    }
    //private val httpCall : ApiService? = RetrofitClient.getClient("http://13.125.229.7:5000")?.create(ApiService::class.java)
    private val httpCall : ApiService? = RetrofitClient.getClient("https://jsonplaceholder.typicode.com/")?.create(ApiService::class.java)

    fun getUser() {

        val call = httpCall?.getUser()
        call?.enqueue(object : retrofit2.Callback<JsonElement>{
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d(TAG, "RetrofitManager - getTodo() - onFailure() called / t: ${t}")
            }
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                Log.d(TAG, "RetrofitManager - getTodo() - onResponse() called / response: $response")
                Log.d(TAG, "response.body : ${response.body()}")
                KUSOUNDOT = response.body().toString()
                print(KUSOUNDOT)
                Log.d(TAG, KUSOUNDOT)
            }
        })
    }
    fun returnKUSOUNDOT(): String {
        return KUSOUNDOT;
    }
    fun getUser2() {
        val call = httpCall?.getUser()
        call?.enqueue(object : retrofit2.Callback<JsonElement>{
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d(TAG, "RetrofitManager - getTodo() - onFailure() called / t: ${t}")
            }
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
            }
        })
    }
    fun createUser(firstName: String){
        val call = httpCall?.createUser(firstName)
        call?.enqueue(object : retrofit2.Callback<JsonElement>{
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d(TAG, "RetrofitManager - getTodo() - onFailure() called / t: ${t}")
            }

            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                Log.d(TAG, "RetrofitManager - getTodo() - onResponse() called / response: $response")
                Log.d(TAG, "response.body : ${response.body()}")
            }

        })
    }
}
