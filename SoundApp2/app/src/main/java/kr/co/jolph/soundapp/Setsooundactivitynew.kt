package kr.co.jolph.soundapp

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import java.io.File
import java.io.IOException
import java.time.LocalDateTime
import java.util.*
import kotlin.concurrent.timer
import kotlin.math.log10

@Suppress("DEPRECATION")

class Setsooundactivitynew : AppCompatActivity() {
    private val channelID="kr.co.jolph.soundapp.channel2"
    private var notificationManager:NotificationManager?=null
    companion object {
        val instance = Setsooundactivitynew()
    }

    var showingmessage:String=""
    var output: String? = null
    fun getoutput(): String? {
        return output
    }
    private var mediaRecorder: MediaRecorder? = null
    private var state: Boolean = false
    var number = 100
    val mHandler: Handler = Handler()
    private val EMA_FILTER = 0.6 // EMA 필터 계산에 사용되는 상수, 기본값 0.6
    private var mEMA = 0.0 // EMA 필터가 적용된 데시벨 피크 값. getAmplitudeEMA()의 리턴값이다.
    private var decibel: Double? = null
    var dateAndtime: LocalDateTime? = null
    var isRecordStart = false
    var presentFileName: String? = null
    var runner: Thread? = null
    //val jsonString="{\"answer\":0,\"message\":\"아기울음소리가 발생했어요!"}
//    val measure = Runnable {
//        decibel = soundDb()
//        println("decibel: $decibel") }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setsooundactivitynew)

//        if (runner == null) {
//            runner = object : Thread() {
//                override fun run() {
//                    while (runner != null) {
//                        try {
//                            sleep(1000)
//                            Log.i("Noise", "runner")
//                        } catch (e: InterruptedException) {
//                        }
//                        if(isRecordStart){
//                            mHandler.post(measure)
//                        }
//                    }
//                }
//            }
//            (runner as Thread).start()
//            Log.d("Noise", "start runner()")
//        }
        println("hello!"+startnumber)
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            val permissions = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(this, permissions,0)
        } else {


            println(startnumber)
            isRecordStart = true
            println("isRecordStart: $isRecordStart")
            startnumber++
            if(startnumber>10000)


                state = false
            val permissions = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(this@Setsooundactivitynew, permissions,0)


            mediaRecorder = MediaRecorder()
            dateAndtime = LocalDateTime.now()
            //output = "${externalCacheDir!!.absolutePath}/${dateAndtime}.wav"
            output = "${externalCacheDir!!.absolutePath}/sound.mp4"
            mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            mediaRecorder?.setOutputFile(output)
            try {
                mediaRecorder?.prepare()
                mediaRecorder?.start()
                timer(period = 2000, initialDelay = 3000)
                {
                    mediaRecorder?.stop();     // stop recording
                    mediaRecorder?.reset();    // set state to idle
                    mediaRecorder?.release();  // release resources back to the system
                    mediaRecorder = null;
                    state = true
                    cancel()
                }

            } catch (e: IllegalStateException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            // if(decibel != null){
            Log.i("Noisetest", "${decibel}")
            // if(decibel!! > 10.0){
            timer(period = 2000, initialDelay = 3000)
            {
                uploadFilecloudstorage(output!!)
                cancel()
            }

            RetrofitManager.instance.getUser()
            //RetrofitManager.instance.createUser(output!!)
            createNotificationChannel(channelID, "Channel2", "this is a chnnel2")
            timer(period = 3000 ){
                if(RetrofitManager.instance.KUSOUNDOT!=""){
                    if(startnumber>10000)
                        cancel()
                    displayNotification()
                    RetrofitManager.instance.KUSOUNDOT =""
                }
            }
            println("send to server! this file decibel is $decibel")
            println("now file: $presentFileName")
            // }
            //  else{
            println("delete all file")
//                            try{
//                                val file = File(presentFileName)
//                                println("now file: $presentFileName")
//                                if(file.exists()){
//                                    file.delete()
//                                    println("file delete complete!")
//                                }
//                            }catch(e: Exception){
//                                e.printStackTrace()
//                                println("file error")
//                            }

            // }
            //   }
            Toast.makeText(this, "Recording started!", Toast.LENGTH_SHORT).show()
        }
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
    @SuppressLint("RestrictedApi", "SetTextI18n")
    @TargetApi(Build.VERSION_CODES.N)
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

        when (RetrofitManager.instance.KUSOUNDOT)
        {
            "{\"answer\":3,\"message\":\"사이렌소리가 발생했어요!\"}" -> showingmessage="애애애엥! 사이렌 소리가 발생했어요!"
            "{\"answer\":0,\"message\":\"아기울음소리가 발생했어요!\"}" -> showingmessage="응애 응애! 아기울음 소리가 발생했어요!"
            "{\"answer\":1,\"message\":\"외침소리가 발생했어요!\"}" -> showingmessage="조심해! 목소리 외침 소리가 발생했어요!"
            "{\"answer\":2,\"message\":\"폭발음소리가 발생했어요!\"}" -> showingmessage="펑! 폭발음 소리가 발생했어요!"
            else -> showingmessage="소리발생! 위험소리일 확률은 50% 미만입니다."
        }

        val notification: Notification = NotificationCompat.Builder(this@Setsooundactivitynew, channelID)
                .setContentTitle("소리 알림") // 노티 제목
                .setContentText(showingmessage) // 노티 내용
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
        if(wavpath!=null){
            var storage = Firebase.storage
            val storageRef = storage.reference

            var file = Uri.fromFile(File(wavpath))
            Log.d("wavpath확인하기", wavpath)
            //var file = Uri.fromFile(File(${output}))
            val riversRef = storageRef.child("sounds/${file.lastPathSegment}")
            var metadata = storageMetadata {
                contentType = "audio/wav"
            }
            riversRef.putFile(file)
        }
    }
    private fun soundDb(): Double? {
        println("soundDb: $dateAndtime")
        val amplitude = mediaRecorder?.maxAmplitude
        presentFileName = output
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