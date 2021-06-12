package kr.co.jolph.soundapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import kotlinx.android.synthetic.main.activity_f_irebasestoragetest.*
import java.io.File
import kotlin.concurrent.timer

class FIrebasestoragetest : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_f_irebasestoragetest)
        var TESTsentence2:String = "졸업프로젝트화이팅"

        fireebasetest.setOnClickListener {
//            var storage = Firebase.storage
//            val storageRef = storage.reference
//            var file = Uri.fromFile(File("/storage/emulated/0/Android/data/kr.co.jolph.soundapp/cache/sound650.wav"))
//            val riversRef = storageRef.child("sounds/${file.lastPathSegment}")
//            var metadata = storageMetadata {
//                contentType = "audio/wav"
            if (TESTsentence2==Getresultfromserver.instance.TESTsentence1)
            {
                Toast.makeText(this, "문자 같다고 비교 가능", Toast.LENGTH_SHORT).show()
            }
            else
            {
                Toast.makeText(this, "문자 같다고 비교 불가능", Toast.LENGTH_SHORT).show()
            }
            //riversRef.putFile(file, metadata)
        }
    }

}