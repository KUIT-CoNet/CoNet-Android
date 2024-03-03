package com.kuit.conet.Network

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
    @POST("auth/login") // :: 로그인(회원가입) //
    fun signUp(
        @Body
        login: Login
    ): Call<KaKaoResponse>

    @POST("auth/term") // :: 약관 동의 및 이름 입력 //
    fun registed(
        @Header("Authorization")
        authorization: String,
        @Body
        sendInfo: sendInfo
    ): Call<RegistedResponse>

    @POST("auth/regenerate-token") // :: refresh token 재발급
    fun getAccess(
        @Header("Authorization")
        refreshToken: String,
    ): Call<RefreshResponse>

    // HOME //
    @GET("home/plan/waiting") // :: 홈 화면 대기 중인 약속 조회
    fun homeoncallshow(
        @Header("Authorization")
        authorization: String,
    ): Call<HomeOncall>

    @GET("home/plan/month") // :: 홈 화면 특정 달의 확정된 약속 날짜 조회
    fun homepromiseshow(
        @Header("Authorization")
        authorization: String,
        @Query("searchDate")
        searchDate: String,         //2024-02 형식
    ): Call<HomePlanShow>

    @GET("home/plan/day") // :: 홈 화면 특정 날짜의 확정된 약속 조회
    fun homepromiseinfo(
        @Header("Authorization")
        authorization: String,
        @Query("searchDate")
        searchDate: String,
    ): Call<HomePlanInfo>


    // TEAM //
    @GET("team") // :: 모임 리스트 조회
    fun getGroup(
        @Header("Authorization")
        authorization: String
    ): Call<ResponseGetGroup>

    @GET("team/{teamId}/members") // :: 모임 구성원 조회
    fun getGroupMembers(
        @Header("Authorization")
        authorization: String,
        @Path("teamId")
        groupId: Int,
    ): Call<ResponseGetGroupMembers>

    @GET("team/{teamId}") // :: 모임 상세 조회
    fun getGroupDetail(
        @Header("Authorization")
        authorization: String,
        @Path("teamId")
        groupId: Int,
    ): Call<ResponseGetGroupDetail>

    @DELETE("team/{teamId}") // :: 모임 삭제
    fun DeleteGroup(
        @Header("Authorization")
        authorization: String,
        @Path("teamId")
        teamId: Int,
    ): Call<EditUserName>

    @POST("team/join") // :: 모임 참여
    fun enrollGroup(
        @Header("Authorization")
        authorization: String,
        @Body
        inviteCode: String,
    ): Call<ResponseEnrollGroup>

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
    fun LeaveGroup(
        @Header("Authorization")
        authorization: String,
        @Body
        teamId: Int,
    ): Call<EditUserName>

    @Multipart
    @POST("team/update") // :: 모임 수정
    fun updateGroup(
        @Header("Authorization")
        authorization: String,
        @Part
        file: MultipartBody.Part,
        @Part("request")
        request: RequestBody,
    ): Call<ResponseUpdateGroup>

    @POST("team/code") // :: 초대 코드 재발급
    fun getGroupCode(
        @Header("Authorization")
        authorization: String,
        @Body
        teamId: Int,
    ): Call<ResponseGroupCode>


    // PLAN //
    @POST("plan")  // :: 약속 생성 //
    fun makePlan(
        @Header("Authorization")
        authorization: String,
        @Body
        makePlanInfo: MakePlanInfo,
    ): Call<ResponseMakePlan>

    @POST("plan/update/waiting") // :: 약속 수정 - 대기 중 //
    fun UpdateWaiting(
        @Header("Authorization")
        authorization: String,
        @Body
        updateWaiting: UpdateWaiting,
    ): Call<ResponseUpdateWaiting>

    @POST("plan/available-time-slot") // :: 나의 가능한 시간 저장 //
    fun InputMyTime(
        @Header("Authorization")
        authorization: String,
        @Body
        inputMyTime: InputMyTime,
    ): Call<ResponseInputMyTime>

    @POST("plan/fix") // :: 약속 확정 //
    fun FixPlan(
        @Header("Authorization")
        authorization: String,
        @Body
        fixPlan: FixPlan,
    ): Call<ResponseFixPlan>

    // :: 약속 수정 - 확정
    @POST("plan/update/fixed")
    fun updatePlanDetail(
        @Header("Authorization")
        authorization: String,
        @Body
        planDetail: PlanDetail,
    ): Call<ResponseUpdatePlanDetail>

    @DELETE("plan/{planId}") // :: 약속 삭제 //
    fun deletePlan(
        @Header("Authorization")
        authorization: String,
        @Path("planId")
        planId: Int,
    ): Call<ResponseDeletePlan>

    @GET("plan/{planId}") // :: 약속 상세 정보 조회 //
    fun getPlanDetail(
        @Header("Authorization")
        authorization: String,
        @Path("planId")
        planId: Int,
    ): Call<ResponseGetPlanDetail>

    //이거 뭐야 왜이래? 못찾겠다;;;
    @GET("plan/day") // :: 모임 내 특정 날짜의 확정된 약속 조회
    fun ShowGroupConfirmPlan(
        @Query("teamId")
        teamId: Int,
        @Query("searchDate")
        searchDate: String,      // 2024-02-09 형식
    ): Call<HomePlanInfo>

    @GET("plan/month") // :: 모임 내 특정 달의 확정된 약속이 존재하는 날짜 조회
    fun ShowGroupMonth(
        @Query("teamId")
        teamId: Int,
        @Query("searchDate")
        searchDate: String,      // 2024-02 형식
    ): Call<HomePlanShow>

    @GET("plan/waiting") // :: [사이드바] 모임 내 대기 중인 약속 조회
    fun ShowGroupOncall(
        @Query("teamId")
        teamId: Int,
    ): Call<HomeOncall>

    @GET("plan/fixed") // :: [사이드바] 모임 내 확정된 지난/다가오는 약속 조회
    fun showSideBar(
        @Header("Authorization")
        authorization: String,
        @Query("teamId")
        teamId: Int,
        @Query("period")
        period: String, //지난 약속 : past, 다가오는 : oncoming (대소문자 구분 필요 x)
    ): Call<ResponseSideBarPlan>

    @GET("plan/{planId}/available-time-slot/my") // :: 특정 약속의 나의 가능한 시간 조회 //
    fun ShowMyTime(
        @Header("Authorization")
        authorization: String,
        @Query("planId")
        planId: Int,
    ): Call<ShowMyTime>

    @GET("plan/{planId}/available-time-slot")  // :: 대기중인 약속의 구성원, 가능한 시간 조회 //
    fun ShowMemTime(
        @Query("planId")
        planId: Int,
    ): Call<ShowMemTime>


    // MEMBER //
    @GET("member") // :: 사용자 조회 //
    fun showuser(
        @Header("Authorization")
        authorization: String,
    ): Call<ShowUser>

    @GET("member/bookmark") // :: 북마크 된 모임 조회
    fun getBookmarkGroup(
        @Header("Authorization")
        authorization: String,
    ): Call<ResponseGetGroup>

    @POST("member/bookmark") // :: 북마크 추가/삭제
    fun checkBookmark(
        @Header("Authorization")
        authorization: String,
        @Body
        teamId: Int,
    ): Call<ResponseBookmark>

    @POST("member/name") // :: 사용자 이름 수정 //
    fun editName(
        @Header("Authorization")
        authorization: String,
        @Body
        name: String,
    ): Call<EditUserName>

    @Multipart
    @POST("member/image") // ::  사용자 이미지 수정 //
    fun editImage(
        @Header("Authorization")
        authorization: String,
        @Part
        image: MultipartBody.Part,
    ): Call<EditUserImage>

    @DELETE("user") // :: 회원 탈퇴 //
    fun deleteUser(
        @Header("Authorization")
        authorization: String,
    ): Call<DeleteUser>


    // NOTICE //
    @GET("notice") // :: 공지 조회 //
    fun getNotice(
        @Header("Authorization")
        authorization: String,
    ): Call<Notice>

}