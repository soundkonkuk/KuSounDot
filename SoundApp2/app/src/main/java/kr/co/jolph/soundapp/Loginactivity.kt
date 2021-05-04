package kr.co.jolph.soundapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_loginactivity.*

class Loginactivity : AppCompatActivity() {

    private var firebaseAuth: FirebaseAuth? = null
    var reviewpossible = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loginactivity)
        firebaseAuth = FirebaseAuth.getInstance();
        MakeButton.setOnClickListener {
            createEmail()
        }
        loginButton.setOnClickListener {
            loginEmail()
        }
        init2()
    }
    private fun createEmail(){

        firebaseAuth!!.createUserWithEmailAndPassword(inputID.text.toString(), inputPW.text.toString())
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = firebaseAuth?.currentUser
                    Toast.makeText(this, "회원가입을 완료하였습니다!",Toast.LENGTH_SHORT).show()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this, "회원가입을 실패하였습니다. 이메일형식을 확인해주세요!",Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun loginEmail(){

        firebaseAuth!!.signInWithEmailAndPassword(inputID.text.toString(), inputPW.text.toString())
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(this, "로그인이 성공적으로 완료되었습니다!",Toast.LENGTH_SHORT).show()
                    val user = firebaseAuth?.currentUser
                    reviewpossible="login해따"
                    val i = Intent(this, MainActivity::class.java)
                    startActivity(i)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this, "로그인에 실패하였습니다 다시확인해주세요!",Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun init2(){
        NologinButton.setOnClickListener {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }
    }
}