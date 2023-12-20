package com.kuit.conet.Utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

//날짜 형식 바꾸는 함수를 공용으로 쓸 수 있게끔....
fun DateFormatChanger(option : Int, date : String) : String{
    if(option == 1){ //yyyy년 M월 d일 형식을 yyyy. MM. dd로  바꾸는 메소드
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일", Locale.KOREAN)
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy. MM. dd", Locale.KOREAN)
        val inputdate = LocalDate.parse(date, inputFormatter)
        val outputdate = inputdate.format(outputFormatter)
        return outputdate
    }
    return ""
}