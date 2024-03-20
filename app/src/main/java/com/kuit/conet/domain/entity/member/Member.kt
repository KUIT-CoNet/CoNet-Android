package com.kuit.conet.domain.entity.member

import com.kuit.conet.Network.Members

data class Member(
    val id: Long,
    val name: String,
    val imageUrl: String,
) {
    //기존 코드를 위해 임시적으로 넣은 코드
    fun asMembers(): Members {
        return Members(
            id = id.toInt(),
            name = name,
            image = imageUrl,
        )
    }
}
