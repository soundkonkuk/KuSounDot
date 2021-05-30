package kr.co.jolph.soundapp

import com.google.gson.JsonElement
import com.squareup.okhttp.OkHttpClient
import retrofit2.Call
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface ApiService {

    @GET("/predict")//우리꺼
    //@GET("todos/1")
    fun getUser(): Call<JsonElement>

    @FormUrlEncoded
    @POST("todos/1")
    fun createUser(@Field("firstName") firstName :String
    ) : Call<JsonElement>

}