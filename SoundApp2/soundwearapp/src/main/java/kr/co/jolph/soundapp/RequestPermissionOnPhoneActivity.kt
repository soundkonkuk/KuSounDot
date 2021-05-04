package kr.co.jolph.soundapp

import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.util.Log
import android.view.View

class RequestPermissionOnPhoneActivity: WearableActivity() {

    //스마트 폰에서 권한 설정 화면을 열것인지 묻는 Activity

    private val TAG = "RequestPermissionOnPhon"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "OnCreate()")
        setContentView(R.layout.activity_request_permission_on_phone)
        setAmbientEnabled()
    }

    fun onClickPermissionPhoneStorage(view: View?) {
        Log.d(TAG, "onClickPermissionPhoneStorage")
        setResult(RESULT_OK)
        finish()
    }
}