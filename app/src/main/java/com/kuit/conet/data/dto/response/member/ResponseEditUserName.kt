package com.kuit.conet.data.dto.response.member

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseEditUserName(
    @SerialName("result")
    val result: String,
)