package com.kuit.conet.UI

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.kuit.conet.*
import com.kuit.conet.UI.Home.Home
import com.kuit.conet.UI.User.User
import com.kuit.conet.UI.Group.GroupFragment
import androidx.appcompat.app.AppCompatActivity
import com.kuit.conet.databinding.ActivityKonetMainBinding

class ConetMainActivity : AppCompatActivity() {
    lateinit var binding : ActivityKonetMainBinding
    var exit_mills : Long = 0 // 시간을 저장하는 전역변수

    /*
    Home클래스 : 홈 메뉴 구현
    Users클래스 : 모임 메뉴 구현
    User클래스 : My메뉴 구현
    */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKonetMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("AccessToken", "${getAccessToken(this)}")
        Log.d("refreshToken", "${getRefreshToken(this)}")
        Log.d("name", "${getUsername(this)}")
        Log.d("isoption", "${getIsoption(this)}")

        initBottomNavigation()
    }

    private fun initBottomNavigation() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment, Home())
            .commitAllowingStateLoss()


        binding.mainBnv.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home_menu -> {
                    supportFragmentManager.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.main_fragment, Home())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.together_menu -> {
                    supportFragmentManager.beginTransaction()

                        .replace(R.id.main_fragment, GroupFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.my_menu -> {
                    supportFragmentManager.beginTransaction()

                        .replace(R.id.main_fragment, User())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }

        binding.mainBnv.itemIconTintList = null

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val fragment = supportFragmentManager.findFragmentById(R.id.main_fragment) // 프래그 먼트를 띄워주는 프레임 레이아웃 넣어줌 -> 프레임 레이아웃위에 뜬 프레그먼트가 뭔지 알려줌
                if (fragment is Home) { // Home 프레그먼트에서 뒤로 가기 누르면
                    killapp() // 앱 종료
                } else {
                    binding.mainBnv.selectedItemId = R.id.home_menu // 선택된 프레그먼트를 강제로 홈 프레그먼트로
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback) // callback 등록
    }

    fun killapp(){ // 뒤로가기 2초안에 두번 눌러야 종료되게
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