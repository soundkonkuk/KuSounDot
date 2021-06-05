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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var actionBar : ActionBar?
        actionBar = supportActionBar;
        actionBar?.hide()
        initNaviBar()
    }
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
