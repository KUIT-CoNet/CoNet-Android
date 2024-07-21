package com.kuit.conet.UI

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.kuit.conet.*
import com.kuit.conet.UI.Home.HomeFragment
import com.kuit.conet.UI.User.UserFragment
import com.kuit.conet.UI.Group.GroupFragment
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.kuit.conet.Utils.LIFECYCLE
import com.kuit.conet.databinding.ActivityKonetMainBinding

class ConetMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKonetMainBinding
    private val fragmentManager = supportFragmentManager

    private var exitMills: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKonetMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d(LIFECYCLE, "ConetMainActivity - onCreate() called")

        initBottomNavigation()
        binding.mainBnv.itemIconTintList = null

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val fragment = fragmentManager.findFragmentById(R.id.main_fragment)
                if (fragment is HomeFragment) { // Home 프레그먼트에서 뒤로 가기 누르면
                    killApp() // 앱 종료
                } else {
                    binding.mainBnv.selectedItemId = R.id.home_menu // 선택된 프레그먼트를 강제로 홈 프레그먼트로
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback) // callback 등록
    }

    private fun initBottomNavigation() {
        fragmentManager.commit {
            replace(R.id.main_fragment, HomeFragment())
        }

        binding.mainBnv.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home_menu -> {
                    fragmentManager.commit {
                        addToBackStack(null)
                        replace(R.id.main_fragment, HomeFragment())
                    }
                    return@setOnItemSelectedListener true
                }

                R.id.together_menu -> {
                    fragmentManager.commit {
                        addToBackStack(null)
                        replace(R.id.main_fragment, GroupFragment())
                    }
                    return@setOnItemSelectedListener true
                }

                R.id.my_menu -> {
                    fragmentManager.commit {
                        addToBackStack(null)
                        replace(R.id.main_fragment, UserFragment())
                    }
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    fun killApp() {
        if (System.currentTimeMillis() - exitMills > 2000) { //System.currentTimeMillis()현재 시간을 밀리초로 변환한것
            exitMills = System.currentTimeMillis()
            Toast.makeText(this, "뒤로 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else {
            moveTaskToBack(true)                        // 태스크를 백그라운드로 이동
            finishAndRemoveTask()                        // 액티비티 종료 + 태스크 리스트에서 지우기
            android.os.Process.killProcess(android.os.Process.myPid())    // 앱 프로세스 종료
        }
    }
}