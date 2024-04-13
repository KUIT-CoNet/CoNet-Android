package com.kuit.conet.domain.entity.user

data class User(
    val name: String,
    val imgUrl: String,
    val emails: List<Email>,
) {
    data class Email(
        val email: String,
        val platform: String,       // TODO 나중에 enum, sealed class로 변경
    )
}
