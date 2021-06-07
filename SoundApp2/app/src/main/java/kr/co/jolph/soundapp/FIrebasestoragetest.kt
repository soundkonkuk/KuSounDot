package kr.co.jolph.soundapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import kotlinx.android.synthetic.main.activity_f_irebasestoragetest.*
import java.io.File

class FIrebasestoragetest : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_f_irebasestoragetest)
        fireebasetest.setOnClickListener {
            var storage = Firebase.storage
            val storageRef = storage.reference
            var file = Uri.fromFile(File("/storage/emulated/0/Android/data/kr.co.jolph.soundapp/cache/sound.wav"))
            val riversRef = storageRef.child("sounds/${file.lastPathSegment}")
            var metadata = storageMetadata {
                contentType = "audio/wav"
            }
            riversRef.putFile(file, metadata)
        }
    }

}