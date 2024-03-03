package com.kuit.conet.data.dto.response.member

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponsePostBookmark(
    @SerialName("result")
    val result: String,
)