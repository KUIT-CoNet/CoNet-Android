package com.kuit.conet.domain.entity.plan

data class UndecidedPlan(
    val planId: Long,
    val planName: String,
    val groupName: String,
    val startDate: String,
    val endDate: String,
) {
    companion object {
        fun emptyList() = kotlin.collections.emptyList<UndecidedPlan>()
    }
}
