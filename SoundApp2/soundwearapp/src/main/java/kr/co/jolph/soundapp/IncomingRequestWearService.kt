package kr.co.jolph.soundapp


import android.util.Log
import com.google.android.gms.wearable.*

/*
* 폰에서 들어오는 모든 웨어 데이터 및 권한 요청을 처리
*/


class IncomingRequestWearService : WearableListenerService() {

    private val TAG = "IncomingRequestService"

    fun IncomingRequestWearService() {
        Log.d(TAG, "IncomingRequestWearService()")
    }
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate()")

    }
    override fun onMessageReceived(messageEvent: MessageEvent) {
        Log.d(TAG, "onMessageReceived(): $messageEvent")
        val messagePath = messageEvent.path
    }

}