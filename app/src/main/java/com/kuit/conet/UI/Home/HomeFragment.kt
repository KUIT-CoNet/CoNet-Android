package com.kuit.conet.UI.Home

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.kuit.conet.Utils.CalendarFragment
import com.kuit.conet.R
import com.kuit.conet.Utils.LIFECYCLE
import com.kuit.conet.Utils.OncallList
import com.kuit.conet.Utils.Todolist
import com.kuit.conet.Utils.calendar.compareWithLocalDate
import com.kuit.conet.Utils.calendar.toString
import com.kuit.conet.databinding.FragmentHomeBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
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
            val today = LocalDate.now()

            if (date.compareWithLocalDate(today)) {
                binding.tvPromiseDate.text = "오늘의 약속"
            } else {
                binding.tvPromiseDate.text = "${date.toString(false)} 의 약속"
            }

            val todolist = Todolist(date, -1)
            childFragmentManager.beginTransaction()
                .replace(R.id.fl_todolist, todolist)
                .commitAllowingStateLoss()

            viewLifecycleOwner.lifecycleScope.launch {
                todolist.waitForInit()
                binding.tvPromiseCount.text = todolist.returnsize().toString()
            }
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.fl_calendarView, calendarFragment)
            .commitAllowingStateLoss()

        val oncallList = OncallList(-1, 1)
        childFragmentManager.beginTransaction()
            .replace(R.id.fl_oncalllist, oncallList)
            .commitAllowingStateLoss()

        viewLifecycleOwner.lifecycleScope.launch {
            oncallList.waitForInit()
            binding.tvPromiseallCount.text = oncallList.returnsize().toString()
        }

        val today = CalendarDay.today() // 오늘 날짜
        val todolist = Todolist(today, -1)
        childFragmentManager.beginTransaction()
            .replace(R.id.fl_todolist, todolist)
            .commitAllowingStateLoss()

        viewLifecycleOwner.lifecycleScope.launch {
            todolist.waitForInit()
            binding.tvPromiseCount.text =
                todolist.returnsize().toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d(LIFECYCLE, "HomeFragment - onDestroyView() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(LIFECYCLE, "HomeFragment - onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(LIFECYCLE, "HomeFragment - onDestroy() called")
    }

    private fun dateString(
        date: Date,
        isyear: Boolean,
    ): String { // option이 1이면 m월 d일 형식, option이 2면 yyyy년 m월 d일 형식
        return if (isyear) {
            SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault()).format(date)
        } else {
            SimpleDateFormat("MM월 dd일", Locale.getDefault()).format(date)
        }
    }

}