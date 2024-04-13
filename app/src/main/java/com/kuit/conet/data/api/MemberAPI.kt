package com.kuit.conet.data.api

import com.kuit.conet.data.dto.request.member.RequestEditUserName
import com.kuit.conet.data.dto.request.member.RequestPostBookmark
import com.kuit.conet.data.dto.response.member.ResponseEditUserImg
import com.kuit.conet.data.dto.response.member.ResponseEditUserName
import com.kuit.conet.data.dto.response.member.ResponseGetBookmarkGroups
import com.kuit.conet.data.dto.response.member.ResponseGetUserInfo
import com.kuit.conet.data.dto.response.member.ResponsePostBookmark
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface MemberAPI {
    @GET("member") // :: 사용자 조회
    fun getUserInfo(
        @Header("Authorization")
        authorization: String,
    ): Call<ResponseGetUserInfo>

    @POST("member/name") // :: 사용자 이름 수정
    fun editUserName(
        @Header("Authorization")
        authorization: String,
        @Body
        request: RequestEditUserName,
    ): Call<ResponseEditUserName>

    @Multipart
    @POST("member/image") // ::  사용자 이미지 수정
    fun editUserImg(
        @Header("Authorization")
        authorization: String,
        @Part
        image: MultipartBody.Part,
    ): Call<ResponseEditUserImg>

    @GET("member/bookmark") // :: 북마크 된 모임 조회
    fun getBookmarkGroups(
        @Header("Authorization")
        authorization: String,
    ): Call<ResponseGetBookmarkGroups>

    @POST("member/bookmark") // :: 북마크 추가/삭제
    fun postBookmark(
        @Header("Authorization")
        authorization: String,
        @Body
        request: RequestPostBookmark,
    ): Call<ResponsePostBookmark>
}