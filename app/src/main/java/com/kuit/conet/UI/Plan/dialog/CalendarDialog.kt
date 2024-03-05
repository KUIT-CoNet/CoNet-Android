package com.example.calenderdialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kuit.conet.R
import com.kuit.conet.UI.Home.Calendar.CustomWeekDayFormatter
import com.kuit.conet.UI.Home.Calendar.OnedayDecorator
import com.kuit.conet.UI.Home.Calendar.SelectionDecortor
import com.kuit.conet.UI.Home.Calendar.SundayDecorator
import com.kuit.conet.UI.Home.MonthPicker
import com.kuit.conet.databinding.DialogCalendarBinding
import com.prolificinteractive.materialcalendarview.CalendarDay

class CalenderDialog : Fragment() {
    lateinit var binding : DialogCalendarBinding
    private val sundayDecorator = SundayDecorator()
    private val onedayDecorator = OnedayDecorator()

    interface OnButtonClickListener {
        fun onButtonClicked(date : String)
    }

    private var buttonClickListener: OnButtonClickListener? = null

    fun setOnButtonClickListener(listener: OnButtonClickListener) {
        buttonClickListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogCalendarBinding.inflate(inflater, container, false)
        binding.cvCalendarSelectBtn.isEnabled = false
        binding.clCalendarSelectDate.setOnClickListener {
            Log.d("click!","클릭감지")
            showDialog()
        }
        binding.mcvCalendar.selectedDate = CalendarDay.today()
        binding.mcvCalendar.setDateTextAppearance(R.style.CalendarDateTextStyle)
        binding.mcvCalendar.setHeaderTextAppearance(R.style.CalendarHeaderTextStyle)
        binding.mcvCalendar.setTitleFormatter { day -> "${day!!.year}년  ${day.month + 1}월" }
        binding.mcvCalendar.setWeekDayTextAppearance(R.style.CalendarWeekdayTextStyle)
        binding.mcvCalendar.addDecorator(sundayDecorator)
        binding.mcvCalendar.addDecorator(onedayDecorator)

        val selectionDecorator = SelectionDecortor(requireContext(), R.color.purpleCircle)
        binding.mcvCalendar.addDecorator(selectionDecorator)

        binding.mcvCalendar.setOnDateChangedListener { widget, date, selected ->
            Log.d("호출","호출")
            selectionDecorator.setSelectedDate(date)
            binding.mcvCalendar.invalidateDecorators()
            binding.btnSelectCl.isEnabled = true
            binding.btnSelectCl.setBackgroundResource(R.color.purpleMain)
        }

        binding.mcvCalendar.setOnMonthChangedListener { widget, date ->
            Log.d("monthchange", "$date")
            val year = date.year.toString()
            val month = if(date.month + 1 >= 10) (date.month + 1).toString() else "0" + (date.month + 1)
            val date = "$year-$month"
            Log.d("date", date)

        }

        binding.btnSelectCl.setOnClickListener {

            val date = binding.mcvCalendar.selectedDate
            val year = (date.year).toString()
            val month = if(date.month + 1 < 10) "0"+(date.month + 1).toString() else (date.month + 1).toString()
            val day = if(date.day < 10) "0" + (date.day).toString() else (date.day).toString()
            val sendDate = "$year.$month.$day"
            buttonClickListener?.onButtonClicked(sendDate)


        }

        binding.dialogCalender.setOnClickListener {
            val date = binding.mcvCalendar.selectedDate
            val year = (date.year).toString()
            val month = if(date.month + 1 < 10) "0"+(date.month + 1).toString() else (date.month + 1).toString()
            val day = (date.day).toString()
            val sendDate = "$year.$month.$day"
            buttonClickListener?.onButtonClicked(sendDate)
            val fragment = parentFragmentManager.findFragmentById(R.id.fl_calendar)
            if (fragment != null) {
                parentFragmentManager.beginTransaction()
                    .remove(fragment)
                    .commit()
            }
        }
        val customWeekDayFormatter = CustomWeekDayFormatter(requireContext())
        binding.mcvCalendar.setWeekDayFormatter(customWeekDayFormatter)
        return binding.root
    }

    private fun showDialog(){
        binding.flCalendarSelectDate.visibility = View.VISIBLE
        val chooseDateDialog = MonthPicker()
        chooseDateDialog.setOnButtonClickListener(object :
            MonthPicker.OnButtonClickListener {
            override fun onButtonClicked(year: Int, month: Int) {
                binding.mcvCalendar.currentDate = CalendarDay.from(year, month-1, 1)
                binding.mcvCalendar.selectedDate = CalendarDay.from(year, month-1, 1)
                binding.flCalendarSelectDate.visibility = View.GONE
            }
        })
        parentFragmentManager.beginTransaction()
            .replace(R.id.fl_select_date, chooseDateDialog)
            .commitAllowingStateLoss()
    }
}