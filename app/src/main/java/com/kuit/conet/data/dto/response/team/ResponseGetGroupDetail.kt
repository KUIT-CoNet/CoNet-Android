package com.kuit.conet.data.dto.response.team

import com.kuit.conet.domain.entity.group.Group
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetGroupDetail(
    @SerialName("result")
    val result: ResultGetGroupDetail,
) {
    @Serializable
    data class ResultGetGroupDetail(
        @SerialName("teamId")
        val groupId: Long,
        @SerialName("teamName")
        val groupName: String,
        @SerialName("teamImgUrl")
        val groupImgUrl: String,
        @SerialName("teamMemberCount")
        val groupMemberCount: Int,
        @SerialName("isNew")
        val isNew: Boolean,
        @SerialName("bookmark")
        val bookmark: Boolean,
    ) {
        fun asGroup(): Group {
            return Group(
                id = groupId,
                name = groupName,
                imageUrl = groupImgUrl,
                memberCount = groupMemberCount,
                isFavorite = bookmark,
            )
        }
    }
}


