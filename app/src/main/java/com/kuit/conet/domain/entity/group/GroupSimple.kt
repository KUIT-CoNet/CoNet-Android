package com.kuit.conet.domain.entity.group

data class GroupSimple(
    val id: Long,
    val name: String,
    val imageUrl: String,
    var isFavorite: Boolean,
)
