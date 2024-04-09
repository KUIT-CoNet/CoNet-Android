package com.kuit.conet.data.dto.response.plan

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseUpdateFixedPlan(
    @SerialName("result")
    val result: String,
)