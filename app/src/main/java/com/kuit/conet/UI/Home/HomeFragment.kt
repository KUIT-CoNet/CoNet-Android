package com.kuit.conet.UI.Home

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import com.kuit.conet.Utils.CalendarFragment
import com.kuit.conet.R
import com.kuit.conet.Utils.LIFECYCLE
import com.kuit.conet.Utils.OncallList
import com.kuit.conet.Utils.TAG
import com.kuit.conet.Utils.Todolist
import com.kuit.conet.databinding.FragmentHomeBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding
        get() = requireNotNull(_binding) { "HomeHomeFragment's binding's null" }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        Log.d(LIFECYCLE, "HomeFragment - onCreateView() called")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(LIFECYCLE, "HomeFragment - onViewCreated() called")

        val calendarFragment = CalendarFragment()
        calendarFragment.setOnDateChangedListener { widget, date, selected ->
            val selectedDateString = dateString(date.date, 1)
            val today = Date() // 오늘 날짜
            val todayString = dateString(today, 1)
            Log.d(TAG, selectedDateString)

            if (todayString == selectedDateString) {
                binding.tvPromiseDate.text = "오늘의 약속"
            } else {
                binding.tvPromiseDate.text = "$selectedDateString 의 약속"
            }
            Log.d(TAG, dateString(date.date, 2))
            val todolist = Todolist(date, -1)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fl_todolist, todolist)
                .commitAllowingStateLoss()
            CoroutineScope(Dispatchers.Main).launch {
                todolist.waitForInit()
                binding.tvPromiseCount.text = todolist.returnsize().toString()
            }
        }

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

        val today = CalendarDay.today() // 오늘 날짜
        val todolist = Todolist(today, -1)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fl_todolist, todolist)
            .commitAllowingStateLoss()

        CoroutineScope(Dispatchers.Main).launch {
            todolist.waitForInit()
            binding.tvPromiseCount.text = todolist.returnsize().toString()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
        Log.d(LIFECYCLE, "HomeFragment - onDestroyView() called")
    }

    private fun dateString(
        date: Date,
        option: Int
    ): String { // option이 1이면 m월 d일 형식, option이 2면 yyyy년 m월 d일 형식
        val dateFormat: SimpleDateFormat = if (option == 1) {
            SimpleDateFormat("M월 d일", Locale.getDefault())
        } else {
            SimpleDateFormat("yyyy년 M월 d일", Locale.getDefault())
        }
        return dateFormat.format(date)
    }

}