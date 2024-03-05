package com.kuit.conet.data.dto.response.team

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseTeamJoin(
    @SerialName("result")
    val result: ResultTeamJoin,
) {
    @Serializable
    data class ResultTeamJoin(
        @SerialName("memberName")
        val memberName: String,
        @SerialName("teamName")
        val groupName: String,
    )
}

