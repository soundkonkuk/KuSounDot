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
    //private val channelID="kr.co.jolph.soundapp.channel1"
    //private var notificationManager:NotificationManager?=null
    val TAG: String = "LOG"
    var filepath = Uri.parse("/storage/emulated/0/Download/q15-ararat.jpg")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_getresultfromserver)
        //notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //노티피케이션 채널 생성
        //MainActivity.instance1.createNotificationChannel(channelID, "DemoChannel", "this is a demo")
        get_method_btn.setOnClickListener {
            uploadFilecloudstorage()
           // RetrofitManager.instance.getUser2()
          //  RetrofitManager.instance.createUser("50번소리파일")
            timer(period = 3000, initialDelay = 3000)
            {
                Log.d(TAG, "겟 메소드 호출")
                RetrofitManager.instance.getUser()
                cancel()
            }

        }
        //MainActivity.instance1.displayNotification()
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