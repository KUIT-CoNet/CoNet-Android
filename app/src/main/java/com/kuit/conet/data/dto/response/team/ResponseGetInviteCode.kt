package com.kuit.conet.data.dto.response.team

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetInviteCode(
    @SerialName("result")
    val result: ResultGetInviteCode,
) {

    @Serializable
    data class ResultGetInviteCode(
        @SerialName("teamId")
        val groupId: Long,
        @SerialName("inviteCode")
        val inviteCode: String,
        @SerialName("codeDeadLine")
        val codeDeadLine: String,
    )
}
