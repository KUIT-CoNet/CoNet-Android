package com.kuit.conet.UI.Home.Calendar


import android.graphics.Color

import android.text.style.ForegroundColorSpan

import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import java.util.*

class OnedayDecorator: DayViewDecorator { // 오늘 날짜 보라색으로 설정
    private val calendar = Calendar.getInstance()
    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return day?.isInRange(CalendarDay.today(), CalendarDay.today()) == true
    }
    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(ForegroundColorSpan(Color.parseColor("#7737FF")))
    }
}