package com.kuit.conet.data.dto.response.team

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseUpdateGroup(
    @SerialName("result")
    val result: ResultUpdateGroup
) {
    @Serializable
    data class ResultUpdateGroup(
        @SerialName("name")
        val groupName: String,
        @SerialName("imgUrl")
        val imageUrl: String,
    )
}