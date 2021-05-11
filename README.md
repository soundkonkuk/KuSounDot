# KuSounDot
test
https://www.google.com/search?q=retrofit2+multipart&oq=retrofit2+multipart&aqs=chrome..69i57j0l4j0i30l4j0i5i30.4455j0j7&sourceid=chrome&ie=UTF-8
https://velog.io/@dev_thk28/Android-Retrofit2-Multipart%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0-Java
https://www.google.com/search?q=multipart+%EC%86%8C%EB%A6%AC%EC%A0%84%EC%86%A1&oq=multipart+%EC%86%8C%EB%A6%AC%EC%A0%84%EC%86%A1&aqs=chrome..69i57.3400j0j7&sourceid=chrome&ie=UTF-8
https://velog.io/@sa833591/form-data-%EA%B7%B8%EB%A6%AC%EA%B3%A0-MultipartFile
https://www.google.com/search?q=formdata+multipart+sound&oq=formdata+multipart+sound&aqs=chrome..69i57.5152j0j7&sourceid=chrome&ie=UTF-8
https://github.com/jsierles/react-native-audio/issues/107

https://forums.expo.io/t/how-to-upload-audio-or-generic-filetype/5082
https://groups.google.com/g/discuss-webrtc/c/f13d84TYV6k
https://stackoverflow.com/questions/50575995/how-to-post-audio-and-image-as-multipart-formdata-in-native-android


package kr.co.jolph.soundapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_retrofit_send_file_example.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

@Suppress("DEPRECATION")
class RetrofitSendFileExample3 : AppCompatActivity() {

    var filePath = "";
    private var bitmap:Uri? = null
    private var selectedImageUri: Uri? = null

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retrofit_send_file_example3)
//        image_view.setOnClickListener {
//            //openImageChooser()
//        }

        button_upload.setOnClickListener {
            uploadSound()
        }
    }

    private fun openImageChooser() {
        Intent(Intent.ACTION_PICK).also {
            it.type = "sound/*"
            val mimeTypes = arrayOf("sound/wav", "sound/mp3")
            it.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            startActivityForResult(it, REQUEST_CODE_PICK_IMAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            when (requestCode) {

                REQUEST_CODE_PICK_IMAGE -> {
                    selectedImageUri = data?.data
                    //bitmap='primary:Android/data/kr.co.jolph.soundapp/cache/2021-05-09T06:46:58.655.wav'
                    //bitmap = MediaStore.Images.Media.getBitmap(contentResolver, data?.data ).toString();
                    //image_view.setImageURI("primary:Android/data/kr.co.jolph.soundapp/cache/2021-05-09T06:46:58.655.wav")
                    //image_view.setImageURI("sdk_gphone_x86/Android/data/kr.co.jolph.soundapp.cache/2021-05-09T06:46:58.655.wav")
                    image_view.setImageURI(bitmap)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun uploadSound() {
        if (selectedImageUri == null) {
            layout_root.snackbar("Select an sound First")
            return
        }

        val parcelFileDescriptor =
            contentResolver.openFileDescriptor(selectedImageUri!!, "r", null) ?: return

        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        //image_view.setImageURI("primary:Android/data/kr.co.jolph.soundapp/cache/2021-05-09T06:46:58.655.wav")
        //image_view.setImageURI("sdk_gphone_x86/Android/data/kr.co.jolph.soundapp.cache/2021-05-09T06:46:58.655.wav")
        //image_view.setImageURI("primary:Android/data/kr.co.jolph.soundapp/cache/2021-05-09T06:46:58.655.wav")
        //image_view.setImageURI("sdk_gphone_x86/Android/data/kr.co.jolph.soundapp.cache/2021-05-09T06:46:58.655.wav")
        //image_view.setImageURI("Android/data/kr.co.jolph.soundapp.cache/2021-05-09T06:46:58.655.wav")
        //image_view.setImageURI("data/data/kr.co.jolph.soundapp.cache/2021-05-09T06:46:58.655.wav")
        val file2 = File("primary:Android/data/kr.co.jolph.soundapp/cache/2021-05-09T06:46:58.655.wav", contentResolver.getFileName(selectedImageUri!!))
        val file = File("primary:Android/data/kr.co.jolph.soundapp/cache/2021-05-09T06:46:58.655.wav")
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

        progress_bar.progress = 0
        //https://velog.io/@dev_thk28/Android-Retrofit2-Multipart%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0-Java
        val body = UploadRequestBody3(file, "wav", this)
        MyAPI().uploadSound(
            MultipartBody.Part.createFormData(
                "wav", //서버에서 받는 키값 String
                file.name,  //파일 이름 String
                body   //파일 경로를 가지는 RequestBody 객체
            ),
            RequestBody.create(MediaType.parse("multipart/form-data"), "json")
        ).enqueue(object : Callback<UploadResponse> {
            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                layout_root.snackbar(t.message!!)
                progress_bar.progress = 0
            }

            override fun onResponse(
                call: Call<UploadResponse>,
                response: Response<UploadResponse>
            ) {
                response.body()?.let {
                    layout_root.snackbar(it.message)
                    progress_bar.progress = 100
                }
            }
        })

    }

    fun onProgressUpdate(percentage: Int) {
        progress_bar.progress = percentage
    }

    companion object {
        const val REQUEST_CODE_PICK_IMAGE = 101
    }
}
