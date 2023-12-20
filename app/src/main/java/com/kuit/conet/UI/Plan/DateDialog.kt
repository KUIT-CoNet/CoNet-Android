package com.kuit.conet.UI.Plan

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kuit.conet.R
import com.kuit.conet.UI.Home.Calendar.*
import com.kuit.conet.UI.Home.choose_date_dialog
import com.kuit.conet.Utils.TAG
import com.kuit.conet.databinding.DialogBottomSheetDateBinding
import com.prolificinteractive.materialcalendarview.CalendarDay

class DateDialog(var year:String, var month:String, var day:String): BottomSheetDialogFragment() {

    private lateinit var binding: DialogBottomSheetDateBinding

    val sundayDecorator = SundayDecorator()
    val onedayDecorator = OnedayDecorator()
    val dayDecorator = DayDecorator()

    interface OnButtonClickListener {
        fun onButtonClicked(year: String, month: String, date: String)
    }

    private var buttonClickListener: OnButtonClickListener? = null

    fun setOnButtonClickListener(listener: OnButtonClickListener) {
        buttonClickListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DialogBottomSheetDateBinding.inflate(layoutInflater, container, false)
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
        binding.viewCanlendar.addDecorator(dayDecorator)

        val selectionDecortor = SelectionDecortor(requireContext(), R.color.purpleCircle)
        binding.viewCanlendar.addDecorator(selectionDecortor)

        binding.viewCanlendar.setOnDateChangedListener { widget, date, selected ->
            Log.d("호출","호출")
            selectionDecortor.setSelectedDate(date)
            binding.viewCanlendar.invalidateDecorators()
            year = date.year.toString()
            month = if(date.month + 1 > 10) (date.month + 1).toString() else "0"+(date.month + 1).toString()
            day = if(date.day > 10) date.day.toString() else "0"+date.day
        }

        binding.viewCanlendar.setOnMonthChangedListener { widget, date ->
            Log.d("monthchange", "${date}")
            /*val year = date.year.toString()
            val month = if(date.month + 1 >= 10) (date.month + 1).toString() else "0" + (date.month + 1)
            val date = "$year-$month"*/
            Log.d(TAG, "DateDialog - viewCalendar 날짜 변경됨\n" +
                    "date : $date")
        }

        binding.applyBtn.setOnClickListener {
            /*val date = binding.viewCanlendar.selectedDate
            val year = date.year.toString()
            val month = if(date.month + 1 < 10) "0"+(date.month + 1).toString() else (date.month + 1).toString()
            val day = if(date.day<10) "0${date.day}" else (date.day).toString()
            val sendDate = "$year. $month. $day"
            Log.d(TAG, "DateDialog - applyBtn 눌렀을 때\n" +
                    "sendData : $sendDate")*/
            Log.d(
                TAG, "DateDialog - applyBtn 클릭됨 최종 날짜\n" +
                        "year : $year, month : $month, day: $day"
            )

            buttonClickListener?.onButtonClicked(year, month, day)

            dismiss()
        }

        binding.dialogCalendarFl.setOnClickListener {
            Log.d("달력이즈","사라짐")
            val date = binding.viewCanlendar.selectedDate
            year = (date.year).toString()
            month = if(date.month + 1 < 10) "0"+(date.month + 1).toString() else (date.month + 1).toString()
            day = if(date.day<10) "0${date.day}" else (date.day).toString()
            Log.d(TAG, "DateDialog - calendar 날짜 변경\n" +
                    "year : $year, month : $month, day: $day")
            val sendDate = "$year. $month. $day"

//            buttonClickListener?.onButtonClicked(year, month, day)

            val fragment = parentFragmentManager.findFragmentById(R.id.fl_calendar)
            if (fragment != null) {
                parentFragmentManager.beginTransaction()
                    .remove(fragment)
                    .commit()
            }
        }
        val customWeekDayFormatter = CustomWeekDayFormatter(requireContext())
        customWeekDayFormatter
        binding.viewCanlendar.setWeekDayFormatter(customWeekDayFormatter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle( // Background -> Transparent.
            BottomSheetDialogFragment.STYLE_NORMAL,
            R.style.TransparentBottomSheetDialogFragment
        )
    }

    fun showDialog(){
        binding.flSelectDate3.visibility = View.VISIBLE
        val chooseDateDialog = choose_date_dialog()
        chooseDateDialog.setOnButtonClickListener(object :
            choose_date_dialog.OnButtonClickListener {
            override fun onButtonClicked(year: Int, month: Int) {
                binding.viewCanlendar.currentDate = CalendarDay.from(year, month-1, 1)
                binding.viewCanlendar.selectedDate = CalendarDay.from(year, month-1, 1)
                binding.flSelectDate3.visibility = View.GONE
            }
        })
        childFragmentManager.beginTransaction()
        .replace(R.id.fl_select_date3, chooseDateDialog)
        .commitAllowingStateLoss()
    }
}