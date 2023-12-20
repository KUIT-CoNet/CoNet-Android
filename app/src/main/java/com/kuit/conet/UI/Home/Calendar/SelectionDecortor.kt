package com.kuit.conet.UI.Home.Calendar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.LineBackgroundSpan
import androidx.core.content.ContextCompat
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
class CustomBackgroundspan(radius : Float, color: Int) : LineBackgroundSpan {
    private var radius = radius
    private var color = color

    override fun drawBackground(
        canvas: Canvas,
        paint: Paint,
        left: Int,
        right: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        text: CharSequence,
        start: Int,
        end: Int,
        lineNumber: Int
    ) {
        val oldColor = paint.color
        if (color != 0) {
            paint.color = color
        }
        canvas.drawCircle(((left + right) / 2).toFloat(), ((top + bottom)/2).toFloat(), radius, paint)
        paint.color = oldColor
    }


}
class SelectionDecortor(private val context : Context, private val color: Int) : DayViewDecorator{
    private var selectedDate: CalendarDay? = null // 선택한 날짜를 저장할 변수

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        // 선택한 날짜와 현재 날짜가 동일한지 확인
        return day == selectedDate
    }

    override fun decorate(view: DayViewFacade?) {
//        val drawable = view?.let { viewContext ->
//            ContextCompat.getDrawable(context, R.drawable.calendar_selection_circle)
//        }
//        if (drawable != null) {
//            view?.setSelectionDrawable(drawable)
//        }
        val color = ContextCompat.getColor(context, color)
        view?.addSpan(CustomBackgroundspan(48f, color)) // 점의 크기와 색상 설정
    }

    // 날짜 클릭 시 선택한 날짜 설정 메서드
    fun setSelectedDate(date: CalendarDay) {
        selectedDate = date
    }
}