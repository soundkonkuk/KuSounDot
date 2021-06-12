package kr.co.jolph.soundapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.wear.ambient.AmbientModeSupport
import androidx.wear.phone.interactions.PhoneTypeHelper
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import com.google.android.wearable.intent.RemoteIntent


class MobileAppCheckActivity : FragmentActivity() ,
        CapabilityClient.OnCapabilityChangedListener{

    companion object {
        private val TAG = "MobileAppCheckActivity"
        private const val Welcom_Message = "소리녹음 웨어 앱에 오신걸 환영합니다!\n"
        private const val Check_Message = "${Welcom_Message}모바일과의 연결을 확인중입니다..."
        private const val Missing_All_Message = "${Welcom_Message}핸드폰에 소리녹음 모바일 앱이 설치되어있지 않습니다.\n 아래 버튼을 클릭해서 모바일 앱을 설치해 주세요!"
        private const val Install_WearApp_Message = "$Welcom_Message(%s) 핸드폰에 모바일 앱이 설치되었습니다!\n"
        //CAPABILITY_PHONE_APP-> Mobile App > values > wear.xml
        private const val CAPABILITY_PHONE_APP = "sound_phone_app"
        // Links to install mobile app for both Android (Play Store) and iOS.
        // TODO: Replace with your links/packages.
        private const val ANDROID_MARKET_APP_URI = "market://details?id=com.example.android.wearable.wear.wearverifyremoteapp"
        // TODO: Replace with your links/packages.
        private const val APP_STORE_APP_URI = "https://itunes.apple.com/us/app/android-wear/id986496028?mt=8"

        private var mInformationTextView: TextView? = null
        private var mRemoteOpenButton: Button? = null
        private var mAndroidPhoneNodeWithApp: Node? = null
    }

    // RemoteIntent: Support for opening android intents on other devices.
    // Result from sending RemoteIntent to phone to open app in play/app store.
    private val mResultReceiver: ResultReceiver = object : ResultReceiver(Handler()) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
            if (resultCode == RemoteIntent.RESULT_OK) {
                android.support.wearable.view.ConfirmationOverlay().showOn(this@MobileAppCheckActivity)
            } else if (resultCode == RemoteIntent.RESULT_FAILED) {
                android.support.wearable.view.ConfirmationOverlay()
                        .setType(android.support.wearable.view.ConfirmationOverlay.FAILURE_ANIMATION)
                        .showOn(this@MobileAppCheckActivity)
            } else {
                throw IllegalStateException("Unexpected result $resultCode")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate()")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mobileappcheck)

        // Ambient Mode
        // 충전 중인 스마트폰이나 태블릿 화면에 다양한 알림이나 스마트홈 조작 패널을 표시해 마치 스마트 디스플레이처럼 활용할 수 있게 해주는 기능.

        // Enables Ambient mode.
        AmbientModeSupport.attach(this)
        mInformationTextView = findViewById(R.id.information_text_view)
        mRemoteOpenButton = findViewById(R.id.remote_open_button)
        mInformationTextView?.setText(Check_Message)
        mRemoteOpenButton?.setOnClickListener(View.OnClickListener { openAppInStoreOnPhone() })
    }

    override fun onPause() {
        Log.d(TAG, "onPause()")
        super.onPause()
        Wearable.getCapabilityClient(this).removeListener(this, CAPABILITY_PHONE_APP)
    }

    override fun onResume() {
        Log.d(TAG, "onResume()")
        super.onResume()
        Wearable.getCapabilityClient(this).addListener(this, CAPABILITY_PHONE_APP)
        checkIfPhoneHasApp()
    }

    override fun onCapabilityChanged(capabilityInfo: CapabilityInfo) {
        Log.d(TAG, "onCapabilityChanged(): $capabilityInfo")
        mAndroidPhoneNodeWithApp = pickBestNodeId(capabilityInfo.nodes)
        verifyNodeAndUpdateUI()
    }

    private fun checkIfPhoneHasApp() {
        Log.d(TAG, "checkIfPhoneHasApp()")
        val capabilityInfoTask = Wearable.getCapabilityClient(this)
                .getCapability(CAPABILITY_PHONE_APP, CapabilityClient.FILTER_ALL)
        capabilityInfoTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "Capability request succeeded.")
                val capabilityInfo = task.result
                mAndroidPhoneNodeWithApp = pickBestNodeId(capabilityInfo!!.nodes)
            } else {
                Log.d(TAG, "Capability request failed to return any results.")
            }
            verifyNodeAndUpdateUI()
        }
    }

    private fun verifyNodeAndUpdateUI() {
        if (mAndroidPhoneNodeWithApp != null) {

            // Mobile App 설치완료
            val installMessage: String = kotlin.String.format(Install_WearApp_Message, mAndroidPhoneNodeWithApp!!.displayName)
            Log.d(TAG, installMessage)
            mInformationTextView!!.text = installMessage
            mRemoteOpenButton!!.visibility = View.INVISIBLE

            //소리권한 액티비티로 이동 + 찾은 Phone Node도 전달
            val phoneNode: String = mAndroidPhoneNodeWithApp!!.id

            val handler = Handler()
            handler.postDelayed({
                val soundIntent: Intent = Intent(this@MobileAppCheckActivity, SoundAuthorityActivity::class.java)
                soundIntent.putExtra("Phone Node", phoneNode)
                startActivity(soundIntent)

                //웨어 앱에서 해당 액티비티 종료
                finish()

            }, 1300) //딜레이 타임 조절



        } else {
            Log.d(TAG, Missing_All_Message)
            mInformationTextView?.setText(Missing_All_Message)
            mRemoteOpenButton!!.visibility = View.VISIBLE
        }
    }


    //웨어와 연결된 휴대폰 종류에 따라 다른 스토어앱 open
    private fun openAppInStoreOnPhone() {
        Log.d(TAG, "openAppInStoreOnPhone()")
        //PhoneTypeHelper: 페어링된 휴대폰 종류(유형) 찾을때 사용하는 클래스
        val phoneDeviceType = PhoneTypeHelper.getPhoneDeviceType(applicationContext)
        when (phoneDeviceType) {
            //안드로이드일때
            PhoneTypeHelper.DEVICE_TYPE_ANDROID -> {
                Log.d(TAG, "\tDEVICE_TYPE_ANDROID")
                // Create Remote Intent to open Play Store listing of app on remote device.
                val intentAndroid = Intent(Intent.ACTION_VIEW)
                        .addCategory(Intent.CATEGORY_BROWSABLE)
                        .setData(Uri.parse(ANDROID_MARKET_APP_URI))
                RemoteIntent.startRemoteActivity(
                        applicationContext,
                        intentAndroid,
                        mResultReceiver)
            }
            //IOS
            PhoneTypeHelper.DEVICE_TYPE_IOS -> {
                Log.d(TAG, "\tDEVICE_TYPE_IOS")

                // Create Remote Intent to open App Store listing of app on iPhone.
                val intentIOS = Intent(Intent.ACTION_VIEW)
                        .addCategory(Intent.CATEGORY_BROWSABLE)
                        .setData(Uri.parse(APP_STORE_APP_URI))
                RemoteIntent.startRemoteActivity(
                        applicationContext,
                        intentIOS,
                        mResultReceiver)
            }
            PhoneTypeHelper.DEVICE_TYPE_ERROR or PhoneTypeHelper.DEVICE_TYPE_UNKNOWN -> Log.d(TAG, "\tDEVICE_TYPE_ERROR_UNKNOWN")
        }
    }


    //노드 집합에는 하나의 핸드폰만 있어야함. 그래서 가장 첫번째 것만 잡기위한 메소드
    //웨어러블 기기와 연결된 모바일 디바이스는 하나!
    private fun pickBestNodeId(nodes: Set<Node>): Node? {
        Log.d(TAG, "pickBestNodeId(): $nodes")
        var bestNodeId: Node? = null
        // 근처 노드 찾기
        for (node in nodes) {
            bestNodeId = node
        }
        return bestNodeId
    }









}