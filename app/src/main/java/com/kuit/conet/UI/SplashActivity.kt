package com.kuit.conet.UI

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.kuit.conet.UI.Login.LoginActivity
import com.kuit.conet.Network.RefreshResponse
import com.kuit.conet.Network.RetrofitClient
import com.kuit.conet.Utils.NETWORK
import com.kuit.conet.databinding.ActivitySplashBinding
import com.kuit.conet.Utils.getRefreshToken
import com.kuit.conet.Utils.saveUserRefreshToken
import retrofit2.Call
import retrofit2.Response

class SplashActivity : AppCompatActivity(){

    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        renewalAccessToken(getRefreshToken(this))
//        finish()
    }

    private fun renewalAccessToken(refreshToken: String) { // 리프레쉬 토큰 사용해서 AccessToken갱신하는 함수
        RetrofitClient.instance.getAccess("Bearer $refreshToken").enqueue(object :
            retrofit2.Callback<RefreshResponse> { // 서버와 비동기적으로 데이터 주고받을 수 있는 방법 enqueue사용
            override fun onResponse( // 통신에 성공했을 경우
                call: Call<RefreshResponse>,
                response: Response<RefreshResponse>
            ) {
                if (response.isSuccessful) {
                    val resp = requireNotNull(response.body()) { "SplashActivity - getAccess() 결과 불러오기 실패" }
                    Log.d(NETWORK, "SplashActivity - getAccess() 실행결과 성공")
                    saveUserRefreshToken(applicationContext, resp.result.refreshToken)
                    val mIntent = Intent(applicationContext, ConetMainActivity::class.java)
                    startActivity(mIntent)
                    finish()
                } else { // 유효하지 않은 refresh token인 경우 -> idToken다시 받아오기 (재로그인)
                    Log.d(NETWORK, "SplashActivity - getAccess() 실행결과 안좋음")
                    val mIntent = Intent(applicationContext, LoginActivity::class.java)
                    startActivity(mIntent)
                    finish()
                }
            }

            override fun onFailure(call: Call<RefreshResponse>, t: Throwable) { // 통신에 실패했을 경우
                Log.d(NETWORK, "SplashActivity - getAccess() 실행결과 실패\n이유 : $t")
                val mIntent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(mIntent)
                finish()
            }

        })
    }
}