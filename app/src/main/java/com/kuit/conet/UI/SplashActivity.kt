package com.kuit.conet.UI

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.kuit.conet.UI.Login.LoginActivity
import com.kuit.conet.Network.RetrofitInterface
import com.kuit.conet.Network.getRetrofit
import com.kuit.conet.Network.refreshResponse
import com.kuit.conet.databinding.ActivitySplashBinding
import com.kuit.conet.getRefreshToken
import com.kuit.conet.saveUserRefreshToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Response

class SplashActivity : AppCompatActivity(){
    lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//        Handler(Looper.getMainLooper()).postDelayed({
//
//            // 일정 시간이 지나면 MainActivity로 이동
//            renewalAccessToken(getRefreshToken(this))
//
//            // 이전 키를 눌렀을 때 스플래스 스크린 화면으로 이동을 방지하기 위해
//            // 이동한 다음 사용안함으로 finish 처리
//            finish()
//
//        }, 2000)

        // 스플래시 화면을 보여주는 핸들러

        renewalAccessToken(getRefreshToken(this))
        finish()



    }

    private fun renewalAccessToken(refreshToken: String?) { // 리프레쉬 토큰 사용해서 AccessToken갱신하는 함수
        Log.d("getAccess", "getAccess 함수 실행")
        val signUpService = getRetrofit().create(RetrofitInterface::class.java)
        //Log.d("getAccess", "getAccess 함수 실행2")
        signUpService.getAccess("Bearer $refreshToken", "").enqueue(object :
            retrofit2.Callback<refreshResponse> { // 서버와 비동기적으로 데이터 주고받을 수 있는 방법 enqueue사용
            override fun onResponse( // 통신에 성공했을 경우
                call: Call<refreshResponse>,
                response: Response<refreshResponse>
            ) {
                if (response.isSuccessful) {
                    val resp = response.body()// 성공했을 경우 response body불러오기
                    Log.d("SIGNUP/SUCCESS", resp.toString())
                    Log.d("AccessToken", resp!!.result.accessToken)
                    Log.d("RefreshToken", resp!!.result.refreshToken)
                    saveUserRefreshToken(applicationContext, resp!!.result.refreshToken)
                    val mIntent = Intent(applicationContext, ConetMainActivity::class.java)
                    startActivity(mIntent)
                    finish()

                } else { // 유효하지 않은 refresh token인 경우 -> idToken다시 받아오기 (재로그인)
                    Log.d("error", "실패")
//                    Log.d("login","재로그인")
//                    kakaoLogin()
                    val mIntent = Intent(applicationContext, LoginActivity::class.java)
                    startActivity(mIntent)
                    finish()
                }
            }

            override fun onFailure(call: Call<refreshResponse>, t: Throwable) { // 통신에 실패했을 경우
                Log.d("SIGNUP/FAILURE", t.message.toString()) // 실패한 이유 메세지 출력
                val mIntent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(mIntent)
                finish()
            }

        })
        //Log.d("iswork","${iswork}")
    }
}