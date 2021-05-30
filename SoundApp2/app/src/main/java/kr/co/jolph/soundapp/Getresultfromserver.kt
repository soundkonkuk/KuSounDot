package kr.co.jolph.soundapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.getActivity
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.os.Build

import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.time.seconds
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_home.*
import kr.co.jolph.soundapp.Getresultfromserver

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.util.Log
import kr.co.jolph.soundapp.RetrofitManager
import kotlinx.android.synthetic.main.activity_getresultfromserver.*
import kotlinx.android.synthetic.main.activity_main.*
import android.net.Uri
import android.widget.Toast
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.net.URI
import kotlin.concurrent.timer

class Getresultfromserver : AppCompatActivity() {
    private val channelID="kr.co.jolph.soundapp.channel2"
    private var notificationManager:NotificationManager?=null
    val TAG: String = "LOG"
    var filepath = Uri.parse("/storage/emulated/0/Download/q15-ararat.jpg")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_getresultfromserver)

       createNotificationChannel(channelID, "Channel2", "this is a chnnel2")

        get_method_btn.setOnClickListener {
            //uploadFilecloudstorage()
          //  RetrofitManager.instance.createUser("50번소리파일")
            timer(period = 3000, initialDelay = 3000)
            {
                Log.d(TAG, "겟 메소드 호출")
                RetrofitManager.instance.getUser()
                cancel()
            }
            timer(period=3000){
                displayNotification()
            }


        }
    }
    fun displayNotification() {
        /* 1. 알림콘텐츠 설정*/
        //채널 ID
        val notificationId = 2000

        val intent2 = Intent(this, CameraActivity::class.java)
        val pendingIntent2: PendingIntent = PendingIntent.getActivity(
            this,
            0, //request code
            intent2,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val action2: NotificationCompat.Action =
            NotificationCompat.Action.Builder(0, "자세히 보기", pendingIntent2).build()

        val notification: Notification = NotificationCompat.Builder(this@Getresultfromserver, channelID)
            .setContentTitle("소리 알림") // 노티 제목
            .setContentText(RetrofitManager.instance.KUSOUNDOT) // 노티 내용
            .setSmallIcon(android.R.drawable.ic_dialog_info) //아이콘이미지
            .setAutoCancel(true) // 사용자가 알림을 탭하면 자동으로 알림을 삭제합니다.
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            //.setContentIntent(pendingIntent) //노티클릭시 인텐트작업
            .addAction(action2) //액션버튼 인텐트
            .build()
        /* 3. 알림 표시*/
        //NotificationManagerCompat.notify()에 전달하는 알림 ID를 저장해야 합니다.
        // 알림을 업데이트하거나 삭제하려면 나중에 필요하기 때문입니다.
        notificationManager?.notify(notificationId, notification) //노티실행
    }

    fun createNotificationChannel(id: String, name: String, channelDescription: String) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //중요도
            val importance = NotificationManager.IMPORTANCE_HIGH
            //채널 생성
            val channel = NotificationChannel(id, name, importance).apply {
                description = channelDescription
            }
            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager?.createNotificationChannel(channel)
        } else {

        }
    }

    private fun uploadFilecloudstorage(){
        if(filepath!=null){
            var storage = Firebase.storage
            val storageRef = storage.reference
            var file = Uri.fromFile(File("/storage/emulated/0/Download/babycry10.wav"))
            val riversRef = storageRef.child("sounds/${file.lastPathSegment}")
            riversRef.putFile(file)
        }
    }
}