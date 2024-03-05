package com.kuit.conet.data.dto.response.plan

import com.kuit.conet.domain.entity.plan.DecidedPlan
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetGroupDailyDecidedPlan(
    @SerialName("result")
    val result: ResultGetGroupDailyDecidedPlan,
) {
    @Serializable
    data class ResultGetGroupDailyDecidedPlan(
        @SerialName("count")
        val count: Int,
        @SerialName("plans")
        val plans: List<Plan>,
    ) {
        @Serializable
        data class Plan(
            @SerialName("planId")
            val planId: Long,
            @SerialName("planName")
            val planName: String,
            @SerialName("time")
            val time: String,           // 03:00:00 형식
        ) {
            fun asDecidedPlan(): DecidedPlan {
                return DecidedPlan(
                    planId = planId,
                    planName = planName,
                    time = time,
                )
            }
        }
    }
}