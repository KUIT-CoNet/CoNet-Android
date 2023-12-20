package com.kuit.conet.Utils

import com.kuit.conet.Network.RetrofitClient
import okhttp3.HttpUrl
import okhttp3.ResponseBody

object NetworkUtil {
    fun getErrorResponse(errorBody: ResponseBody?): ErrorResponse? {
        return RetrofitClient.retrofit.responseBodyConverter<ErrorResponse>(
            ErrorResponse::class.java,
            ErrorResponse::class.java.annotations
        ).convert(errorBody)
    }
}

data class ErrorResponse(
    val protocol: String,
    val code: Int,
    val message: String,
    val url: HttpUrl
)