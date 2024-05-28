package com.kuit.conet.UI

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.kuit.conet.UI.Login.LoginActivity
import com.kuit.conet.Network.RetrofitClient
import com.kuit.conet.UI.application.CoNetApplication
import com.kuit.conet.Utils.LIFECYCLE
import com.kuit.conet.Utils.NETWORK
import com.kuit.conet.databinding.ActivitySplashBinding
import com.kuit.conet.Utils.getRefreshToken
import com.kuit.conet.Utils.saveUserRefreshToken
import com.kuit.conet.data.dto.response.auth.ResponseRenewTokens
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.nio.charset.Charset

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d(LIFECYCLE, "SplashActivity - onCreate() called")

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        lifecycleScope.launch {
            val refreshToken =
                CoNetApplication.getInstance().getDataStore().bearerRefreshToken.first()

            renewTokens(refreshToken)
        }

//        renewTokens(getRefreshToken(this))
    }

    private fun renewTokens(refreshToken: String) {
        RetrofitClient.authInstance.renewTokens(
            refreshToken = refreshToken
        ).enqueue(object : retrofit2.Callback<ResponseRenewTokens> {
            override fun onResponse(
                call: Call<ResponseRenewTokens>,
                response: Response<ResponseRenewTokens>
            ) {
                if (response.isSuccessful) {
                    Log.d(NETWORK, "SplashActivity - getAccess() 실행결과 성공")
                    val resp =
                        requireNotNull(response.body()) { "SplashActivity - getAccess() 결과 불러오기 실패" }

                    lifecycleScope.launch {
                        CoNetApplication.getInstance().getDataStore().also {
                            it.setAccesToken(resp.result.accessToken)
                            it.setRefreshToken(resp.result.refreshToken)
                        }

                        val mIntent = Intent(applicationContext, ConetMainActivity::class.java)
                        startActivity(mIntent)
                        finish()
                    }
                    /*saveUserRefreshToken(applicationContext, resp.result.refreshToken)

                    val mIntent = Intent(applicationContext, ConetMainActivity::class.java)
                    startActivity(mIntent)
                    finish()*/
                } else {
                    Log.d(NETWORK, "SplashActivity - getAccess() 실행결과 안좋음\nresponse : $response")
                    val mIntent = Intent(applicationContext, LoginActivity::class.java)
                    startActivity(mIntent)
                    finish()
                }
            }

            override fun onFailure(
                call: Call<ResponseRenewTokens>,
                t: Throwable
            ) {
                Log.d(NETWORK, "SplashActivity - getAccess() 실행결과 실패\n이유 : $t")
                val mIntent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(mIntent)
                finish()
            }

        })
    }
}