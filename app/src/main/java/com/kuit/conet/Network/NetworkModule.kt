package com.kuit.conet.Network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.kuit.conet.data.api.MemberAPI
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://conet.store/"

object RetrofitClient {
    val retrofit = getRetrofit()
    val instance = retrofit.create(RetrofitInterface::class.java)

    private val jsonRetrofit = getJsonRetrofit()
    val jsonInstance = jsonRetrofit.create(RetrofitInterface::class.java)
    val memberInstance =
        requireNotNull(jsonRetrofit.create(MemberAPI::class.java)) { "NetworkModule's MemberAPI is null" }
}

fun okHttpClient(): OkHttpClient {
    val builder = OkHttpClient.Builder()
    val logging = HttpLoggingInterceptor()
    logging.level = HttpLoggingInterceptor.Level.BODY
    return builder.addInterceptor(logging).build()
}

fun getRetrofit(): Retrofit {
    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient())
        .build()

    return retrofit
}

fun getJsonRetrofit(): Retrofit {
    return Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .client(okHttpClient())
        .build()
}
