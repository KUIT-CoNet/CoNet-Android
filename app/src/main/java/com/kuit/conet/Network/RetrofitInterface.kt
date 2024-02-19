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
    // AUTH //
    @POST("auth/login") // :: 로그인(회원가입)
    fun signUp(
        @Body login: Login
    ) : Call<KaKaoResponse>

    @POST("auth/term") // :: 약관 동의 및 이름 입력
    fun registed(
        @Header("Authorization") authorization : String?,
        @Body sendInfo: sendInfo
    ) : Call<registedResponse>

    @POST("auth/regenerate-token") // :: refresh token 재발급
    fun getAccess(
        @Header("Authorization") refreshToken:String?,
        @Body default : String
    ) : Call<refreshResponse>

    // HOME //
    @GET("home/plan/waiting") // :: 홈 화면 대기 중인 약속 조회
    fun homeoncallshow(
        @Header("Authorization") authorization : String?
    ) : Call<HomeOncall>

    @GET("home/plan/month") // :: 홈 화면 특정 달의 확정된 약속 날짜 조회
    fun homepromiseshow(
        @Header("Authorization") authorization : String?,
        @Query("searchDate") searchDate : String
    ) : Call<HomePlanShow>

    @GET("home/plan/day") // :: 홈 화면 특정 날짜의 확정된 약속 조회
    fun homepromiseinfo(
        @Header("Authorization") authorization : String?,
        @Query("searchDate") searchDate : String
    ) : Call<HomePlanInfo>

    // TEAM //
    @GET("team") // :: 모임 리스트 조회
    fun getGroup(
        @Header("Authorization") authorization: String
    ): Call<ResponseGetGroup>

    @GET("team/{teamId}/members") // :: 모임 구성원 조회
    fun getGroupMembers(
        @Header("Authorization") authorization: String,
        @Path("teamId") groupId: Int
    ): Call<ResponseGetGroupMembers>

    @GET("team/{teamID}") // :: 모임 상세 조회
    fun getGroupDetail(
        @Header("Authorization") authorization: String,
        @Path("teamId") groupId: Int
    ): Call<ResponseGetGroupDetail>

    @DELETE("team/{teamId}") // :: 모임 삭제
    fun DeleteGroup(
        @Header("Authorization") authorization: String,
        @Path("teamID") teamId: Int
    ) : Call<EditUserName>

    @POST("team/join") // :: 모임 참여
    fun enrollGroup(
        @Header("Authorization") authorization: String,
        @Body inviteCode: String
    ): Call<ResponseEnrollGroup>

    @Multipart
//    @Headers("Content-Type: application/json")
    @POST("team") // :: 모임 생성
    fun createGroup(
        @Header("Authorization") authorization: String,
        @Part file: MultipartBody.Part,
        @Part("request") request: RequestBody
    ): Call<ResponseCreateGroup>

    @POST("team/leave") // :: 모임 탈퇴
    fun LeaveGroup(
        @Header("Authorization") authorization: String,
        @Body teamId: Int
    ) : Call<EditUserName>

    @Multipart
//    @Headers("Content-Type: application/json")
    @POST("team/update") // :: 모임 수정
    fun updateGroup(
        @Header("Authorization") authorization: String,
        @Part file: MultipartBody.Part,
        @Part("request") request: RequestBody
    ): Call<ResponseUpdateGroup>

    @POST("team/code") // :: 초대 코드 재발급
    fun getGroupCode(
        @Header("Authorization") authorization: String,
        @Body teamId: Int
    ): Call<ResponseGroupCode>

   


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









    @GET("team/bookmark")
    fun getBookmarkGroup(
        @Header("Authorization") authorization: String
    ): Call<ResponseGetGroup>





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

    @POST("plan")  // 이거부터 다시
    fun MakePlan(
        @Header("Authorization") authorization: String,
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