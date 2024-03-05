package com.kuit.conet.data.api

import com.kuit.conet.Network.HomeOncall
import com.kuit.conet.Network.HomePlanInfo
import com.kuit.conet.Network.HomePlanShow
import com.kuit.conet.data.dto.response.home.ResponseGetDailyPlan
import com.kuit.conet.data.dto.response.home.ResponseGetMonthlyPlan
import com.kuit.conet.data.dto.response.home.ResponseGetWaitingPlan
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface HomeAPI {
    @GET("home/plan/waiting") // :: 홈 화면 대기 중인 약속 조회
    fun getWaitingPlan(
        @Header("Authorization")
        authorization: String,
    ): Call<ResponseGetWaitingPlan>

    @GET("home/plan/month") // :: 홈 화면 특정 달의 확정된 약속 날짜 조회
    fun getMonthlyPlan(
        @Header("Authorization")
        authorization: String,
        @Query("searchDate")
        searchDate: String,         // 2024. 02 형 (2024-02)
    ): Call<ResponseGetMonthlyPlan>

    @GET("home/plan/day") // :: 홈 화면 특정 날짜의 확정된 약속 조회
    fun getDailyPlan(
        @Header("Authorization")
        authorization: String,
        @Query("searchDate")
        searchDate: String,        // 2023. 12. 14 형식 (2024-12-14)
    ): Call<ResponseGetDailyPlan>
}