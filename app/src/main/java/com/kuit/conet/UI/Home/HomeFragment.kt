package com.kuit.conet.UI.Home

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import com.kuit.conet.Utils.CalendarFragment
import com.kuit.conet.R
import com.kuit.conet.Utils.OncallList
import com.kuit.conet.Utils.Todolist
import com.kuit.conet.databinding.FragmentHomeBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class Home : Fragment() { //커밋푸쉬위해 임시로 주석써둠

    lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val today = CalendarDay.today() // 오늘 날짜


        // Inflate the layout for this fragment
        val calendarFragment = CalendarFragment()
        calendarFragment.setOnDateChangedListener(object : OnDateSelectedListener {
            override fun onDateSelected(
                widget: MaterialCalendarView,
                date: CalendarDay,
                selected: Boolean
            ) {
                Log.d("인터페이스", "실행")
                val selectedDateString = dateString(date.date, 1)
                val today = Date() // 오늘 날짜
                val todayString = dateString(today, 1)
                // 로그로 선택한 날짜 정보 출력
                Log.d("Selected date", "${selectedDateString}")

                if (todayString == selectedDateString) {
                    binding.tvPromiseDate.text = "오늘의 약속"
                } else {
                    binding.tvPromiseDate.text = selectedDateString + "의 약속"
                }
                Log.d("dateString테스트", "${dateString(date.date, 2)}")
                val todolist = Todolist(date, -1)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fl_todolist, todolist)
                    .commitAllowingStateLoss()
                CoroutineScope(Dispatchers.Main).launch {
                    todolist.waitForInit()
                    binding.tvPromiseCount.text = todolist.returnsize().toString()
                }
            }
        })
        parentFragmentManager.beginTransaction()
            .replace(R.id.fl_calendarView, calendarFragment)
            .commitAllowingStateLoss()


        val oncallList = OncallList(-1, 1)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fl_oncalllist, oncallList)
            .commitAllowingStateLoss()

        CoroutineScope(Dispatchers.Main).launch {
            oncallList.waitForInit()
            binding.tvPromiseallCount.text = oncallList.returnsize().toString()
        }


        val todolist = Todolist(today, -1)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fl_todolist, todolist)
            .commitAllowingStateLoss()

        CoroutineScope(Dispatchers.Main).launch {
            todolist.waitForInit()
            binding.tvPromiseCount.text = todolist.returnsize().toString()
        }


        return binding.root
    }

    fun dateString(
        date: Date,
        option: Int
    ): String { // option이 1이면 m월 d일 형식, option이 2면 yyyy년 m월 d일 형식
        var dateFormat: SimpleDateFormat
        if (option == 1) {
            dateFormat = SimpleDateFormat("M월 d일", Locale.getDefault())
        } else {
            dateFormat = SimpleDateFormat("yyyy년 M월 d일", Locale.getDefault())
        }
        val selectedDateString = dateFormat.format(date)
        return selectedDateString
    }


}