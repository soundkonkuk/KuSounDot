package kr.co.jolph.soundapp
import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
//import kotlinx.android.synthetic.main.fragment_home.retrofitimageexample3
import kotlinx.android.synthetic.main.fragment_home.view.*
import java.time.LocalDateTime
var startnumber=100
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
@Suppress("UNREACHABLE_CODE")
class home : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var output: String? = null
    fun getoutput(): String? {
        return output
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_home, container, false)
        view.button_start_recording.setOnClickListener {
            val a = 1
            if (startnumber>99999)
            {startnumber-=100000}
            val intent= Intent(activity, Setsooundactivitynew::class.java)
            startActivity(intent)
        }
        view.button_stop_recording.setOnClickListener {
            val a = 2
            startnumber+=100000
            val intent= Intent(activity, Setsooundactivitynew2::class.java)
            startActivity(intent)
        }
        return view
    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                settings().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}