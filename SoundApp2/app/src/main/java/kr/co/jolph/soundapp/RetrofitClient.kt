package kr.co.jolph.soundapp

import android.icu.util.TimeUnit
import com.squareup.okhttp.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    val TAG: String = "LOG"
    private var retrofitClient: Retrofit? = null


    fun getClient(baseUrl: String): Retrofit? {
        if(retrofitClient == null){

            val client: okhttp3.OkHttpClient = okhttp3.OkHttpClient.Builder()
                    .connectTimeout(3000, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(3000, java.util.concurrent.TimeUnit.SECONDS).build()

            retrofitClient = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
        }
        return retrofitClient
    }
}

