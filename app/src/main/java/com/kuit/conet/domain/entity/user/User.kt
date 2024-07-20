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

    companion object {
        val EMPTY_USER = User(
            name = "알 수 없음",
            imgUrl = "https://cdn.pixabay.com/photo/2024/01/02/13/28/gecko-8483282_1280.png",
            emails = emptyList(),
        )
    }
}
