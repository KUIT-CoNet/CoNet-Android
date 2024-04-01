package com.kuit.conet.domain.entity.member

import java.io.Serializable

data class Member(
    val id: Long,
    val name: String,
    val imageUrl: String,
    var inPlan: Boolean = false,
) : Serializable
