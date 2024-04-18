package com.kuit.conet.data.dto.request.member

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestEditUserName(
    @SerialName("name")
    val name: String,
)
