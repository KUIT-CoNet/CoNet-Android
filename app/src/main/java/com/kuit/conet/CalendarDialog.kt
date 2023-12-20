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
    val sundayDecorator = SundayDecorator()
    val onedayDecorator = OnedayDecorator()

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
            Log.d("click!","클릭감지!!!")
            showDialog()
        }
        binding.viewCanlendar.selectedDate = CalendarDay.today()
        binding.viewCanlendar.setDateTextAppearance(R.style.CalendarDateTextStyle)
        binding.viewCanlendar.setHeaderTextAppearance(R.style.CalendarHeaderTextStyle)
        binding.viewCanlendar.setTitleFormatter { day -> "${day!!.year}년  ${day.month + 1}월" }
        binding.viewCanlendar.setWeekDayTextAppearance(R.style.CalendarWeekdayTextStyle)
        binding.viewCanlendar.addDecorator(sundayDecorator)
        binding.viewCanlendar.addDecorator(onedayDecorator)

        val selectionDecortor = SelectionDecortor(requireContext(), R.color.purpleCircle)
        binding.viewCanlendar.addDecorator(selectionDecortor)

        binding.viewCanlendar.setOnDateChangedListener { widget, date, selected ->
            Log.d("호출","호출")
            selectionDecortor.setSelectedDate(date)
            binding.viewCanlendar.invalidateDecorators()
            binding.btnSelectCl.isEnabled = true
            binding.btnSelectCl.setBackgroundResource(R.color.purpleMain)
        }

        binding.viewCanlendar.setOnMonthChangedListener { widget, date ->
            Log.d("monthchange", "${date}")
            val year = date.year.toString()
            val month = if(date.month + 1 >= 10) (date.month + 1).toString() else "0" + (date.month + 1)
            val date = year + "-" + month
            Log.d("date","${date}")

        }

        binding.btnSelectCl.setOnClickListener {

            val date = binding.viewCanlendar.selectedDate
            val year = (date.year).toString()
            val month = if(date.month + 1 < 10) "0"+(date.month + 1).toString() else (date.month + 1).toString()
            val day = if(date.day < 10) "0" + (date.day).toString() else (date.day).toString()
            val sendDate = year + "." + month + "." + day
            buttonClickListener?.onButtonClicked(sendDate)


        }

        binding.dialogCalender.setOnClickListener {
            val date = binding.viewCanlendar.selectedDate
            val year = (date.year).toString()
            val month = if(date.month + 1 < 10) "0"+(date.month + 1).toString() else (date.month + 1).toString()
            val day = (date.day).toString()
            val sendDate = year + "." + month + "." + day
            buttonClickListener?.onButtonClicked(sendDate)
            val fragment = parentFragmentManager.findFragmentById(R.id.fl_calendar)
            if (fragment != null) {
                parentFragmentManager.beginTransaction()
                    .remove(fragment)
                    .commit()
            }
        }
        val customWeekDayFormatter = CustomWeekDayFormatter(requireContext())
        binding.viewCanlendar.setWeekDayFormatter(customWeekDayFormatter)
        return binding.root
    }

    fun showDialog(){
        binding.flSelectDate.visibility = View.VISIBLE
        val chooseDateDialog = choose_date_dialog()
        chooseDateDialog.setOnButtonClickListener(object :
            choose_date_dialog.OnButtonClickListener {
            override fun onButtonClicked(year: Int, month: Int) {
                binding.viewCanlendar.currentDate = CalendarDay.from(year, month-1, 1)
                binding.viewCanlendar.selectedDate = CalendarDay.from(year, month-1, 1)
                binding.flSelectDate.visibility = View.GONE
            }
        })
        parentFragmentManager.beginTransaction()
            .replace(R.id.fl_select_date, chooseDateDialog)
            .commitAllowingStateLoss()
    }
}