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
import com.kuit.conet.UI.Home.choose_date_dialog
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
        binding.BtnSelectDate.isEnabled = false
        binding.clSelectDate.setOnClickListener {
            Log.d("click!","클릭감지")
            showDialog()
        }
        binding.viewCalendar.selectedDate = CalendarDay.today()
        binding.viewCalendar.setDateTextAppearance(R.style.CalendarDateTextStyle)
        binding.viewCalendar.setHeaderTextAppearance(R.style.CalendarHeaderTextStyle)
        binding.viewCalendar.setTitleFormatter { day -> "${day!!.year}년  ${day.month + 1}월" }
        binding.viewCalendar.setWeekDayTextAppearance(R.style.CalendarWeekdayTextStyle)
        binding.viewCalendar.addDecorator(sundayDecorator)
        binding.viewCalendar.addDecorator(onedayDecorator)

        val selectionDecorator = SelectionDecortor(requireContext(), R.color.purpleCircle)
        binding.viewCalendar.addDecorator(selectionDecorator)

        binding.viewCalendar.setOnDateChangedListener { widget, date, selected ->
            Log.d("호출","호출")
            selectionDecorator.setSelectedDate(date)
            binding.viewCalendar.invalidateDecorators()
            binding.btnSelectCl.isEnabled = true
            binding.btnSelectCl.setBackgroundResource(R.color.purpleMain)
        }

        binding.viewCalendar.setOnMonthChangedListener { widget, date ->
            Log.d("monthchange", "$date")
            val year = date.year.toString()
            val month = if(date.month + 1 >= 10) (date.month + 1).toString() else "0" + (date.month + 1)
            val date = "$year-$month"
            Log.d("date", date)

        }

        binding.btnSelectCl.setOnClickListener {

            val date = binding.viewCalendar.selectedDate
            val year = (date.year).toString()
            val month = if(date.month + 1 < 10) "0"+(date.month + 1).toString() else (date.month + 1).toString()
            val day = if(date.day < 10) "0" + (date.day).toString() else (date.day).toString()
            val sendDate = "$year.$month.$day"
            buttonClickListener?.onButtonClicked(sendDate)


        }

        binding.dialogCalender.setOnClickListener {
            val date = binding.viewCalendar.selectedDate
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
        binding.viewCalendar.setWeekDayFormatter(customWeekDayFormatter)
        return binding.root
    }

    private fun showDialog(){
        binding.flSelectDate.visibility = View.VISIBLE
        val chooseDateDialog = choose_date_dialog()
        chooseDateDialog.setOnButtonClickListener(object :
            choose_date_dialog.OnButtonClickListener {
            override fun onButtonClicked(year: Int, month: Int) {
                binding.viewCalendar.currentDate = CalendarDay.from(year, month-1, 1)
                binding.viewCalendar.selectedDate = CalendarDay.from(year, month-1, 1)
                binding.flSelectDate.visibility = View.GONE
            }
        })
        parentFragmentManager.beginTransaction()
            .replace(R.id.fl_select_date, chooseDateDialog)
            .commitAllowingStateLoss()
    }
}