package com.kuit.conet.data.api

import com.kuit.conet.Network.EditUserName
import com.kuit.conet.Network.ResponseCreateGroup
import com.kuit.conet.Network.ResponseEnrollGroup
import com.kuit.conet.Network.ResponseGetGroup
import com.kuit.conet.data.dto.response.team.ResponseGetGroupMembers
import com.kuit.conet.Network.ResponseGroupCode
import com.kuit.conet.Network.ResponseUpdateGroup
import com.kuit.conet.data.dto.request.team.RequestGetInviteCode
import com.kuit.conet.data.dto.request.team.RequestLeaveGroup
import com.kuit.conet.data.dto.request.team.RequestTeamJoin
import com.kuit.conet.data.dto.response.team.ResponseGetGroupDetail
import com.kuit.conet.data.dto.response.team.ResponseGetGroups
import com.kuit.conet.data.dto.response.team.ResponseGetInviteCode
import com.kuit.conet.data.dto.response.team.ResponseLeaveGroup
import com.kuit.conet.data.dto.response.team.ResponseTeamJoin
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface TeamAPI {
    @GET("team") // :: 모임 리스트 조회
    fun getGroups(
        @Header("Authorization")
        authorization: String
    ): Call<ResponseGetGroups>

    @GET("team/{teamId}/members") // :: 모임 구성원 조회
    fun getGroupMembers(
        @Header("Authorization")
        authorization: String,
        @Path("teamId")
        groupId: Long,
    ): Call<ResponseGetGroupMembers>

    @GET("team/{teamId}") // :: 모임 상세 조회
    fun getGroupDetail(
        @Header("Authorization")
        authorization: String,
        @Path("teamId")
        groupId: Long,
    ): Call<ResponseGetGroupDetail>

    /*@DELETE("team/{teamId}") // :: 모임 삭제
    fun DeleteGroup(
        @Header("Authorization")
        authorization: String,
        @Path("teamId")
        teamId: Int,
    ): Call<EditUserName>*/

    @POST("team/join") // :: 모임 참여
    fun enrollGroup(
        @Header("Authorization")
        authorization: String,
        @Body
        request: RequestTeamJoin,
    ): Call<ResponseTeamJoin>

    @Multipart
    @POST("team") // :: 모임 생성
    fun createGroup(
        @Header("Authorization")
        authorization: String,
        @Part
        file: MultipartBody.Part,
        @Part("request")
        request: RequestBody,
    ): Call<ResponseCreateGroup>

    @POST("team/leave") // :: 모임 탈퇴
    fun leaveGroup(
        @Header("Authorization")
        authorization: String,
        @Body
        request: RequestLeaveGroup
    ): Call<ResponseLeaveGroup>

    /*@Multipart
    @POST("team/update") // :: 모임 수정
    fun updateGroup(
        @Header("Authorization")
        authorization: String,
        @Part
        file: MultipartBody.Part,
        @Part("request")
        request: RequestBody,
    ): Call<ResponseUpdateGroup>*/

    @POST("team/code") // :: 초대 코드 재발급
    fun getInviteCode(
        @Body
        request: RequestGetInviteCode,
    ): Call<ResponseGetInviteCode>
}