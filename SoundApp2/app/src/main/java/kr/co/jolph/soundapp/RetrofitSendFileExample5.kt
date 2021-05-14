package kr.co.jolph.soundapp

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_retrofit_send_file_example5.*
import java.io.File
import java.net.URI


class RetrofitSendFileExample5 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retrofit_send_file_example5)

        button55.setOnClickListener {
            uploadFilecloudstorage()
        }
    }
    var filepath = Uri.parse("/storage/emulated/0/Download/q15-ararat.jpg")

    private fun uploadFilecloudstorage(){
        if(filepath!=null){
            var storage = Firebase.storage

            // Create a Cloud Storage reference from the app
            val storageRef = storage.reference
            var file = Uri.fromFile(File("/storage/emulated/0/Download/babycry10.wav"))
            val riversRef = storageRef.child("sounds/${file.lastPathSegment}")
            riversRef.putFile(file)

        }
    }
}