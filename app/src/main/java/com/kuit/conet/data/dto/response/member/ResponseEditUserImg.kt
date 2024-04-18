package com.kuit.conet.data.dto.response.member

import com.google.gson.annotations.SerializedName
import com.kuit.conet.domain.entity.user.UserSimple
import kotlinx.serialization.Serializable

@Serializable
data class ResponseEditUserImg(
    @SerializedName("result")
    val result: UserImage,
) {
    @Serializable
    data class UserImage(
        @SerializedName("name")
        val name: String,
        @SerializedName("imgUrl")
        val imgUrl: String,
    ) {
        fun asUserSimple(): UserSimple {
            return UserSimple(
                name = name,
                imgUrl = imgUrl,
            )
        }
    }
}