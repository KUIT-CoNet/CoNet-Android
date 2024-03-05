package com.kuit.conet.data.dto.response.team

import com.kuit.conet.domain.entity.group.GroupSimple
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetGroups(
    @SerialName("result")
    val result: List<ResultGetGroup>,
) {
    @Serializable
    data class ResultGetGroup(
        @SerialName("teamId")
        val groupId: Long,
        @SerialName("teamName")
        var groupName: String,
        @SerialName("teamImgUrl")
        var groupUrl: String,
        @SerialName("teamMemberCount")
        val groupMemberCount: Int,
        @SerialName("isNew")
        val newTag: Boolean,
        @SerialName("bookmark")
        var favoriteTag: Boolean,
    ) {
        fun asGroupSimple(): GroupSimple {
            return GroupSimple(
                id = groupId,
                name = groupName,
                imageUrl = groupUrl,
                isFavorite = favoriteTag,
            )
        }
    }
}