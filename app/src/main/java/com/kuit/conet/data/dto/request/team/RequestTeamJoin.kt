package com.kuit.conet.data.dto.request.team

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestTeamJoin(
    @SerialName("inviteCode")
    val inviteCode: String,
)
