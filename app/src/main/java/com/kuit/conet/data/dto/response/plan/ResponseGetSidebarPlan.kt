package com.kuit.conet.data.dto.response.plan

import com.kuit.conet.domain.entity.plan.DecidedPlan
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetSidebarPlan(
    @SerialName("result")
    val result: ResultGetSidebarPlan,
) {
    @Serializable
    data class ResultGetSidebarPlan(
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
            @SerialName("date")
            val date: String,
            @SerialName("time")
            val time: String,
            @SerialName("participant")
            val participant: Boolean,
            @SerialName("dday")
            val dday: Int,
        ) {
            fun asDecidedPlan(): DecidedPlan {
                return DecidedPlan(
                    planId = planId,
                    planName = planName,
                    date = date,
                    time = time,
                    participant = participant,
                    dday = dday,
                )
            }
        }
    }
}