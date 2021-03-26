package kr.co.jolph.soundapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import com.google.android.wearable.intent.RemoteIntent
import kotlinx.android.synthetic.main.fragment_watch.*


/**
 * A simple [Fragment] subclass.
 * Use the [watch.newInstance] factory method to
 * create an instance of this fragment.
 */
class watch : Fragment(), CapabilityClient.OnCapabilityChangedListener{

    companion object {

        //parameters
        private val TAG: String? = "watch"
        private const val Welcom_Message = "소리녹음 모바일 앱에 오신걸 환영합니다!\n\n"
        private const val Check_Message = "${Welcom_Message}시계연결을 확인중입니다..."
        private const val No_Device = "${Welcom_Message}핸드폰과 연결되어 있는 시계가 없습니다. \n\n 시계를 연결하고 소리알림을 어디서든 받아보세요!"
        private const val Missing_All_Message = """${Welcom_Message}시계에 소리녹음 웨어앱이 설치되어있지 않습니다.아래 버튼을 클릭해서 웨어앱을 설치해 주세요!"""
        private const val Install_WearApp_Message ="$Welcom_Message(%s) 시계에 웨어앱이 설치되었습니다!\n\n"
        //CAPABILITY_WEAR_APP -> Wear App > values > wear.xml
        private const val CAPABILITY_WEAR_APP = "verify_remote_wear_app"

        // TODO: Replace with Links/packages
        private const val PLAY_STORE_APP_URI = "market://details?id=com.example.android.wearable.wear.wearverifyremoteapp"

        private var mWearNodesWithApp: Set<Node>? = null
        private var mAllConnectedNodes: List<Node>? = null
        //private var mInformationTextView: TextView? = null
        //private var mRemoteOpenButton: Button? = null

    }

    //View Binding 방식
    // private var _binding: FragmentWatchBinding? = null
    // private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //View Binding 방식
        //_binding = FragmentWatchBinding.inflate(inflater, container, false)
        //return binding.root
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_watch, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        information_text_view?.text = Check_Message
        remote_open_button?.setOnClickListener { openPlayStoreOnWearDevicesWithoutApp() }
        /* mInformationTextView = view?.findViewById(R.id.information_text_view)
         mRemoteOpenButton = view?.findViewById(R.id.remote_open_button)
         mRemoteOpenButton?.setOnClickListener { openPlayStoreOnWearDevicesWithoutApp() }*/
    }

    override fun onPause() {
        Log.d(TAG, "onPause()")
        super.onPause()
        activity?.let { Wearable.getCapabilityClient(it).removeListener(this, CAPABILITY_WEAR_APP) }
    }

    //사용자와 상호작용 하는 단계 / Activity 스택의 Top에 위치 / 주로 어플 기능이 onResume()에 설정됨
    override fun onResume() {
        Log.d(TAG, "onResume()")
        super.onResume()
        activity?.application?.let { Wearable.getCapabilityClient(it).addListener(this, CAPABILITY_WEAR_APP) }

        // 웨어 앱이 설치된 기능(Capability)을 갖춘 장치에 대한 초기 요청
        // Initial request for devices with our capability, aka, our Wear app installed.
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
                mWearNodesWithApp = capabilityInfo!!.nodes
                Log.d(TAG, "Capable Nodes: $mWearNodesWithApp")
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
            val installMessage = java.lang.String.format(Install_WearApp_Message, mWearNodesWithApp)
            Log.d(TAG, installMessage)
            information_text_view.setText(installMessage)
            remote_open_button.setVisibility(View.VISIBLE)
        } else {
            // Wear App 설치완료
            //모든 디바이스에 연결&앱설치 되어있을경우
            val installMessage = java.lang.String.format(Install_WearApp_Message, mWearNodesWithApp)
            Log.d(TAG, installMessage)
            information_text_view.setText(installMessage)
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


}


