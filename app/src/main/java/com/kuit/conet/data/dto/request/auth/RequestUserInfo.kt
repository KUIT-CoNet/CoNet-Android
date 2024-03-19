package com.kuit.conet.data.dto.request.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestAgreeToTermsAndConditions(
    @SerialName("name")
    val name: String,
)