package com.kuit.conet.data.api

import com.kuit.conet.data.dto.request.plan.RequestUpdateFixedPlan
import com.kuit.conet.data.dto.response.plan.ResponseGetGroupDailyDecidedPlan
import com.kuit.conet.data.dto.response.plan.ResponseGetGroupWaitingPlan
import com.kuit.conet.data.dto.response.plan.ResponseGetPlanDetail
import com.kuit.conet.data.dto.response.plan.ResponseGetSidebarPlan
import com.kuit.conet.data.dto.response.plan.ResponseUpdateFixedPlan
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PlanAPI {
    /*@POST("plan")  // :: 약속 생성
    fun makePlan(
        @Header("Authorization")
        authorization: String,
        @Body
        makePlanInfo: MakePlanInfo,
    ): Call<ResponseMakePlan>

    @POST("plan/update/waiting") // :: 약속 수정 - 대기 중
    fun UpdateWaiting(
        @Header("Authorization")
        authorization: String,
        @Body
        updateWaiting: UpdateWaiting,
    ): Call<ResponseUpdateWaiting>

    @POST("plan/available-time-slot") // :: 나의 가능한 시간 저장
    fun InputMyTime(
        @Header("Authorization")
        authorization: String,
        @Body
        inputMyTime: InputMyTime,
    ): Call<ResponseInputMyTime>

    @POST("plan/fix") // :: 약속 확정
    fun FixPlan(
        @Header("Authorization")
        authorization: String,
        @Body
        fixPlan: FixPlan,
    ): Call<ResponseFixPlan>*/

    // :: 약속 수정 - 확정
    @POST("plan/update/fixed")
    fun updateFixedPlan(
        @Header("Authorization")
        authorization: String,
        @Body
        planDetail: RequestUpdateFixedPlan,
    ): Call<ResponseUpdateFixedPlan>

    @DELETE("plan/{planId}") // :: 약속 삭제
    fun deletePlan(
        @Header("Authorization")
        authorization: String,
        @Path("planId")
        planId: Long,
    ): Call<ResponseDeletePlan>

    @GET("plan/{planId}") // :: 약속 상세 정보 조회
    fun getPlanDetail(
        @Header("Authorization")
        authorization: String,
        @Path("planId")
        planId: Long,
    ): Call<ResponseGetPlanDetail>

    @GET("plan/day") // :: 모임 내 특정 날짜의 확정된 약속 조회
    fun getGroupDailyDecidedPlan(
        @Query("teamId")
        teamId: Long,
        @Query("searchDate")
        searchDate: String,      // 2024-02-09 형식 (2024. 02. 09)
    ): Call<ResponseGetGroupDailyDecidedPlan>

    /*@GET("plan/month") // :: 모임 내 특정 달의 확정된 약속이 존재하는 날짜 조회
    fun ShowGroupMonth(
        @Query("teamId")
        teamId: Int,
        @Query("searchDate")
        searchDate: String,      // 2024-02 형식
    ): Call<HomePlanShow>*/

    @GET("plan/waiting") // :: [사이드바] 모임 내 대기 중인 약속 조회
    fun getGroupWaitingPlan(
        @Query("teamId")
        teamId: Long,
    ): Call<ResponseGetGroupWaitingPlan>

    @GET("plan/fixed") // :: [사이드바] 모임 내 확정된 지난/다가오는 약속 조회
    fun getSidebarPlan(
        @Header("Authorization")
        authorization: String,
        @Query("teamId")
        teamId: Long,
        @Query("period")
        period: String, //지난 약속 : past, 다가오는 : oncoming (대소문자 구분 필요 x)
    ): Call<ResponseGetSidebarPlan>

    /*@GET("plan/{planId}/available-time-slot/my") // :: 특정 약속의 나의 가능한 시간 조회 //
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
    ): Call<ShowMemTime>*/
}