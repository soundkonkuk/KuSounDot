//package kr.co.jolph.soundapp
//
//import android.app.Activity
//import android.content.Intent
//import android.net.Uri
//import android.os.Build
//import android.os.Bundle
//import androidx.annotation.RequiresApi
//import androidx.appcompat.app.AppCompatActivity
//import kotlinx.android.synthetic.main.activity_retrofit_send_file_example.*
//import okhttp3.MediaType
//import okhttp3.MultipartBody
//import okhttp3.RequestBody
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import java.io.File
//import java.io.FileInputStream
//import java.io.FileOutputStream
//
//@Suppress("DEPRECATION")
//class RetrofitSendFileExample4 : AppCompatActivity() {
//
//    var filePath = "";
//    private var bitmap:Uri? = null
//    private var selectedImageUri: Uri? = null
//
//    @RequiresApi(Build.VERSION_CODES.KITKAT)
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_retrofit_send_file_example4)
//        image_view.setOnClickListener {
//            openImageChooser()
//        }
//
//        button_upload.setOnClickListener {
//            uploadImage()
//        }
//    }
//
//    private fun openImageChooser() {
//        Intent(Intent.ACTION_PICK).also {
//            it.type = "sound/*"
//            val mimeTypes = arrayOf("sound/wav", "sound/mp3")
//            it.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
//            startActivityForResult(it, RetrofitSendFileExample3.REQUEST_CODE_PICK_IMAGE)
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK) {
//
//            when (requestCode) {
//
//                REQUEST_CODE_PICK_IMAGE -> {
//                    selectedImageUri = data?.data
//                    //bitmap='primary:Android/data/kr.co.jolph.soundapp/cache/2021-05-09T06:46:58.655.wav'
//                    //bitmap = MediaStore.Images.Media.getBitmap(contentResolver, data?.data ).toString();
//                    //image_view.setImageURI("primary:Android/data/kr.co.jolph.soundapp/cache/2021-05-09T06:46:58.655.wav")
//                    //image_view.setImageURI("sdk_gphone_x86/Android/data/kr.co.jolph.soundapp.cache/2021-05-09T06:46:58.655.wav")
//                    image_view.setImageURI(bitmap)
//                }
//            }
//        }
//    }
//
//    @RequiresApi(Build.VERSION_CODES.KITKAT)
//    private fun uploadImage() {
//        if (selectedImageUri == null) {
//            layout_root.snackbar("Select an sound First")
//            return
//        }
//
//        val parcelFileDescriptor =
//            contentResolver.openFileDescriptor(selectedImageUri!!, "r", null) ?: return
//
//        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
//        val file = File("sdk_gphone_x86/Android/data/kr.co.jolph.soundapp.cache/2021-05-09T06:46:58.655.wav", contentResolver.getFileName(selectedImageUri!!))
//        val outputStream = FileOutputStream(file)
//        inputStream.copyTo(outputStream)
//
//        progress_bar.progress = 0
//        val body = UploadRequestBody(file, "wav", this)
//        MyAPI().uploadImage(
//            MultipartBody.Part.createFormData(
//                "wav",
//                file.name,
//                body
//            ),
//            RequestBody.create(MediaType.parse("multipart/form-data"), "json")
//        ).enqueue(object : Callback<UploadResponse> {
//            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
//                layout_root.snackbar(t.message!!)
//                progress_bar.progress = 0
//            }
//
//            override fun onResponse(
//                call: Call<UploadResponse>,
//                response: Response<UploadResponse>
//            ) {
//                response.body()?.let {
//                    layout_root.snackbar(it.message)
//                    progress_bar.progress = 100
//                }
//            }
//        })
//
//    }
//
//    fun onProgressUpdate(percentage: Int) {
//        progress_bar.progress = percentage
//    }
//
//    companion object {
//        const val REQUEST_CODE_PICK_IMAGE = 101
//    }
//}