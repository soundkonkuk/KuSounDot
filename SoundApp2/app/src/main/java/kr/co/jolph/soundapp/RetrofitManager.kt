package kr.co.jolph.soundapp

import android.util.Log
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

var anan = ""

class RetrofitManager {
    private val channelID="kr.co.jolph.soundapp.channel2"
    val TAG: String = "LOG"
    var KUSOUNDOT: String =""
    var KUSOUNDOTNUM: Int = 3
    companion object {
        val instance = RetrofitManager()
    }
    private val httpCall : ApiService? = RetrofitClient.getClient("http://13.125.229.7:5000")?.create(ApiService::class.java)
    //private val httpCall : ApiService? = RetrofitClient.getClient("https://jsonplaceholder.typicode.com/")?.create(ApiService::class.java)
    fun getUser(){
        val call = httpCall?.getUser()
        call?.enqueue(object : retrofit2.Callback<JsonElement>{
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d(TAG, "RetrofitManager - getTodo() - onFailure() called / t: ${t}")
            }
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                Log.d(TAG, "RetrofitManager - getTodo() - onResponse() called / response: $response")
                Log.d(TAG, "response.body : ${response.body()}")
                KUSOUNDOT = response.body().toString()
                KUSOUNDOTNUM+=1
                print(KUSOUNDOT)
                Log.d(TAG, KUSOUNDOT)
                anan = KUSOUNDOT
            }

        })
    }
    fun returnKUSOUNDOT(): String {
        return KUSOUNDOT;
    }

//    fun createUser(output: String){
//        val call = httpCall?.createUser(output)
//        Log.d("OUTPUTTEST", "${output}")
//        call?.enqueue(object : retrofit2.Callback<JsonElement>{
//            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
//                Log.d(TAG, "post 임!!!RetrofitManager - getTodo() - onFailure() called / t: ${t}")
//            }
//
//            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
//                Log.d(TAG, "post 임!!!RetrofitManager - getTodo() - onResponse() called / response: $response")
//                Log.d(TAG, "post 임!!!response.body : ${response.body()}")
//            }
//        })
//    }
}

private fun <T> Call<T>?.enqueue(callback: Callback<JsonElement>) {
    TODO("Not yet implemented")
}
