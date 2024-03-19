package com.kuit.conet.data.api

import com.kuit.conet.Network.RefreshResponse
import com.kuit.conet.data.dto.request.auth.RequestAgreeToTermsAndConditions
import com.kuit.conet.data.dto.request.auth.RequestLogin
import com.kuit.conet.data.dto.response.auth.ResponseAgreeToTermsAndConditions
import com.kuit.conet.data.dto.response.auth.ResponseLogin
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthAPI {
    @POST("auth/login") // :: 로그인(회원가입)
    fun login(
        @Body
        login: RequestLogin
    ): Call<ResponseLogin>

    @POST("auth/term") // :: 약관 동의 및 이름 입력
    fun agreeToTermsAndConditions(
        @Header("Authorization")
        authorization: String,
        @Body
        userInfo: RequestAgreeToTermsAndConditions
    ): Call<ResponseAgreeToTermsAndConditions>

    @POST("auth/regenerate-token") // :: refresh token 재발급
    fun getAccess(
        @Header("Authorization")
        refreshToken: String,
    ): Call<RefreshResponse>

}