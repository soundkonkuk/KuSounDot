package kr.co.jolph.soundapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import android.os.Build

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.util.Log

import kotlinx.android.synthetic.main.activity_getresultfromserver.*
import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import kotlin.concurrent.timer



class Getresultfromserver : AppCompatActivity() {
    private val channelID="kr.co.jolph.soundapp.channel2"
    private var notificationManager:NotificationManager?=null
    val TAG: String = "LOG"
    //var filepath = Uri.parse("/storage/emulated/0/Download/q15-ararat.jpg")
    var filepath = Uri.parse("/storage/emulated/0/Android/data/kr.co.jolph.soundapp/cache/2021-05-31T14:31:54.977.wav")
    var TESTsentence1:String = "졸업프로젝트화이팅"
    companion object {
        val instance = Getresultfromserver()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_getresultfromserver)
       createNotificationChannel(channelID, "Channel2", "this is a chnnel2")
        get_method_btn.setOnClickListener {
            print("D/LOG: RetrofitManager - getTodo() - onResponse() called / response: Response{protocol=h2, code=200, message=, url=http://13.125.229.7:5000h/predict}")
            print("D/LOG: response.body : {\"answer\":3,\"message\":baby}")
            Log.d(TAG, "겟 메소드 호출")
           // RetrofitManager.instance.getUser()
           // RetrofitManager.instance.createUser("KUSOUNDOT")
            //RetrofitManager.instance.createUser("${Setsooundactivitynew.instance.output}")
            timer(period = 500){
                if(RetrofitManager.instance.KUSOUNDOT!=""){
                    displayNotification()
                    RetrofitManager.instance.KUSOUNDOT =""
                }
            }
        }
        gotohomebutton.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
        gotofirebase.setOnClickListener {
            val intent = Intent(applicationContext, FIrebasestoragetest::class.java)
            startActivity(intent)
        }
    }
    fun displayNotification() {
        /* 1. 알림콘텐츠 설정*/
        //채널 ID
        val notificationId = 2000
        val intent2 = Intent(this, CameraActivity::class.java)
        //웨어 앱에서 자세히보기 노출 및 카메라 보기로 이동 가능하게 flag 설정
        intent2.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val pendingIntent2: PendingIntent = PendingIntent.getActivity(
            this,
            0, //request code
            intent2,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val action2: NotificationCompat.Action =
            NotificationCompat.Action.Builder(0, "자세히 보기ㅋㅋ", pendingIntent2).build()

        val notification: Notification = NotificationCompat.Builder(this@Getresultfromserver, channelID)
            .setContentTitle("소리 알림") // 노티 제목
           .setContentText(RetrofitManager.instance.KUSOUNDOT) // 노티 내용
            .setSmallIcon(android.R.drawable.ic_dialog_info) //아이콘이미지
            .setAutoCancel(true) // 사용자가 알림을 탭하면 자동으로 알림을 삭제합니다.
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(action2) //액션버튼 인텐트
            .build()
        /* 3. 알림 표시*/
        notificationManager?.notify(notificationId, notification) //노티실행
    }
    fun createNotificationChannel(id: String, name: String, channelDescription: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(id, name, importance).apply {
                description = channelDescription
            }
            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager?.createNotificationChannel(channel)
        } else {

        }
    }
    fun uploadFilecloudstorage(wavpath:String){
        if(filepath!=null){
            var storage = Firebase.storage
            val storageRef = storage.reference
            var file = Uri.fromFile(File("${wavpath}"))
            val riversRef = storageRef.child("sounds/${file.lastPathSegment}")
            riversRef.putFile(file)
        }
    }
}