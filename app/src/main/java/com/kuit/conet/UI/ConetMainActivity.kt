package com.kuit.conet.UI

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.kuit.conet.*
import com.kuit.conet.UI.Home.HomeFragment
import com.kuit.conet.UI.User.User
import com.kuit.conet.UI.Group.GroupFragment
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.kuit.conet.databinding.ActivityKonetMainBinding

class ConetMainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityKonetMainBinding
    private val fragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKonetMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBottomNavigation()
        binding.mainBnv.itemIconTintList = null

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val fragment = fragmentManager.findFragmentById(R.id.main_fragment)
                if (fragment is HomeFragment) { // Home 프레그먼트에서 뒤로 가기 누르면
                    killapp() // 앱 종료
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
                        replace(R.id.main_fragment, User())
                    }
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    fun killapp(){ // 뒤로가기 2초안에 두번 눌러야 종료되게
        var exit_mills : Long = 0
        if(System.currentTimeMillis() - exit_mills > 2000){ //System.currentTimeMillis()현재 시간을 밀리초로 변환한것
            exit_mills = System.currentTimeMillis()
            Toast.makeText(this, "뒤로 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else{
            moveTaskToBack(true);						// 태스크를 백그라운드로 이동
            finishAndRemoveTask();						// 액티비티 종료 + 태스크 리스트에서 지우기
            android.os.Process.killProcess(android.os.Process.myPid());	// 앱 프로세스 종료
        }
    }
}