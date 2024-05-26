package com.kuit.conet.Utils.calendar

import java.util.Calendar
import java.util.Locale

// 오늘의 날짜 가져오기
fun getToday(): Calendar {
    // com.prolificinteractive.materialcalendarview 의 CalendarDay.today() 와 같은 기능
    return Calendar.getInstance(Locale.getDefault())
}