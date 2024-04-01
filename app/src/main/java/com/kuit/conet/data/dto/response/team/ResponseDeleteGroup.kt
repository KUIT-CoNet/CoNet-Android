package com.kuit.conet.data.dto.response.team

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseDeleteGroup(
    @SerialName("result")
    val result: String,
)
