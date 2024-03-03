package com.kuit.conet.data.dto.request.member

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestPostBookmark(
    @SerialName("teamId")
    val teamId: Int,
)