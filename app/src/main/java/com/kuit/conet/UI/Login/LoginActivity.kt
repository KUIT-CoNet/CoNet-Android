package com.kuit.conet.UI.Login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.kuit.conet.UI.JoinMemberShip.JoinMembershipActivity
import com.kuit.conet.UI.ConetMainActivity
import com.kuit.conet.Network.RetrofitClient
import com.kuit.conet.R
import com.kuit.conet.Utils.LIFECYCLE
import com.kuit.conet.Utils.NETWORK
import com.kuit.conet.Utils.TAG
import com.kuit.conet.databinding.ActivityLoginBinding
import com.kuit.conet.Utils.saveUserAccessToken
import com.kuit.conet.Utils.saveUserRefreshToken
import com.kuit.conet.data.dto.request.auth.RequestLogin
import com.kuit.conet.data.dto.response.auth.ResponseLogin
import retrofit2.Call
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var iswork = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d(LIFECYCLE, "LoginActivity - onCreate() called")

        if (iswork) {
            val mIntent = Intent(applicationContext, ConetMainActivity::class.java)
            startActivity(mIntent)
        } else {
            val keyHash = Utility.getKeyHash(this)
            Log.d("hash", keyHash)

            /** KakoSDK init */
            KakaoSdk.init(this, this.getString(R.string.kakao_app_key))
        }

        binding.loginKakaoCv.setOnClickListener {
            loginWithKakao() //로그인
        }

    }

    private fun loginWithKakao() {
        Log.d(TAG, "LoginActivity - kakaoLogin() called")

        // 카카오계정으로 로그인 공통 callback 구성
        // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.d(TAG, "LoginActivity - loginWithKakao() called\n카카오계정으로 로그인 실패 : $error")
                setLogin("")
            } else if (token != null) {
                //TODO: 최종적으로 카카오로그인 및 유저정보 가져온 결과
                UserApiClient.instance.me { user, error ->
                    Log.d(
                        TAG,
                        "LoginActivity - loginWithKakao() called\n카카오계정으로 로그인 성공\ntoken: ${token.idToken}\nme: $user"
                    )
                    setLogin(token.idToken)
                }
            }
        }

        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                if (error != null) {
                    Log.d(TAG, "LoginActivity - loginWithKakao() called\n카카오계정으로 로그인 실패 : $error")

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                } else if (token != null) {
                    Log.d(
                        TAG,
                        "LoginActivity - loginWithKakao() called\n카카오계정으로 로그인 성공\ntoken: ${token.idToken}\n"
                    )
                    setLogin(token.idToken)
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
        }


    }

    private fun setLogin(idToken: String?) {
        Log.d(TAG, "LoginActivity - setLogin() called")
        if (idToken.isNullOrBlank()) {
            Log.d(TAG, "LoginActivity - setLogin() called\n비정상적인 로그인입니다.")
        } else {
            getKakaoResponse(idToken)
        }
    }

    private fun getKakaoResponse(idToken: String) {
        RetrofitClient.authInstance.login(
            login = RequestLogin(
                platform = "kakao",
                idToken = idToken,
            )
        ).enqueue(object :
            retrofit2.Callback<ResponseLogin> { // 서버와 비동기적으로 데이터 주고받을 수 있는 방법 enqueue사용
            override fun onResponse( // 통신에 성공했을 경우
                call: Call<ResponseLogin>,
                response: Response<ResponseLogin>
            ) {
                if (response.isSuccessful) {
                    Log.d(NETWORK, "LoginActivity - login() 실행결과 -  성공")

                    val resp = requireNotNull(response.body()) { "LoginActivity - login() 실행결과 불러오기 실패" }
                    saveUserAccessToken(applicationContext, resp.result.accessToken)
                    saveUserRefreshToken(applicationContext, resp.result.refreshToken)

                    if (resp.result.isRegistered) {
                        val intent = Intent(this@LoginActivity, ConetMainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Log.d(TAG, "LoginActivity - 회원가입 첫 시도, 회원가입 화면으로 전환")
                        val intent = Intent(this@LoginActivity, JoinMembershipActivity::class.java)
                        startActivity(intent)
                    }

                } else {
                    Log.d(NETWORK, "LoginActivity - login() 실행결과 -  안좋음")
                }
            }

            override fun onFailure(call: Call<ResponseLogin>, t: Throwable) { // 통신에 실패했을 경우
                Log.d(NETWORK, "LoginActivity - login() 실행결과 -  실패\nbecuase : $t")
            }
        })
    }

}