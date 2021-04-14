package kr.co.jolph.soundapp

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService

/**
 * 웨어 장치에서 폰 데이터(및 권한)에 대한 모든 수신 요청을 처리
 */

class IncomingRequestPhoneService : WearableListenerService() {

    private val TAG = "IncomingRequestPhone"
    val watchFrag = watch

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate()")
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)
        Log.d(TAG, "onMessageReceived(): $messageEvent")
        val messagePath = messageEvent.path
        if (messagePath == watchFrag.MESSAGE_PATH_PHONE) {
            val dataMap = DataMap.fromByteArray(messageEvent.data)
            val requestType = dataMap.getInt(watchFrag.KEY_COMM_TYPE, 0)
            if (requestType == watchFrag.COMM_TYPE_REQUEST_PROMPT_PERMISSION) {
                promptUserForStoragePermission(messageEvent.sourceNodeId)
            } else if (requestType == watchFrag.COMM_TYPE_REQUEST_DATA) {
                respondWithStorageInformation(messageEvent.sourceNodeId)
            }
        }
    }

    private fun promptUserForStoragePermission(nodeId: String) {
        Log.d(TAG, "promptStoragePermission")
        val PermissionApproved = (ContextCompat.checkSelfPermission(this,
            Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        if (!PermissionApproved) {
            val dataMap = DataMap()
            dataMap.putInt(watchFrag.KEY_COMM_TYPE,
                watchFrag.COMM_TYPE_RESPONSE_USER_APPROVED_PERMISSION)
            sendMessage(nodeId, dataMap)
        } else {
            // Launch Phone Activity to grant storage permissions.
            val startIntent = Intent(this, PhonePermissionRequestActivity::class.java)
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startIntent.putExtra(watchFrag.EXTRA_PROMPT_PERMISSION_FROM_WEAR, true)
            startActivity(startIntent)
        }
    }

    private fun respondWithStorageInformation(nodeId: String) {
        Log.d(TAG, "respondwithStorageInformation")
        val PermissionApproved = (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)
        if (!PermissionApproved) {
            val dataMap = DataMap()
            dataMap.putInt(watchFrag.KEY_COMM_TYPE,watchFrag.COMM_TYPE_RESPONSE_PERMISSION_REQUIRED)
            sendMessage(nodeId, dataMap)
        } else {

            val stringBuilder = StringBuilder()

            // Send valid results
            val dataMap = DataMap()
            dataMap.putInt(watchFrag.KEY_COMM_TYPE,watchFrag.COMM_TYPE_RESPONSE_DATA)
            dataMap.putString(watchFrag.KEY_PAYLOAD, stringBuilder.toString())
            sendMessage(nodeId, dataMap)
        }
    }

    private fun sendMessage(nodeId: String, dataMap: DataMap) {
        Log.d(TAG, "sendMessage() Node: $nodeId")

        val sendMessageTask = Wearable.getMessageClient(
            applicationContext).sendMessage(
            nodeId,
            watchFrag.MESSAGE_PATH_WEAR,
            dataMap.toByteArray())
        sendMessageTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "Message sent successfully")
            } else {
                Log.d(TAG, "Message failed.")
            }
        }
    }

}