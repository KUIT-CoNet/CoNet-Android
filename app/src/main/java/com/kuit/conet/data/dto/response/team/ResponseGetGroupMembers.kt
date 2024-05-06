package com.kuit.conet.data.dto.response.team

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetGroupMembers(
    @SerialName("result")
    val result: List<Member>,
) {
    @Serializable
    data class Member(
        @SerialName("memberId")
        val userId: Long,
        @SerialName("name")
        val name: String?,
        @SerialName("memberImgUrl")
        val userImgUrl: String,
    ) {
        fun asMember(): com.kuit.conet.domain.entity.member.Member {
            return if (name == null) {
                com.kuit.conet.domain.entity.member.Member(
                    userId,
                    "이름 없음",
                    userImgUrl,
                )
            } else {
                com.kuit.conet.domain.entity.member.Member(
                    userId,
                    name,
                    userImgUrl,
                )
            }

            /*return com.kuit.conet.domain.entity.member.Member(
                id = userId,
                name = name,
                imageUrl = userImgUrl,
            )*/
        }
    }
}
