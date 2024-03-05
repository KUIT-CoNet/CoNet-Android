package com.kuit.conet.data.dto.response.home

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetWaitingPlan(
    @SerialName("result")
    val result: ResultGetWaitingPlan,
) {
    @Serializable
    data class ResultGetWaitingPlan(
        @SerialName("count")
        val count: Int,
        @SerialName("plans")
        val plans: List<Plan>,
    ) {
        @Serializable
        data class Plan(
            @SerialName("planId")
            val planId: Long,
            @SerialName("startDate")
            val startDate: String,          // 2024-01-07 형식
            @SerialName("endDate")
            val endDate: String,            // 2024-01-07 형식
            @SerialName("teamName")
            val teamName: String,
            @SerialName("planName")
            val planName: String,
        )
    }
}