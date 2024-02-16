package com.kuit.conet.Network

import com.google.gson.annotations.JsonAdapter
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import okhttp3.RequestBody
import retrofit2.http.*

interface RetrofitInterface {
    @POST("auth/login")
    fun signUp(
        @Body login: Login
    ) : Call<KaKaoResponse>

    @POST("auth/regenerate-token")
    fun getAccess(
        @Header("Authorization") refreshToken:String?,
        @Body default : String
    ) : Call<refreshResponse>

    @POST("auth/term-and-name")
    fun registed(
        @Header("Authorization") authorization : String?,
        @Body sendInfo: sendInfo
    ) : Call<registedResponse>
    @GET("home/month")
    fun homepromiseshow( // 홈 화면 특정 달의 약속 조회
        @Header("Authorization") authorization : String?,
        @Query("searchDate") searchDate : String
    ) : Call<HomePlanShow>

    @GET("home/day")
    fun homepromiseinfo( // 홈 화면 특정 날짜의 약속 조회
        @Header("Authorization") authorization : String?,
        @Query("searchDate") searchDate : String
    ) : Call<HomePlanInfo>

    @GET("home/waiting")
    fun homeoncallshow( // 홈 화면 대기중 약속 조회
        @Header("Authorization") authorization : String?
    ) : Call<HomeOncall>

    @GET("user")
    fun showuser(
        @Header("Authorization") authorization : String?
    ) : Call<ShowUser>

    @POST("user/name")
    fun editName(
        @Header("Authorization") authorization : String?,
        @Body name : String
    ) : Call<EditUserName>

    @Multipart
    @POST("user/image")
    fun editImage(
        @Header("Authorization") authorization: String?,
        @Part image: MultipartBody.Part
    ) : Call<EditUserImage>

    @POST("user/delete")
    fun deleteUser(
        @Header("Authorization") authorization : String?
    ) : Call<DeleteUser>

    @Multipart
//    @Headers("Content-Type: application/json")
    @POST("team/create")
    fun createGroup(
        @Header("Authorization") authorization: String,
        @Part file: MultipartBody.Part,
        @Part("request") request: RequestBody
    ): Call<ResponseCreateGroup>

    @POST("team/participate")
    fun enrollGroup(
        @Header("Authorization") authorization: String,
        @Body inviteCode: String
    ): Call<ResponseEnrollGroup>

    @POST("team/code")
    fun getGroupCode(
        @Header("Authorization") authorization: String,
        @Body teamId: Int
    ): Call<ResponseGroupCode>

    @GET("team")
    fun getGroup(
        @Header("Authorization") authorization: String
    ): Call<ResponseGetGroup>

    @GET("team/bookmark")
    fun getBookmarkGroup(
        @Header("Authorization") authorization: String
    ): Call<ResponseGetGroup>

    @Multipart
//    @Headers("Content-Type: application/json")
    @POST("team/update")
    fun updateGroup(
        @Header("Authorization") authorization: String,
        @Part file: MultipartBody.Part,
        @Part("request") request: RequestBody
    ): Call<ResponseUpdateGroup>

    @GET("team/members")
    fun getGroupMembers(
//        @Header("Authorization") authorization: String,
        @Query("teamId") groupId: Int
    ): Call<ResponseGetGroupMembers>

    @POST("team/plan/delete")
    fun deletePlan(
        @Body planId: Int
    ): Call<ResponseDeletePlan>

    //팀 내 특정 달의 약속 조회
    @GET("team/plan/month")
    fun ShowGroupMonth(
        @Query("teamId") teamId: Int,
        @Query("searchDate") searchDate: String
    ) : Call<HomePlanShow>

    //팀 내 특정 날짜의 약속 조회
    @GET("team/plan/day")
    fun ShowGroupConfirmPlan(
        @Query("teamId") teamId: Int,
        @Query("searchDate") searchDate : String
    ) : Call<HomePlanInfo>

    //팀 내 대기 중인 약속 조회
    @GET("team/plan/waiting")
    fun ShowGroupOncall(
        @Query("teamId") teamId: Int
    ) : Call<HomeOncall>

    //사이드바 확정 약속 조회
    @GET("team/plan/fixed")
    fun ShowSideBarConfirm(
        @Query("teamId") teamId : Int
    ) : Call<ResponseSidePlan>

    //팀 사이드바 - 지난 약속 조회
    @GET("team/plan/past")
    fun ShowLastPlan(
        @Query("teamId") teamId : Int
    ) : Call<ResponseSidePlan>

    @POST("team/plan/create")
    fun MakePlan(
        @Body makePlanInfo : MakePlanInfo
    ) : Call<ResponseMakePlan>

    @POST("team/bookmark")
    fun enrollBookmark(
        @Header("Authorization") authorization: String,
        @Body teamId: Int
    ) : Call<ResponseEnrollBookmark>

    @POST("team/bookmark/delete")
    fun deleteBookmark(
        @Header("Authorization") authorization: String,
        @Body teamId: Int
    ): Call<ResponseDeleteBookmark>

    //모임 나가기
    @POST("team/leave")
    fun LeaveGroup(
        @Header("Authorization") authorization: String,
        @Body teamId: Int
    ) : Call<EditUserName>
    // 모임 삭제
    @POST("team/delete")
    fun DeleteGroup(
        @Header("Authorization") authorization: String,
        @Body teamId: Int
    ) : Call<EditUserName>

    //히스토리 조회
    @GET("history")
    fun ShowHistory(
        @Query("teamId") teamId : Int
    ) : Call<ResponseShowHistory>
    //내 가능한 시간 입력
    @POST("team/plan/time")
    fun InputMyTime(
        @Header("Authorization") authorization: String,
        @Body inputMyTime: InputMyTime
    ): Call<ResponseInputMyTime>

    @GET("team/plan/detail")
//    @Headers("Content-Type: application/json")
    fun getPlanDetail(
        @Query("planId") planId: Int
    ): Call<ResponseGetPlanDetail>

    @Multipart
//    @Headers("Content-Type: application/json")
    @POST("team/plan/update-fixed")
    fun updatePlanDetail(
        @Part("requestBody") requestBody: RequestBody,
        @Part file: MultipartBody.Part?
    ): Call<ResponseUpdatePlanDetail>

    @GET("team/plan/non-history")
    fun ShownonHistory(
        @Query("teamId") teamId: Int
    ): Call<ResponseSidePlan>
    @Multipart
//    @Headers("Content-Type: application/json")
    @POST("history/register")
    fun registHistory(
        @Part("registerRequest") requestBody: RequestBody,
        @Part file: MultipartBody.Part?
    ): Call<ResponseRegistHistory>

    @GET("team/detail")
    fun getGroupDetail(
        @Header("Authorization") authorization: String,
        @Query("teamId") groupId: Int
    ): Call<ResponseGetGroupDetail>
    @POST("team/plan/update-waiting")
    fun UpdateWaiting(
        @Body updateWaiting: UpdateWaiting
    ): Call<ResponseUpdateWaiting>

    @GET("team/plan/member-time")
    fun ShowMemTime(
        @Query("planId") planId : Int
    ): Call<ShowMemTime>

    @GET("team/plan/user-time")
    fun ShowMyTime(
        @Header("Authorization") authorization: String,
        @Query("planId") planId : Int
    ): Call<ShowMyTime>

    @POST("team/plan/fix")
    fun FixPlan(
        @Body fixPlan: FixPlan
    ): Call<ResponseFixPlan>

}