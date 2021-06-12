package kr.co.jolph.soundapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.*
import com.google.android.gms.wearable.R
import com.google.android.wearable.intent.RemoteIntent
import kotlinx.android.synthetic.main.fragment_watch.*


class watch : Fragment(), CapabilityClient.OnCapabilityChangedListener
        , MessageClient.OnMessageReceivedListener, OnCompleteListener<Int> {

    companion object {
        //parameters
        private val TAG: String? = "watch"
        private const val Welcom_Message = "KUSOUNDOT 모바일 앱에 오신걸 환영합니다!\n\n"
        private const val Check_Message = "${Welcom_Message}시계연결을 확인중입니다..."
        private const val No_Device = "${Welcom_Message}핸드폰과 연결되어 있는 시계가 없습니다. \n\n 시계를 연결하고 소리알림을 어디서든 받아보세요!"
        private const val Missing_All_Message = """${Welcom_Message}시계에 소리녹음 웨어앱이 설치되어있지 않습니다.아래 버튼을 클릭해서 웨어앱을 설치해 주세요!"""
        private const val Install_WearApp_Message ="${Welcom_Message} 시계에 웨어앱이 설치되었습니다!\n\n"
        //CAPABILITY_WEAR_APP -> Wear App > values > wear.xml
        private const val CAPABILITY_WEAR_APP = "sound_wear_app"

        // TODO: Replace with Links/packages
        private const val PLAY_STORE_APP_URI = "https://drive.google.com/file/d/1hPF8WF2vHLwu_c3JRhQFr_6rtVXOoFdt/view?usp=sharing"

        private var mWearNodesWithApp: Set<Node>? = null
        private var mAllConnectedNodes: List<Node>? = null

        //Message
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

        //Message
        val EXTRA_PROMPT_PERMISSION_FROM_WEAR: String? =
                "kr.co.jolph.soundapp.extra.PROMPT_PERMISSION_FROM_WEAR"
        var capabilityName: String = "capabilityName"
        private const val REQUEST_WEAR_PERMISSION_RATIONALE = 1

        private var mPhonePermissionApproved = false
        private var mWearRequestingPhoneStoragePermission = false
        private var mOutputTextView: TextView? = null
        private val mWearNodeIds: Set<Node>? = null
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(kr.co.jolph.soundapp.R.layout.fragment_watch, container, false)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        information_text_view?.text = Check_Message
        remote_open_button?.setOnClickListener {
            //openPlayStoreOnWearDevicesWithoutApp()
        }

        //Message
        val intent = Intent(activity, MainActivity::class.java)

        mWearRequestingPhoneStoragePermission = intent.getBooleanExtra(EXTRA_PROMPT_PERMISSION_FROM_WEAR, false)
    }

    override fun onPause() {
        Log.d(TAG, "onPause()")
        super.onPause()
        activity?.let { Wearable.getCapabilityClient(it).removeListener(this, CAPABILITY_WEAR_APP) }
        activity?.let{Wearable.getMessageClient(it).removeListener(this)}
    }

    //사용자와 상호작용 하는 단계 / Activity 스택의 Top에 위치 / 주로 어플 기능이 onResume()에 설정됨
    override fun onResume() {
        Log.d(TAG, "onResume()")
        super.onResume()


        mPhonePermissionApproved = (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)

        activity?.application?.let{
            Wearable.getMessageClient(it).addListener(this)
        }
        activity?.application?.let { Wearable.getCapabilityClient(it).addListener(
                this,
                CAPABILITY_WEAR_APP
        ) }

        // 웨어 앱이 설치된 기능(Capability)을 갖춘 장치에 대한 초기 요청
        findWearDevicesWithApp()
        findAllWearDevices()
    }

    //앱 스토어에서 해당 앱을 열기 위해 시계에서 RemoteIntent를 보내온 결과
    private val mResultReceiver: ResultReceiver = object : ResultReceiver(Handler()) {
        protected override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
            Log.d(TAG, "OnReceiveResult: $resultCode")
            //응답이 잘 왔을때
            if (resultCode == RemoteIntent.RESULT_OK) {
                val toast = Toast.makeText(
                        activity?.applicationContext,
                        "시계에서 Play Store를 성공적으로 열었습니다.",
                        Toast.LENGTH_SHORT
                )
                toast.show()

                //응답이 실패했을때
            } else if (resultCode == RemoteIntent.RESULT_FAILED) {
                val toast = Toast.makeText(
                        activity?.applicationContext,
                        "시계에서 Play Store를 열지 못했습니다. " +
                                "연결된 시계가 Play Store를 지원하지 않습니다."
                                + " (시계의 버전이 1.0에서는 Play Store가 지원되지 않습니다.)",
                        Toast.LENGTH_LONG
                )
                toast.show()
            } else { //다른 오류 + 결과코드
                throw IllegalStateException("예상치 못한 결과 $resultCode")
            }
        }
    }


    override fun onCapabilityChanged(@NonNull capabilityInfo: CapabilityInfo) {
        Log.d(TAG, "onCapabilityChanged(): $capabilityInfo")
        //CapabilityInfo.getNodes(): Capability(기능)에 대한 노드 집합 반환
        //                          연결이 끊긴 노드는 세트에 포함안될수도
        mWearNodesWithApp = capabilityInfo.nodes
        findAllWearDevices()
        verifyNodeAndUpdateUI()
    }

    private fun findWearDevicesWithApp() {
        Log.d(TAG, "findWearDevicesWithApp()")

        //capabilityInfo has the reachable nodes with the transcription capability
        //getCapability 메서드를 호출하여 기능이 있는 노드를 감지할 수 있습니다.
        val capabilityInfoTask = activity?.application?.let {
            Wearable.getCapabilityClient(it)
                    .getCapability(CAPABILITY_WEAR_APP, CapabilityClient.FILTER_ALL)
        }
        //이 필터를 설정하면 지정된 기능을 선언하는 전체 노드 집합이 기능의 CapabilityInfo에 포함됩니다.
        capabilityInfoTask?.addOnCompleteListener { task: Task<CapabilityInfo?> ->
            if (task.isSuccessful) {
                Log.d(TAG, "Capability request succeeded.")
                val capabilityInfo = task.result
                capabilityName = capabilityInfo!!.name
                mWearNodesWithApp = capabilityInfo.nodes
                Log.d(TAG, "Capable Nodes: $mWearNodesWithApp")
                val wearSupportsSampleApp = capabilityName == CAPABILITY_WEAR_APP
                if(wearSupportsSampleApp){
                    //mWearNodesWithApp = capabilityInfo.nodes
                    if (mWearRequestingPhoneStoragePermission) {
                        mWearRequestingPhoneStoragePermission = false
                        sendWearPermissionResults()
                    }
                }
                verifyNodeAndUpdateUI()
            } else {
                Log.d(TAG, "Capability request failed to return any results.")
            }
        }
    }

    //Mobile Device와 연결된 WearDevice Node찾는 메소드
    private fun findAllWearDevices() {
        Log.d(TAG, "findAllWearDevices()")
        val NodeListTask = activity?.application?.let { Wearable.getNodeClient(it).connectedNodes }
        NodeListTask?.addOnCompleteListener { task: Task<List<Node?>> ->
            if (task.isSuccessful) {
                Log.d(TAG, "Node request succeeded.")
                mAllConnectedNodes = task.result as List<Node>?
            } else {
                Log.d(TAG, "Node request failed to return any results.")
            }
            verifyNodeAndUpdateUI()
        }
    }

    //데이터의 변경이 있을때 UI변경
    private fun verifyNodeAndUpdateUI() {
        Log.d(TAG, "verifyNodeAndUpdateUI()")
        if (mWearNodesWithApp == null || mAllConnectedNodes == null) {
            Log.d(TAG, "Waiting on Results for both connected nodes and nodes with app")
        } else if (mAllConnectedNodes!!.isEmpty()) {
            Log.d(TAG, No_Device)
            information_text_view.setText(No_Device)
            remote_open_button.setVisibility(View.INVISIBLE)
        } else if (mWearNodesWithApp!!.isEmpty()) {
            Log.d(TAG, Missing_All_Message)
            information_text_view.setText(Missing_All_Message)
            remote_open_button.setVisibility(View.VISIBLE)

            //웨어러블 앱이 설치되어 있을때
        } else if (mWearNodesWithApp!!.size < mAllConnectedNodes!!.size) {
            // Wear App 설치 완료 후, 웨어앱과 통신하는 부분 -> ??뭘해야할지 아직??
            Log.d(TAG, Install_WearApp_Message)
            information_text_view.setText(Install_WearApp_Message)
            remote_open_button.setVisibility(View.VISIBLE)
        } else {
            // Wear App 설치완료
            //모든 디바이스에 연결&앱설치 되어있을경우
            Log.d(TAG, Install_WearApp_Message)
            information_text_view.setText(Install_WearApp_Message)
            remote_open_button.setVisibility(View.INVISIBLE)
        }
    }

    private fun openPlayStoreOnWearDevicesWithoutApp() {
        Log.d(TAG, "openPlayStoreOnWearDevicesWithoutApp()")
        //앱이 설치되어있지 않은 웨어러블 기기의 노드를 생성
        val nodesWithoutApp: ArrayList<Node> = ArrayList()

        //모든 기기의 노드중에서 앱이 없는 기기의 노드를 추가
        for (node in mAllConnectedNodes!!) {
            if (!mWearNodesWithApp?.contains(node)!!) {
                nodesWithoutApp.add(node)
            }
        }

        //앱이 없는 노드가 비어있지 않다면, 몇개의 노드가 앱이 없는지 표시 - log
        if (nodesWithoutApp.isNotEmpty()) {
            Log.d(TAG, "Number of nodes without app: " + nodesWithoutApp.size)
            val intent = Intent(Intent.ACTION_VIEW)
                    .addCategory(Intent.CATEGORY_BROWSABLE)
                    .setData(Uri.parse(PLAY_STORE_APP_URI))
            //플레이스토어로 접속

            //앱이 없는 노드만큼, 해당 코드 실행
            for (node in nodesWithoutApp) {
                RemoteIntent.startRemoteActivity( //startRemoteActivity를 이용해서 앱스토어를 열 수 있음
                        activity?.applicationContext,
                        intent,
                        mResultReceiver,
                        node.id
                )
            }
        }
    }


    override fun onMessageReceived(messageEvent: MessageEvent) {
        Log.d(TAG, "onMessageReceived(): $messageEvent")

        val messagePath: String = messageEvent.path

        if (messagePath == MESSAGE_PATH_PHONE) {
            val dataMap = DataMap.fromByteArray(messageEvent.data)
            val commType = dataMap.getInt(KEY_COMM_TYPE, 0)
            if (commType == COMM_TYPE_RESPONSE_USER_DENIED_PERMISSION) {
                logToUi("User denied permission on remote device.")
            } else {
                Log.d(TAG, "Unrecognized communication type received.")
            }
        }
    }

    private fun sendMessage(dataMap: DataMap) {
        Log.d(TAG, "sendMessage(): $mWearNodeIds")
        if (mWearNodeIds != null && mWearNodeIds.isNotEmpty()) {
            var sendMessageTask: Task<Int>
            for (node in mWearNodeIds) {

                activity?.application?.let{
                    sendMessageTask = Wearable.getMessageClient(it).sendMessage(node.id, MESSAGE_PATH_WEAR, dataMap.toByteArray())
                    sendMessageTask.addOnCompleteListener(this)
                }

            }
        } else {
            logToUi("Wear devices not available to send message.")
        }
    }

    override fun onComplete(task: Task<Int>) {
        if (!task.isSuccessful()) {
            Log.d(TAG, "Sending message failed, onComplete.")
            logToUi("Sending message failed.")
        } else {
            Log.d(TAG, "Message sent.")
        }
    }

    private fun sendWearPermissionResults() {
        Log.d(TAG, "sendWearPermissionResults()")
        val dataMap = DataMap()
        if (mPhonePermissionApproved) {
            dataMap.putInt(KEY_COMM_TYPE, COMM_TYPE_RESPONSE_USER_APPROVED_PERMISSION)
        } else {
            dataMap.putInt(KEY_COMM_TYPE, COMM_TYPE_RESPONSE_USER_DENIED_PERMISSION)
        }
        sendMessage(dataMap)
    }

    private fun logToUi(message: String) {
        val mainUiThread = Looper.myLooper() == Looper.getMainLooper()
        if (mainUiThread) {
            if (message.isNotEmpty()) {
                Log.d(TAG, message)
                mOutputTextView!!.text = message
            }
        } else {
            if (message.isNotEmpty()) {
                activity?.runOnUiThread(Runnable {
                    Log.d(TAG, message)
                    mOutputTextView!!.text = message
                })
            }
        }
    }


}


