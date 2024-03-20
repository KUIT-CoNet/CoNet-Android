package com.kuit.conet.data.dto.response.team

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetGroupMembers(
    @SerialName("result")
    val result: List<Member>,
) {
    data class Member(
        @SerialName("userId")
        val userId: Long,
        @SerialName("name")
        val name: String,
        @SerialName("userImgUrl")
        val userImgUrl: String,
    ) {
        fun asMember(): com.kuit.conet.domain.entity.member.Member {
            return com.kuit.conet.domain.entity.member.Member(
                id = userId,
                name = name,
                imageUrl = userImgUrl,
            )
        }
    }
}
