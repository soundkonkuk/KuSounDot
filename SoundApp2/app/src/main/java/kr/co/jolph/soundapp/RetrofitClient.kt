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
            retrofitClient = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
        }
        return retrofitClient
    }
}

