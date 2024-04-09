package com.kuit.conet.domain.entity.plan

data class DecidedPlan(
    val planId: Long,
    val planName: String,
    val groupName: String = "",
    val date: String = "",
    val time: String,
    val participant: Boolean = false,
    val dday: Int = 0,
)