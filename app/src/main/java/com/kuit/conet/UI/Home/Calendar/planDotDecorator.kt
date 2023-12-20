package com.kuit.conet.UI.Home.Calendar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.ForegroundColorSpan
import android.text.style.LineBackgroundSpan
import androidx.core.content.ContextCompat
import com.kuit.conet.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CustomDotspan(radius : Float, color: Int, context: Context) : LineBackgroundSpan{
    val density: Float = context.resources.displayMetrics.density
    private var radius = radius*density
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
        canvas.drawCircle(((left + right) / 2).toFloat(), bottom + radius + (8F*density), radius, paint)
        paint.color = oldColor
    }


}
class planDotDecorator(private val context: Context, private val color: Int, private val dates : ArrayList<Int>) : DayViewDecorator{

    override fun shouldDecorate(day: CalendarDay): Boolean {
        val date = formatCalendarDay(day).toInt()
        //Log.d("date","${date}")
        return dates.contains(date)
    }
    override fun decorate(view: DayViewFacade) {
        val color = ContextCompat.getColor(context, color)
        view.addSpan(CustomDotspan(3.5f, color,context)) // 점의 크기와 색상 설정
        val textcolor = ContextCompat.getColor(context, R.color.texthigh)
        view.addSpan(object: ForegroundColorSpan(textcolor){})
    }

    fun formatCalendarDay(calendarDay: CalendarDay): String { // Calendarday 객체 String형으로 변환
        val dateFormat = SimpleDateFormat("d", Locale.getDefault())
        return dateFormat.format(calendarDay.date)
    }

    // dpToPx 함수 정의
//    private fun Context.dpToPx(dp: Float): Float {
//        val scale = resources.displayMetrics.density
//        return dp * scale + 0.5f
//    }
}