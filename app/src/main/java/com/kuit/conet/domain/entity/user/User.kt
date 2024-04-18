package com.kuit.conet.domain.entity.user

import java.io.Serializable

data class User(
    var name: String,
    var imgUrl: String,
    val emails: List<Email>,
) : Serializable {
    data class Email(
        val email: String,
        val platform: String,       // TODO 나중에 enum, sealed class로 변경
    ) : Serializable
}
