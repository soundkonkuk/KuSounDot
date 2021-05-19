package kr.co.jolph.soundapp

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.wear.ambient.AmbientModeSupport
import com.google.android.gms.wearable.*
import kotlinx.android.synthetic.main.activity_soundauthority.*
import kr.co.jolph.soundapp.RequestPermissionOnPhoneActivity

class SoundAuthorityActivity : FragmentActivity(), AmbientModeSupport.AmbientCallbackProvider,
        MessageClient.OnMessageReceivedListener,
        ActivityCompat.OnRequestPermissionsResultCallback{


    val KEY_COMM_TYPE = "communicationType"
    val KEY_PAYLOAD = "payload"

    // Requests
    val COMM_TYPE_REQUEST_PROMPT_PERMISSION = 1
    val COMM_TYPE_REQUEST_DATA = 2

    // Responses
    val COMM_TYPE_RESPONSE_PERMISSION_REQUIRED = 1001
    val COMM_TYPE_RESPONSE_USER_APPROVED_PERMISSION = 1002
    val COMM_TYPE_RESPONSE_USER_DENIED_PERMISSION = 1003
    val COMM_TYPE_RESPONSE_DATA = 1004

    // Phone
    val CAPABILITY_PHONE_APP = "sound_phone_app"
    val MESSAGE_PATH_PHONE = "/phone_message_path"

    // Wear
    val MESSAGE_PATH_WEAR = "/wear_message_path"

    //mobileAppCheck에서 찾은 노드를 가져옴

    companion object{
        //SoundAuthorityActivity
        private val TAG = "SoundAuthorityActivity"
        private var mAmbientController: AmbientModeSupport.AmbientController? = null
        /* Id to identify starting/closing RequestPermissionOnPhoneActivity (startActivityForResult). */
        private val REQUEST_PHONE_PERMISSION = 1
        private var mPhonePermissionApproved = false
        private var mPhoneStoragePermissionButton: Button? = null
        private var mPhoneNodeId: String? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate()")
        super.onCreate(savedInstanceState)

        // 원격 권한이기 때문에 false로 초기화한 다음 Google Api Client가 연결되면 원격 권한을 확인합니다.
        mPhonePermissionApproved = false
        setContentView(R.layout.activity_soundauthority)

        // Enables Ambient mode.
        mAmbientController = AmbientModeSupport.attach(this)

        mPhoneStoragePermissionButton = findViewById(R.id.phone_permission_button)
        mPhoneNodeId = intent.getStringExtra("Phone Node")
    }

    fun onClickPhonePermission(view: View?) {
        logToUi("Requested info from phone. New approval may be required.")
        val dataMap = DataMap()
        dataMap.putInt(KEY_COMM_TYPE, COMM_TYPE_REQUEST_DATA)
        sendMessage(dataMap)
    }

    override fun onPause() {
        Log.d(TAG, "onPause()")
        super.onPause()
        Wearable.getMessageClient(this).removeListener(this)

    }

    override fun onResume() {
        Log.d(TAG, "onResume()")
        super.onResume()
        Wearable.getMessageClient(this).addListener(this)

    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        Log.d(TAG, "onMessageReceived(): $messageEvent")
        val messagePath = messageEvent.path
        if (messagePath == MESSAGE_PATH_WEAR) {
            val dataMap = DataMap.fromByteArray(messageEvent.data)
            val commType = dataMap.getInt(KEY_COMM_TYPE, 0)
            if (commType == COMM_TYPE_RESPONSE_PERMISSION_REQUIRED) {
                mPhonePermissionApproved = false
                updatePhoneButtonOnUiThread()
                val phonePermissionRationaleIntent = Intent(
                        this,
                        RequestPermissionOnPhoneActivity::class.java
                )
                startActivityForResult(phonePermissionRationaleIntent, REQUEST_PHONE_PERMISSION)
            } else if (commType == COMM_TYPE_RESPONSE_USER_APPROVED_PERMISSION) {
                mPhonePermissionApproved = true
                updatePhoneButtonOnUiThread()
                logToUi("User approved permission on remote device, requesting data again.")
                val outgoingDataRequestDataMap = DataMap()
                outgoingDataRequestDataMap.putInt(KEY_COMM_TYPE, COMM_TYPE_REQUEST_DATA)
                sendMessage(outgoingDataRequestDataMap)
            } else if (commType == COMM_TYPE_RESPONSE_USER_DENIED_PERMISSION) {
                mPhonePermissionApproved = false
                updatePhoneButtonOnUiThread()
                logToUi("User denied permission on remote device.")
            } else if (commType == COMM_TYPE_RESPONSE_DATA) {
                mPhonePermissionApproved = true
                val storageDetails = dataMap.getString(KEY_PAYLOAD)
                updatePhoneButtonOnUiThread()
                logToUi(storageDetails)
            }
        }
    }

    private fun sendMessage(dataMap: DataMap) {
        Log.d(TAG, "sendMessage(): $mPhoneNodeId")
        val sendMessageTask = Wearable.getMessageClient(this)
                .sendMessage(mPhoneNodeId!!, MESSAGE_PATH_PHONE, dataMap.toByteArray())
        sendMessageTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "Message sent successfully")
            } else {
                Log.d(TAG, "Message failed.")
            }
        }
        updatePhoneButtonOnUiThread()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Check which request we're responding to
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PHONE_PERMISSION) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                logToUi("Requested permission on phone.")
                val dataMap = DataMap()
                dataMap.putInt(KEY_COMM_TYPE, COMM_TYPE_REQUEST_PROMPT_PERMISSION)
                sendMessage(dataMap)
            }
        }
    }

    override fun getAmbientCallback(): AmbientModeSupport.AmbientCallback? {
        return MyAmbientCallback()
    }

    private class MyAmbientCallback : AmbientModeSupport.AmbientCallback() {
        /** Prepares the UI for ambient mode.  */
        override fun onEnterAmbient(ambientDetails: Bundle) {
            super.onEnterAmbient(ambientDetails)
            Log.d(TAG, "onEnterAmbient() $ambientDetails")

        }

        /** Restores the UI to active (non-ambient) mode.  */
        override fun onExitAmbient() {
            super.onExitAmbient()
            Log.d(TAG, "onExitAmbient()")

        }
    }

    private fun logToUi(message: String) {
        val mainUiThread = Looper.myLooper() == Looper.getMainLooper()
        if (mainUiThread) {
            if (message.isNotEmpty()) {
                Log.d(TAG, message)
                output.text = message
            }
        } else {
            runOnUiThread {
                if (message.isNotEmpty()) {
                    Log.d(TAG, message)
                    output.text = message
                }
            }
        }
    }

    private fun updatePhoneButtonOnUiThread() {
        runOnUiThread {
            if (mPhonePermissionApproved) {
                phone_permission_button.isEnabled = false
                phone_permission_button.text = "권한허용 완료"
                text_permission_result.text = "스마트폰의 소리권한이 허용되어 있습니다!"

            } else {
                phone_permission_button.isEnabled = true
                phone_permission_button.text = "Phone Permission"
                text_permission_result.text = "*스마트폰의 소리권한을 설정할 수 있습니다.*"
            }
        }

    }

}