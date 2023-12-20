package com.kuit.conet.Utils

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kuit.conet.R
import com.kuit.conet.UI.Home.Calendar.*
import com.kuit.conet.UI.Home.choose_date_dialog
import com.kuit.conet.databinding.FragmentCalenderdialogBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener

class CalenderDialog  : Fragment() {
    lateinit var binding : FragmentCalenderdialogBinding
    val sundayDecorator = SundayDecorator()
    val onedayDecorator = OnedayDecorator()

    private var onDateChangedListener: OnDateSelectedListener? = null

    fun setOnDateChangedListener(listener: OnDateSelectedListener) {
        Log.d(" 실행","실행")
        onDateChangedListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCalenderdialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            onDateChangedListener?.onDateSelected(widget, date, selected)
            Log.d("호출","호출")
            selectionDecortor.setSelectedDate(date)
            binding.viewCanlendar.invalidateDecorators()
        }

        binding.viewCanlendar.setOnMonthChangedListener { widget, date ->
            Log.d("monthchange", "${date}")
            val year = date.year.toString()
            val month = if(date.month + 1 >= 10) (date.month + 1).toString() else "0" + (date.month + 1)
            val date = year + "-" + month
            Log.d("date","${date}")
        }
        val customWeekDayFormatter = CustomWeekDayFormatter(requireContext())
        binding.viewCanlendar.setWeekDayFormatter(customWeekDayFormatter)

    }

    fun showDialog(){
        binding.flSelectDate2.visibility = View.VISIBLE
        val chooseDateDialog = choose_date_dialog()
        chooseDateDialog.setOnButtonClickListener(object :
            choose_date_dialog.OnButtonClickListener {
            override fun onButtonClicked(year: Int, month: Int) {
                binding.viewCanlendar.currentDate = CalendarDay.from(year, month-1, 1)
                binding.viewCanlendar.selectedDate = CalendarDay.from(year, month-1, 1)
                binding.flSelectDate2.visibility = View.GONE
            }
        })
        parentFragmentManager.beginTransaction()
            .replace(R.id.fl_select_date2, chooseDateDialog)
            .commitAllowingStateLoss()
    }
}