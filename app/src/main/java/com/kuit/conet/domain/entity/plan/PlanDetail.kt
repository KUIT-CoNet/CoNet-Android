package com.kuit.conet.domain.entity.plan

import com.kuit.conet.domain.entity.member.Member
import java.io.Serializable

data class PlanDetail(
    val id: Long,
    val planName: String,
    val date: String,
    val time: String,
    val memberCount: Int,
    val members: List<Member>
) : Serializable