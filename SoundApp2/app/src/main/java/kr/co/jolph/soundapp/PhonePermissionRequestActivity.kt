package kr.co.jolph.soundapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat

class PhonePermissionRequestActivity : AppCompatActivity(),
    ActivityCompat.OnRequestPermissionsResultCallback {

    val watchFrag = watch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // If permissions granted, we start the main activity (shut this activity down).
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startMainActivity()
        }
        setContentView(R.layout.activity_phone_permission_request)
    }

    fun onClickApprovePermissionRequest(view: View?) {
        Log.d(TAG, "onClickApprovePermissionRequest()")

        // On 23+ (M+) devices, External storage permission not granted. Request permission.
        val permissions = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
        ActivityCompat.requestPermissions(this, permissions,PERMISSION_REQUEST_READ_STORAGE)
    }

    fun onClickDenyPermissionRequest(view: View?) {
        Log.d(TAG, "onClickDenyPermissionRequest()")
        startMainActivity()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        val permissionResult = ("Request code: " + requestCode + ", Permissions: " + permissions
                + ", Results: " + grantResults)
        Log.d(TAG,"onRequestPermissionsResult(): $permissionResult")
        if (requestCode == PERMISSION_REQUEST_READ_STORAGE) {
            // Close activity regardless of user's decision (decision picked up in main activity).
            startMainActivity()
        }
    }

    private fun startMainActivity() {
        val mainActivityIntent = Intent(this, MainActivity::class.java)

        val serviceStartedActivity = intent.getBooleanExtra(
            watchFrag.EXTRA_PROMPT_PERMISSION_FROM_WEAR, false
        )
        if (serviceStartedActivity) {
            mainActivityIntent.putExtra(
                watchFrag.EXTRA_PROMPT_PERMISSION_FROM_WEAR, true
            )
        }
        startActivity(mainActivityIntent)
    }

    companion object {
        private const val TAG = "PhoneRationale"

        /* Id to identify Location permission request. */
        private const val PERMISSION_REQUEST_READ_STORAGE = 1
    }
}