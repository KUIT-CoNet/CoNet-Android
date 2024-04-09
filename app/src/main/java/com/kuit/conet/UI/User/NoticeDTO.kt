package com.kuit.conet.UI.User

import java.io.Serializable

data class NoticeDTO(
    var id : Int,
    var title : String,
    var content: String,
    var date: String,
) : Serializable