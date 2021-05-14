package kr.co.jolph.soundapp

import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("/predict")
    fun getUser(): Call<JsonElement>
    //fun getUser(@Path(value = "page", encoded = true)page: String ): Call<JsonElement>



}