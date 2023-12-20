package com.kuit.conet.UI.Login

import android.app.Activity
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
import com.kuit.conet.Network.KaKaoResponse
import com.kuit.conet.Network.RetrofitInterface
import com.kuit.conet.Network.getRetrofit
import com.kuit.conet.R
import com.kuit.conet.databinding.ActivityLoginBinding
import com.kuit.conet.saveUserAccessToken
import com.kuit.conet.saveUserRefreshToken
import retrofit2.Call
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    var iswork = false
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("start","시작")
        //renewalAccessToken(getRefreshToken(this))
        //Thread.sleep(3000)
        if (iswork) {
            val mIntent = Intent(applicationContext, ConetMainActivity::class.java)
            startActivity(mIntent)
        } else {
            val keyHash = Utility.getKeyHash(this)
            Log.d("hash", "${keyHash}")

            /** KakoSDK init */
            KakaoSdk.init(this, this.getString(R.string.kakao_app_key))
        }


        binding.loginKakaoCv.setOnClickListener {
            kakaoLogin() //로그인
        }

    }

    private fun kakaoLogin() {
        Log.d("login", "시작")
        // 카카오계정으로 로그인 공통 callback 구성
        // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                TextMsg(this, "카카오계정으로 로그인 실패 : ${error}")
                Log.d("오류","${error}")
                setLogin(false, "")
            } else if (token != null) {
                //TODO: 최종적으로 카카오로그인 및 유저정보 가져온 결과
                UserApiClient.instance.me { user, error ->
                    TextMsg(
                        this, "카카오계정으로 로그인 성공 \n\n " +
                                "token: ${token.idToken} \n\n " +
                                "me: $user"
                    )
                    Log.d("idToken", "${token.idToken}")
                    setLogin(true, token.idToken)
                }
            }
        }
        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                if (error != null) {
                    TextMsg(this, "카카오톡으로 로그인 실패 : ${error}")

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                } else if (token != null) {
                    TextMsg(this, "카카오톡으로 로그인 성공 ${token.idToken}")
                    Log.d("idToken", "${token.idToken}")
                    setLogin(true, token.idToken)
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
        }


    }




    private fun TextMsg(act: Activity, msg: String) {
        //binding.tvHashKey.text = msg
    }

    private fun setLogin(bool: Boolean, idToken: String?) {
        //binding.btnStartKakaoLogin.visibility = if(bool) View.GONE else View.VISIBLE
        Log.d("setLogin","실행")
        if (idToken == null) {
            Log.d("error", "비정상적인 로그인입니다.")
        } else {
            Log.d("successs","정상로그인")
            getKakaoResponse(idToken)
        }
    }

    private fun getKakaoResponse(idToken: String?) { // 레트로핏 라이브러리 사용해서 서버에 idToken주고 그에 대한 응답받아오는 메소드
        Log.d("getkakaoResponse","실행")
        Log.d("idToken","${idToken}")
        val signUpService = getRetrofit().create(RetrofitInterface::class.java)
        signUpService.signUp(idToken).enqueue(object :
            retrofit2.Callback<KaKaoResponse> { // 서버와 비동기적으로 데이터 주고받을 수 있는 방법 enqueue사용
            override fun onResponse( // 통신에 성공했을 경우
                call: Call<KaKaoResponse>,
                response: Response<KaKaoResponse>
            ) {
                if (response.isSuccessful) {
                    val resp = response.body()// 성공했을 경우 response body불러오기
                    Log.d("SIGNUP/SUCCESS", resp.toString())
                    //renewalAccessToken(resp!!.result.refreshToken)
                    saveUserAccessToken(applicationContext, resp!!.result.accessToken)
                    saveUserRefreshToken(applicationContext, resp!!.result.refreshToken)
                    Log.d("성공!","success")
                    if (resp!!.result.isRegistered) {
                        Log.d("idToken","${idToken}")
                        val mIntent = Intent(applicationContext, ConetMainActivity::class.java)
                        startActivity(mIntent)
                    } else {
                        Log.d("회원가입 화면전환", "Join")
                        val mIntent = Intent(applicationContext, JoinMembershipActivity::class.java)
                        startActivity(mIntent)
                    }
                }
            }

            override fun onFailure(call: Call<KaKaoResponse>, t: Throwable) { // 통신에 실패했을 경우
                Log.d("SIGNUP/FAILURE", t.message.toString()) // 실패한 이유 메세지 출력
            }

        })
    }





}