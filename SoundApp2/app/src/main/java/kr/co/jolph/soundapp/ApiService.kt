package kr.co.jolph.soundapp

import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    //@GET("/predict")
    @GET("todos/1")
    fun getUser(): Call<JsonElement>

    @FormUrlEncoded
    @POST("todos/1")
    fun createUser(@Field("firstName") firstName :String
    ) : Call<JsonElement>


}