package kr.co.jolph.soundapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.util.Log
import kr.co.jolph.soundapp.RetrofitManager
import kotlinx.android.synthetic.main.activity_getresultfromserver.*
import kotlinx.android.synthetic.main.activity_main.*


class Getresultfromserver : AppCompatActivity() {
    val TAG: String = "LOG"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_getresultfromserver)
        get_method_btn.setOnClickListener {
            Log.d(TAG, "겟 메소드 호출")
            RetrofitManager.instance.getUser()

        }
    }
}