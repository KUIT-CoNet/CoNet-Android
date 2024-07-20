package com.kuit.conet.Utils.calendar

import com.prolificinteractive.materialcalendarview.CalendarDay
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale

fun CalendarDay.toLocalDate(): LocalDate {
    return LocalDate.of(year, month + 1, day)
}

fun CalendarDay.compareWithLocalDate(date: LocalDate): Boolean {
    /*LocalDate
    *   - year: 년
    *   - monthValue: 월(1~12) / month: 월(Month! - enum class)
    *   - dayOfMonth: 일
    * CalendarDay
    *   - year: 년
    *   - month: 월(0~11)
    *   - day: 일
    * */
    return year == date.year && month + 1 == date.monthValue && day == date.dayOfMonth
}

// CalendarDay와 LocalDate를 비교하여 결과를 반환하는 함수
/*fun CalendarDay.compareWithLocalDate(date: LocalDate): RESULT_COMPARE_LOCALDATE {
    if (year != date.year) {
        return RESULT_COMPARE_LOCALDATE.DIFFERENT_YEAR
    } else if (month + 1 != date.monthValue) {
        return RESULT_COMPARE_LOCALDATE.DIFFERENT_MONTH
    } else if (day != date.dayOfMonth) {
        return RESULT_COMPARE_LOCALDATE.DIFFERENT_DAY
    } else {
        return RESULT_COMPARE_LOCALDATE.SAME
    }
}

enum class RESULT_COMPARE_LOCALDATE {
    SAME, DIFFERENT_YEAR, DIFFERENT_MONTH, DIFFERENT_DAY
}*/

fun CalendarDay.toString(isYear: Boolean): String {
    return if (isYear) {
        SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault()).format(date)
    } else {
        SimpleDateFormat("MM월 dd일", Locale.getDefault()).format(date)
    }
}