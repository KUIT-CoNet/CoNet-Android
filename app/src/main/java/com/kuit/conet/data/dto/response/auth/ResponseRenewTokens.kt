package com.kuit.conet.data.dto.response.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseRenewTokens(
    @SerialName("result")
    val result: ResultRenewTokens,
) {
    @Serializable
    data class ResultRenewTokens(
        @SerialName("email")
        val email: String,
        @SerialName("accessToken")
        val accessToken: String,
        @SerialName("refreshToken")
        val refreshToken: String,
        @SerialName("isRegistered")
        val isRegistered: Boolean,
    )
}

