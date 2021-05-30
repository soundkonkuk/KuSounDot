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
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.time.seconds
import android.content.Intent
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_home.*
import kr.co.jolph.soundapp.Getresultfromserver

class MainActivity : AppCompatActivity() {
    companion object {
        val instance1 = MainActivity()
    }
    private val fra_home = home()
    val fra_camera = camera()
    private val fra_settings = settings()
    private val fra_watch = watch()
    var resultKUSOUNDOT:String = ""
    //private val channelID="kr.co.jolph.soundapp.channel1"
    //private var notificationManager:NotificationManager?=null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var actionBar : ActionBar?
        actionBar = supportActionBar;
        actionBar?.hide()
        initNaviBar()


        soundActivityBtn.setOnClickListener {
            val setSoundIntent = Intent(this, Setsooundactivitynew::class.java)
            startActivity(setSoundIntent)
        }

        //노티피케이션 채널 생성
        /*createNotificationChannel(channelID, "DemoChannel", "this is a demo")
        psybutton.setOnClickListener {
            displayNotification()
        }*/
    }
    /*fun displayNotification() {
        /* 1. 알림콘텐츠 설정*/
        //채널 ID
        val notificationId = 200
        //알림의 자세히 보기 버튼 작업 설정 -----------------------------------------------------------------------
        val intent2 = Intent(this, CameraActivity::class.java)
        val pendingIntent2: PendingIntent = PendingIntent.getActivity(
            this,
            0, //request code
            intent2,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val action2: NotificationCompat.Action =
            NotificationCompat.Action.Builder(0, "자세히 보기", pendingIntent2).build()

        resultKUSOUNDOT=RetrofitManager.instance.returnKUSOUNDOT()
        val notification= NotificationCompat.Builder(this@MainActivity, channelID)
            .setContentTitle("소리 알림") // 노티 제목
            .setContentText(resultKUSOUNDOT) // 노티 내용
            .setSmallIcon(android.R.drawable.ic_dialog_info) //아이콘이미지
            .setAutoCancel(true) // 사용자가 알림을 탭하면 자동으로 알림을 삭제합니다.
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            //.setContentIntent(pendingIntent) //노티클릭시 인텐트작업
            .addAction(action2) //액션버튼 인텐트
            .build()
        /* 3. 알림 표시*/
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(notificationId, notification)
        }

    }
    fun createNotificationChannel(id: String, name: String, channelDescription: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(id, name, importance).apply{
                description = channelDescription
            }
            notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager?.createNotificationChannel(channel)
        } else {

        }
    }*/
    private fun initNaviBar(){

        bottomNavi.run{
            setOnNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.mFirst->{
                        changeFrag(fra_home)
                    }
                    R.id.mSecond ->{
                        changeFrag(fra_camera)
                    }
                    R.id.mThird ->{
                        changeFrag(fra_settings)
                    }
                    R.id.mFourth ->{
                        changeFrag(fra_watch)
                    }

                }
                true
            }
            selectedItemId = R.id.mFirst
        }
    }


    fun changeFrag(fragment: Fragment){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }
}
