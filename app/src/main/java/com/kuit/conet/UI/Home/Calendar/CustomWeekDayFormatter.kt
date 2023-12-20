package com.kuit.conet.UI.Home.Calendar

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import com.prolificinteractive.materialcalendarview.format.WeekDayFormatter
import java.util.*

class CustomWeekDayFormatter(private val context: Context) : WeekDayFormatter {
    override fun format(dayOfWeek: Int): CharSequence {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek)
        val weekDayText = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())

        val colorRes = if (dayOfWeek == Calendar.SUNDAY) Color.RED else Color.BLACK
        val spannable = SpannableString(weekDayText)
        spannable.setSpan(ForegroundColorSpan(colorRes), 0, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannable
    }
}