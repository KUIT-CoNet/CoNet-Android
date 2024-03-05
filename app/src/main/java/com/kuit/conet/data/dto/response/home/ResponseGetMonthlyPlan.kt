package com.kuit.conet.data.dto.response.home

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetMonthlyPlan(
    @SerializedName("result")
    val result: ResultGetMonthlyPlan,
) {
    @Serializable
    data class ResultGetMonthlyPlan(
        @SerializedName("count")
        val count: Int,
        @SerializedName("dates")
        val dates: List<Int>,
    )
}
