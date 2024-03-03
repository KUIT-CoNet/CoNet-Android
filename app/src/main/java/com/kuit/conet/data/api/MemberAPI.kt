package com.kuit.conet.data.api

import com.kuit.conet.Network.EditUserImage
import com.kuit.conet.Network.EditUserName
import com.kuit.conet.Network.ResponseGetGroup
import com.kuit.conet.Network.ShowUser
import com.kuit.conet.data.dto.request.member.RequestPostBookmark
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
    /*@GET("member") // :: 사용자 조회
    fun getUser(
        @Header("Authorization")
        authorization: String,
    ): Call<ShowUser>

    @POST("member/name") // :: 사용자 이름 수정
    fun editName(
        @Header("Authorization")
        authorization: String,
        @Body
        name: String,
    ): Call<EditUserName>

    @Multipart
    @POST("member/image") // ::  사용자 이미지 수정
    fun editImage(
        @Header("Authorization")
        authorization: String,
        @Part
        image: MultipartBody.Part,
    ): Call<EditUserImage>*/

    @GET("member/bookmark") // :: 북마크 된 모임 조회
    fun getBookmarkGroup(
        @Header("Authorization")
        authorization: String,
    ): Call<ResponseGetGroup>

    @POST("member/bookmark") // :: 북마크 추가/삭제
    fun postBookmark(
        @Header("Authorization")
        authorization: String,
        @Body
        request: RequestPostBookmark,
    ): Call<ResponsePostBookmark>
}