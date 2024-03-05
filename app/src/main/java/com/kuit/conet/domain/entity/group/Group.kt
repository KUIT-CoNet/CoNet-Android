package com.kuit.conet.domain.entity.group

data class Group(
    val id: Long,
    val name: String,
    val imageUrl: String,
    val memberCount: Int,
    var isFavorite: Boolean,
)
