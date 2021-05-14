package kr.co.jolph.soundapp

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.*

interface MyAPI {
    @Multipart
    @POST("hello")
    fun uploadImage(
            @Part image: MultipartBody.Part,
            @Part("desc") desc: RequestBody
    ): Call<UploadResponse>
    fun uploadSound(
        @Part sound: MultipartBody.Part,
        @Part("desc") desc: RequestBody
    ): Call<UploadResponse>
//    @Multipart
//    @POST("upload")
//    Call<RequestBody> uploadImage(@Part MultipartBody.Part part,
//    @Part("somedata") RequestBody requestBody);
    companion object {
        operator fun invoke(): MyAPI {
            return Retrofit.Builder()
                    .baseUrl("http://203.252.166.239:5002/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(MyAPI::class.java)
        }
    }
}