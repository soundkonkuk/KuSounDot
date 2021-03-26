package kr.co.jolph.soundapp

import android.content.Intent
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class WearMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wear_main)

        var BtnWatchSetting = findViewById<View>(R.id.btn_watch_setting) as Button
        var BtnMobileSetting = findViewById<View>(R.id.btn_mobile_setting) as Button

        //스마트워치에서 앱 알림 설정 가이드
        BtnWatchSetting.setOnClickListener(View.OnClickListener {
            val watchIntent: Intent = Intent(this@WearMainActivity, ExplainWatchSettingActivity::class.java)
            startActivity(watchIntent)
        })

        //Mobile App 연결, 설치 여부 & Mobile 소리권한
        BtnMobileSetting.setOnClickListener {
            val mobileappcheckIntent: Intent = Intent(this@WearMainActivity,MobileAppCheckActivity::class.java )
            startActivity(mobileappcheckIntent)
        }


    }
}