package com.kuit.conet.data.dto.request.plan

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestUpdateFixedPlan(
    @SerialName("planId")
    val planId: Long,
    @SerialName("planName")
    val planName: String,
    @SerialName("date")
    val date: String,        //2024-02-09 형식
    @SerialName("time")
    val time: String,        // 19:00 형식
    @SerialName("memberIds")
    val membersIds: List<Int>,
)