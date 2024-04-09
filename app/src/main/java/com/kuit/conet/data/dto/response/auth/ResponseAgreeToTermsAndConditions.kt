package com.kuit.conet.data.dto.response.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseAgreeToTermsAndConditions(
    @SerialName("result")
    val result: ResultAgreeToTermsAndConditions,
) {
    @Serializable
    data class ResultAgreeToTermsAndConditions(
        @SerialName("name")
        val name: String,
        @SerialName("email")
        val email: String,
        @SerialName("serviceTerm")
        val serviceTerm: Boolean,
    )
}