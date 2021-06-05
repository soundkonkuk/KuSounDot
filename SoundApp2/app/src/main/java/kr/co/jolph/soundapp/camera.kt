package kr.co.jolph.soundapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.fragment_camera.*

class camera : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_camera, container, false)
        val mWebView = view.findViewById(R.id.webView1) as WebView
        mWebView.loadUrl("http://192.168.147.150:8090/?action=stream")
        val webSettings = mWebView.getSettings()
        webSettings.setJavaScriptEnabled(true)
        webSettings.setSupportMultipleWindows(false)// 새창 띄우기 허용 여부
        webSettings.javaScriptCanOpenWindowsAutomatically = false // 자바스크립트 새창 띄우기(멀티뷰) 허용 여부
        webSettings.loadWithOverviewMode = true // 메타태그 허용 여부
        webSettings.useWideViewPort = true // 화면 사이즈 맞추기 허용 여부
        webSettings.setSupportZoom(true) // 화면 줌 허용 여부
        webSettings.builtInZoomControls = false // 화면 확대 축소 허용 여부
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE // 브라우저 캐시 허용 여부
        webSettings.domStorageEnabled = true // 로컬저장소 허용 여부
        mWebView.setWebViewClient(WebViewClient())
        return view
    }

}
