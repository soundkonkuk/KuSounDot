package kr.co.jolph.soundapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import android.os.Handler
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.util.*
import kotlin.concurrent.timer


@Suppress("DEPRECATION")
class Setsooundactivitynew2 : AppCompatActivity() {



    var output: String? = null
    fun getoutput(): String? {
        return output
    }
    private var mediaRecorder: MediaRecorder? = null
    private var state: Boolean = false
    var startnumber=100
    val mHandler: Handler = Handler()
    private val EMA_FILTER = 0.6 // EMA 필터 계산에 사용되는 상수, 기본값 0.6
    private var mEMA = 0.0 // EMA 필터가 적용된 데시벨 피크 값. getAmplitudeEMA()의 리턴값이다.
    private var decibel: Double? = null
    var dateAndtime: LocalDateTime? = null
    var isRecordStart = false
    var presentFileName: String? = null


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setsooundactivitynew2)
        timer(period = 2000, initialDelay = 3000)
        {
            timer(period = 2000, initialDelay = 3000)
            {
                timer(period = 2000, initialDelay = 3000)
                {

                }
            }
        }
        if(state){
            mediaRecorder?.stop();     // stop recording
            Toast.makeText(this, "소리저장완료!", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "You are not recording right now!", Toast.LENGTH_SHORT).show()
        }
        startnumber+=10000
        mediaRecorder?.reset();    // set state to idle
        mediaRecorder?.release();  // release resources back to the system
        mediaRecorder = null;
        state = false
        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        val timer = Timer()
        timer.schedule(timerTask, 1000)
    }


}