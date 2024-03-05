package com.kuit.conet.Utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity

fun saveUserAccessToken(context : Context, access:String?){
    val sdf = context.getSharedPreferences("tokensave", AppCompatActivity.MODE_PRIVATE) // 보안 위해 모드 프라이빗으로 선언
    val editer = sdf.edit()

    editer.putString("accesstoken", access)
    editer.apply()
}

fun getAccessToken(context: Context) : String{
    val sdf = context.getSharedPreferences("tokensave", AppCompatActivity.MODE_PRIVATE)

    return sdf.getString("accesstoken", "null")!!
}

//리프레쉬 토큰 저장, 불러오기
fun saveUserRefreshToken(context : Context, refresh:String?){
    val sdf = context.getSharedPreferences("tokensave", AppCompatActivity.MODE_PRIVATE) // 보안 위해 모드 프라이빗으로 선언
    val editer = sdf.edit()

    editer.putString("refreshtoken", refresh)
    editer.apply()
}

fun getRefreshToken(context: Context) : String{
    val sdf = context.getSharedPreferences("tokensave", AppCompatActivity.MODE_PRIVATE)

    return sdf.getString("refreshtoken", "null")!!
}

// 선택 동의여부 저장, 불러오기
fun saveIsoption(context : Context, isoption:Int){
    val sdf = context.getSharedPreferences("userinfo", AppCompatActivity.MODE_PRIVATE) // 보안 위해 모드 프라이빗으로 선언
    val editer = sdf.edit()

    editer.putInt("isoption", isoption)
    editer.apply()
}

fun getIsoption(context: Context) : Int{
    val sdf = context.getSharedPreferences("userinfo", AppCompatActivity.MODE_PRIVATE)

    return sdf.getInt("isoption", 0)
}

//이름 저장, 불러오기
fun saveUsername(context : Context, username:String?){
    val sdf = context.getSharedPreferences("userinfo", AppCompatActivity.MODE_PRIVATE) // 보안 위해 모드 프라이빗으로 선언
    val editer = sdf.edit()

    editer.putString("username", username)
    editer.apply()
}

//오류떠서 수정함8/10 10:16
fun getUsername(context: Context) : String{
    val sdf = context.getSharedPreferences("userinfo", AppCompatActivity.MODE_PRIVATE)

    return sdf.getString("username", "null")!!
}

// 유저 이미지 저장, 불러오기
fun saveUserImgUrl(context: Context, userImgUrl : String?){
    val sdf = context.getSharedPreferences("userImageUrl", AppCompatActivity.MODE_PRIVATE) // 보안 위해 모드 프라이빗으로 선언
    val editer = sdf.edit()

    editer.putString("userImageUrl", userImgUrl)
    editer.apply()
}

fun getUserImg(context: Context) : String{
    val sdf = context.getSharedPreferences("userImageUrl", AppCompatActivity.MODE_PRIVATE)

    return sdf.getString("userImageUrl", "null")!!
}

// 유저 이메일 저장, 불러오기

fun saveUserEmail(context: Context, userEmail : String?){
    val sdf = context.getSharedPreferences("userEmail", AppCompatActivity.MODE_PRIVATE) // 보안 위해 모드 프라이빗으로 선언
    val editer = sdf.edit()

    editer.putString("userEmail", userEmail)
    editer.apply()
}

fun getUserEmail(context: Context) : String{
    val sdf = context.getSharedPreferences("userEmail", AppCompatActivity.MODE_PRIVATE)

    return sdf.getString("userEmail", "null")!!
}

