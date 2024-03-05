package com.kuit.conet.data.dto.response.home

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetDailyPlan(
    @SerializedName("result")
    val result: ResultGetDailyPlan,
) {
    @Serializable
    data class ResultGetDailyPlan(
        @SerializedName("count")
        val count: Int,
        @SerializedName("plans")
        val plans: ArrayList<Plan>,
    ) {
        @Serializable
        data class Plan(
            @SerializedName("planId")
            val planId: Long,
            @SerializedName("time")
            val time: String,           // 03:00:00 형식
            @SerializedName("teamName")
            val teamName: String,
            @SerializedName("planName")
            val planName: String,
        )
    }
}
