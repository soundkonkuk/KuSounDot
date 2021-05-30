package kr.co.jolph.soundapp

import kotlin.concurrent.timer
import kotlin.coroutines.*
//import kotlinx.coroutines.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_setsooundactivitynew.*
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.math.log10

@Suppress("DEPRECATION")
class Setsooundactivitynew : AppCompatActivity() {

    private var output: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var state: Boolean = false
    private var recordingStopped: Boolean = false
    var startnumber=100
    var numbers = 100
    val mHandler: Handler = Handler()
    private val EMA_FILTER = 0.6 // EMA 필터 계산에 사용되는 상수, 기본값 0.6
    private var mEMA = 0.0 // EMA 필터가 적용된 데시벨 피크 값. getAmplitudeEMA()의 리턴값이다.
    private var decibel: Double? = null
    var dateAndtime: LocalDateTime? = null
    var isRecordStart = false

    var runner: Thread? = null
    val measure = Runnable {
        decibel = soundDb()
        println("decibel: $decibel") }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setsooundactivitynew)

        if (runner == null) {
            runner = object : Thread() {
                override fun run() {
                    while (runner != null) {
                        try {
                            sleep(10)
                            Log.i("Noise", "runner")
                        } catch (e: InterruptedException) {

                        }
                        if(isRecordStart){
                            mHandler.post(measure)
                        }
                    }
                }
            }
            (runner as Thread).start()
            Log.d("Noise", "start runner()")
        }

        button_start_recording.setOnClickListener {
            println("hello!"+startnumber)
            if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                val permissions = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                ActivityCompat.requestPermissions(this, permissions,0)
                //startRecording()
            } else {
                timer(period = 3000)
                {
                    println(startnumber)
                    isRecordStart = true
                    println("isRecordStart: $isRecordStart")
                    startnumber++
                    if(startnumber>10000)
                        cancel()
                    mediaRecorder?.stop();     // stop recording
                    mediaRecorder?.reset();    // set state to idle
                    mediaRecorder?.release();  // release resources back to the system
                    mediaRecorder = null;
                    state = false
                    val permissions = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                    ActivityCompat.requestPermissions(this@Setsooundactivitynew, permissions,0)
                    mediaRecorder = MediaRecorder()
                    //var dateAndtime: LocalDateTime = LocalDateTime.now()
                    dateAndtime = LocalDateTime.now()
                    output = "${externalCacheDir!!.absolutePath}/${dateAndtime}.wav"
                    mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
                    mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                    mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                    mediaRecorder?.setOutputFile(output)
                    println(dateAndtime)
                    try {
                        mediaRecorder?.prepare()
                        mediaRecorder?.start()
                        state = true
                    } catch (e: IllegalStateException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    if(decibel != null){
                        if(decibel!! > 30.0){
                            // TODO: send .wav file to server
                            println("send to server! this file decibel is $decibel")
                        }else{
                            //TODO: delete all .wav file in cache
                            println("delete all file")
                        }
                    }
                }
                Toast.makeText(this, "Recording started!", Toast.LENGTH_SHORT).show()
            }
        }
        button_stop_recording.setOnClickListener{
            stopRecording()
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun startRecording() {
        startnumber++
        if (state) {
            mediaRecorder?.stop();     // stop recording
            mediaRecorder?.reset();    // set state to idle
            mediaRecorder?.release();  // release resources back to the system
            mediaRecorder = null;
            state = false
        } else {
            mediaRecorder = MediaRecorder()
            startnumber++
            //output = Environment.getExternalStorageDirectory().absolutePath + "/recording.mp3"
            output = "${externalCacheDir!!.absolutePath}/${startnumber}.wav"
            mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            mediaRecorder?.setOutputFile(output)
            try {
                mediaRecorder?.prepare()
                mediaRecorder?.start()
                state = true
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        Toast.makeText(this, "Recording started!", Toast.LENGTH_SHORT).show()
    }
    @SuppressLint("RestrictedApi", "SetTextI18n")
    @TargetApi(Build.VERSION_CODES.N)

    private fun stopRecording(){
        if(state){
            mediaRecorder?.stop();     // stop recording
            /*mediaRecorder?.reset();    // set state to idle
            mediaRecorder?.release();  // release resources back to the system
            mediaRecorder = null;
            state = false*/
            Toast.makeText(this, "소리저장완료!", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "You are not recording right now!", Toast.LENGTH_SHORT).show()
        }
        startnumber+=10000
        mediaRecorder?.reset();    // set state to idle
        mediaRecorder?.release();  // release resources back to the system
        mediaRecorder = null;
        state = false

    }
    private fun soundDb(): Double? {
        println("soundDb: $dateAndtime")
        val amplitude = mediaRecorder?.maxAmplitude
        println("soundDb(): $amplitude")
        return if(amplitude!=null){
            val mEMA = EMA_FILTER * amplitude!! + (1.0 - EMA_FILTER) * mEMA
            println("mEMA: $mEMA")
            val pressure = mEMA/51805.5336
            //println("second: ${20 * ln(amplitude / 2700.0)}")
            20 * log10(pressure/ 0.000028251)
        }else{
            0.0
        }
    }
}