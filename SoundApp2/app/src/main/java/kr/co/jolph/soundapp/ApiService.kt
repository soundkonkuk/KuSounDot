package kr.co.jolph.soundapp

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

class PostResult{
    @SerializedName("firstName")
    val firstName = "KUSOUNDOT"
}
var post = PostResult(
)
interface ApiService {
    @GET("/predict")
    //@GET("todos/1")
    fun getUser(): Call<JsonElement>
    @FormUrlEncoded
    @POST("/check")
    fun createUser(@Body post: String) : Call<PostResult?>?
    //fun createUser(@Field("output") firstName :String ) : Call<JsonElement>
}