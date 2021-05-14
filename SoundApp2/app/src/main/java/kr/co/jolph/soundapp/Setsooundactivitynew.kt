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

@Suppress("DEPRECATION")
class Setsooundactivitynew : AppCompatActivity() {

    private var output: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var state: Boolean = false
    private var recordingStopped: Boolean = false
    var startnumber=100
    var numbers = 100

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setsooundactivitynew)
        button_start_recording.setOnClickListener {
            println("hello!"+startnumber)
            if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                val permissions = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                ActivityCompat.requestPermissions(this, permissions,0)
                startRecording()
            } else {
                timer(period = 3000)
                {
                    println(startnumber)
                    //stopRecording()
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
                    var dateAndtime: LocalDateTime = LocalDateTime.now()
                    val onlyDate: LocalDate = LocalDate.now()
                    output = "${externalCacheDir!!.absolutePath}/${dateAndtime}.wav"
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
            val dateAndtime: LocalDateTime = LocalDateTime.now()
            val onlyDate: LocalDate = LocalDate.now()
            mediaRecorder = MediaRecorder()
            startnumber++
            //output = Environment.getExternalStorageDirectory().absolutePath + "/recording.mp3"
            //output = "${externalCacheDir!!.absolutePath}/${dateAndtime}.wav"
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
}