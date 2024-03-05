package com.kuit.conet.data.dto.response.plan

import com.google.gson.annotations.SerializedName
import com.kuit.conet.domain.entity.plan.UndecidedPlan
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetGroupWaitingPlan(
    @SerializedName("result")
    val result: ResultGetGroupWaitingPlan,
) {
    @Serializable
    data class ResultGetGroupWaitingPlan(
        @SerializedName("count")
        val count: Int,
        @SerializedName("plans")
        val plans: List<Plan>,
    ) {
        @Serializable
        data class Plan(
            @SerializedName("planId")
            val planId: Long,
            @SerializedName("startDate")
            val startDate: String,          // 2024-01-07 형식
            @SerializedName("endDate")
            val endDate: String,            // 2024-01-07 형식
            @SerializedName("teamName")
            val teamName: String,
            @SerializedName("planName")
            val planName: String,
        ) {
            fun asUndecidedPlan(): UndecidedPlan {
                return UndecidedPlan(
                    planId = planId,
                    planName = planName,
                    groupName = teamName,
                    startDate = startDate,
                    endDate = endDate,
                )
            }
        }
    }
}