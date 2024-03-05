package com.kuit.conet.domain.entity.plan

data class DecidedPlan(
    val planId: Long,
    val planName: String,
    val groupName: String = "",
    val time: String,
)