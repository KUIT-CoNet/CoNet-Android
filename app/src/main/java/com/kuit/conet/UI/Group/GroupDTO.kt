package com.kuit.conet.UI.Group

import java.io.Serializable

data class GroupData(
    val groupId: Int,
    val groupName: String,
    val groupUrl: String,
    val groupMemberCount: Int,
    val favoriteTag: Boolean
): Serializable