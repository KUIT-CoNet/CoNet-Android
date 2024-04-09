package com.kuit.conet.data.dto.response.team

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseCreateGroup(
    @SerialName("result")
    val result: ResultCreateGroup,
) {
    @Serializable
    data class ResultCreateGroup(
        @SerialName("teamId")
        val groupId: Long,
        @SerialName("inviteCode")
        val enrollCode: String,
    )
}