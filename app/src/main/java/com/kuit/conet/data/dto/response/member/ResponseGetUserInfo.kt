package com.kuit.conet.data.dto.response.member

import com.kuit.conet.domain.entity.user.User
import com.kuit.conet.domain.entity.user.UserSimple
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetUserInfo(
    @SerialName("result")
    val result: ResultGetUserInfo,
) {
    @Serializable
    data class ResultGetUserInfo(
        @SerialName("name")
        val name: String,
        @SerialName("email")
        val email: String,
        @SerialName("memberImgUrl")
        val memberImgUrl: String,
        @SerialName("platform")
        val platform: String,
    ) {
        fun asUserSimple(): UserSimple {
            return UserSimple(
                name = name,
                imgUrl = memberImgUrl,
            )
        }

        fun asUser(): User {    // TODO 현재 email 갯수가 하나이지만 나중에 여러개를 받을 수 있는 공간으로 확장 필요
            return User(
                name = name,
                imgUrl = memberImgUrl,
                emails = listOf(
                    User.Email(
                        email = email,
                        platform = platform,
                    ),
                ),
            )
        }
    }
}