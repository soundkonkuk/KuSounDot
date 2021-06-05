package kr.co.jolph.soundapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.activity_main.*

class CameraActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        //http://192.168.59.150:8090/?action=streamwww.naver.com
        psywebview.apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
        }
        psywebview.loadUrl("www.naver.com")
    }
}