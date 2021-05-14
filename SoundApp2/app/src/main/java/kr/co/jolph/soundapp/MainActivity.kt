package kr.co.jolph.soundapp


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.time.seconds
import android.content.Intent
import kotlinx.android.synthetic.main.fragment_home.*

class MainActivity : AppCompatActivity() {

    private val fra_home = home()
    private val fra_camera = camera()
    private val fra_settings = settings()
    private val fra_watch = watch()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init3()

        var actionBar : ActionBar?
        actionBar = supportActionBar;
        actionBar?.hide()

        initNaviBar()
    }
    private fun init3(){
        retrofitimageexample3.setOnClickListener {
           val intent= Intent(this, Getresultfromserver::class.java)
            startActivity(intent)
        }




    }
    private fun initNaviBar(){

        bottomNavi.run{
            setOnNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.mFirst->{
                        changeFrag(fra_home)
                    }
                    R.id.mSecond ->{
                        changeFrag(fra_camera)
                    }
                    R.id.mThird ->{
                        changeFrag(fra_settings)
                    }
                    R.id.mFourth ->{
                        changeFrag(fra_watch)
                    }

                }
                true
            }
            selectedItemId = R.id.mFirst
        }
    }

    private fun changeFrag(fragment: Fragment){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }
}
