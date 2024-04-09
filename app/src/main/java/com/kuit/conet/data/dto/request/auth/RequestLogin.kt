package com.kuit.conet.data.dto.request.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestLogin(
    @SerialName("platform")
    var platform: String,
    @SerialName("idToken")
    var idToken: String,
)