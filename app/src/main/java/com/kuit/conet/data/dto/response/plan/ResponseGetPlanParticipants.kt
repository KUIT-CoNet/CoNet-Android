package com.kuit.conet.data.dto.response.plan

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetPlanParticipants(
    @SerialName("result")
    val result: List<Member>,
) {
    @Serializable
    data class Member(
        @SerialName("memberId")
        val memberId: Long,
        @SerialName("name")
        val name: String?,
        @SerialName("memberImgUrl")
        val memberImgUrl: String,
        @SerialName("inPlan")
        val inPlan: Boolean,
    ) {
        fun asMember(): com.kuit.conet.domain.entity.member.Member {
            return com.kuit.conet.domain.entity.member.Member(
                id = memberId,
                name = name ?: "",
                imageUrl = memberImgUrl,
                inPlan = inPlan,
            )

        }
    }
}
