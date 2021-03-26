package kr.co.jolph.soundapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.ImageView
import com.bumptech.glide.Glide

class ExplainWatchSettingActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explain)

        val mContext = this
        val btn_noti_setting = findViewById<Button>(R.id.btn_noti_setting)
        val gif_image = findViewById<ImageView>(R.id.gif_image)

        Glide.with(this).load(R.drawable.explain).into(gif_image)

        btn_noti_setting.setOnClickListener {
            mContext.startActivity(Intent(Settings.ACTION_APPLICATION_SETTINGS))
        }
    }

}