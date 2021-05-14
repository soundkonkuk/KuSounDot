package kr.co.jolph.soundapp

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


// 싱글턴 패턴 개념을 사용하여 retrofit api를 매번 호출하게 하지 않고 object 화 시켰서 한번 만들면 재활용 할수 있게 효율적으로 작성.
object RetrofitClient {

    val TAG: String = "LOG"

    //레트로핏 클라이언트를 선언해서 써뽀자
    private var retrofitClient: Retrofit? = null

    //레트로핏 클라이언트 가져오기 위한 함수를 만들어 보자

    fun getClient(baseUrl: String): Retrofit? {

        // 레트로핏 클라이이언트 없으면.....????
        if(retrofitClient == null){
            retrofitClient = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        //잇으면 그냥 기존꺼 고고

        return retrofitClient

    }

}