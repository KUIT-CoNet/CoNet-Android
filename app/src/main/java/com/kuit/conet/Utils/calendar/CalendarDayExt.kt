package com.kuit.conet.Utils.calendar

import com.prolificinteractive.materialcalendarview.CalendarDay
import java.util.Calendar

fun CalendarDay.asCalendar(): Calendar {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, day)
    return calendar
}

fun Calendar.asCalendarDay(): CalendarDay {
    return CalendarDay.from(get(Calendar.YEAR), get(Calendar.MONTH), get(Calendar.DATE))
}