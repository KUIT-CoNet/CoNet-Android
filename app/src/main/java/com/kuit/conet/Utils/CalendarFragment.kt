package com.kuit.conet.Utils

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kuit.conet.Network.RetrofitClient
import com.kuit.conet.R
import com.kuit.conet.UI.Home.MonthPicker
import com.kuit.conet.UI.Home.Calendar.*
import com.kuit.conet.UI.application.CoNetApplication
import com.kuit.conet.data.dto.response.home.ResponseGetMonthlyPlan
import com.kuit.conet.databinding.FragmentCalendarBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import retrofit2.Call
import retrofit2.Response
import kotlin.collections.ArrayList
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding: FragmentCalendarBinding
        get() = requireNotNull(_binding) { "CalendarFragment's binding is null" }
    private var _onDateChangedListener: OnDateSelectedListener? = null
    private val onDateChangedListener: OnDateSelectedListener
        get() = requireNotNull(_onDateChangedListener) { "CalendarFragment's listener is null" }

    private lateinit var eventDecorator: planDotDecorator
    private val sundayDecorator = SundayDecorator()
    private var onedayDecorator = OnedayDecorator()

    fun setOnDateChangedListener(listener: OnDateSelectedListener) {
        _onDateChangedListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        Log.d(LIFECYCLE, "CalendarFragment - onCreateView() called")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(LIFECYCLE, "CalendarFragment - onViewCreated() called")

        val today = CalendarDay.today()
        initUpdatePlan(today)

        binding.clSelectDate.setOnClickListener {
            showDialog()
        }

        CoroutineScope(Dispatchers.Main).launch {
            val year = today.year.toString()
            val month = if (today.month < 9) "0${today.month + 1}" else "${today.month + 1}"

            binding.viewCanlendar.setTitleFormatter { "${today.year}년  ${today.month + 1}월" }
            val promiseDates = updateDate("$year-$month")
            binding.viewCanlendar.addDecorator(sundayDecorator)
            eventDecorator = planDotDecorator(
                requireContext(),
                R.color.mainsub1,
                promiseDates as ArrayList<Int>
            ) // 일정 있으면 날짜 밑에 점 찍기
            binding.viewCanlendar.addDecorator(eventDecorator)
            binding.viewCanlendar.addDecorator(onedayDecorator)
        }

        val selectionDecortor = SelectionDecortor(requireContext(), R.color.purpleCircle)
        binding.viewCanlendar.addDecorator(selectionDecortor)

        val customWeekDayFormatter = CustomWeekDayFormatter(requireContext())
        binding.viewCanlendar.setWeekDayFormatter(customWeekDayFormatter)

        binding.viewCanlendar.setOnDateChangedListener { widget, date, selected ->
            onDateChangedListener.onDateSelected(widget, date, selected)
            Log.d(TAG, "CalendarFragment - calendar 일 변경 event $date")
            selectionDecortor.setSelectedDate(date)
            binding.viewCanlendar.invalidateDecorators()
        }

        binding.viewCanlendar.setOnMonthChangedListener { widget, date ->
            Log.d(TAG, "CalendarFragment - calendar 월 변경 event $date")
            val year = date.year
            val month = if (date.month < 9) "0${date.month + 1}" else "${date.month + 1}"
            binding.viewCanlendar.setTitleFormatter { "${date.year}년  ${date.month + 1}월" }

            CoroutineScope(Dispatchers.Main).launch {
                val promiseDates = updateDate("$year-$month")
                Log.d(TAG, "CalendarFragment - 데코 지우기")
                binding.viewCanlendar.removeDecorator(eventDecorator)
                eventDecorator = planDotDecorator(
                    requireContext(),
                    R.color.mainsub1,
                    promiseDates as ArrayList<Int>
                )
                binding.viewCanlendar.addDecorator(eventDecorator)
                binding.viewCanlendar.removeDecorator(onedayDecorator)
                onedayDecorator = OnedayDecorator()
                binding.viewCanlendar.addDecorator(onedayDecorator)
                binding.viewCanlendar.invalidateDecorators()
            }
        }

    }

    override fun onDestroyView() {
        _binding = null
        _onDateChangedListener = null
        super.onDestroyView()
        Log.d(LIFECYCLE, "CalendarFragment - onDestroyView() called")
    }

    private fun showDialog() {
        binding.flSelectDate.visibility = View.VISIBLE
        val chooseDateDialog = MonthPicker()
        chooseDateDialog.setOnButtonClickListener(object : MonthPicker.OnButtonClickListener {
            override fun onButtonClicked(year: Int, month: Int) {
                binding.viewCanlendar.currentDate = CalendarDay.from(year, month - 1, 1)
                binding.viewCanlendar.selectedDate = CalendarDay.from(year, month - 1, 1)
                binding.flSelectDate.visibility = View.GONE
            }
        })
        parentFragmentManager.beginTransaction()
            .replace(R.id.fl_select_date, chooseDateDialog)
            .commitAllowingStateLoss()
    }

    private suspend fun updateDate(callDate: String): List<Int> {
        val bearerAccessToken =
            CoNetApplication.getInstance().getDataStore().bearerAccessToken.first()

        return suspendCoroutine { continuation ->
            Log.d(TAG, "CalendarFragment - updateDate() called\n요청 날짜 : $callDate")

            RetrofitClient.homeInstance.getMonthlyPlan(
                authorization = bearerAccessToken,
                searchDate = callDate,
            ).enqueue(object : retrofit2.Callback<ResponseGetMonthlyPlan> {
                override fun onResponse(
                    call: Call<ResponseGetMonthlyPlan>,
                    response: Response<ResponseGetMonthlyPlan>
                ) {
                    if (response.isSuccessful) {
                        Log.d(NETWORK, "CalendarFragment - getMonthlyPlan() 실행 결과 - 성공")
                        val resp =
                            requireNotNull(response.body()) { "CalendarFragment - getMonthlyPlan() 실행 결과 불러오기 실패" }
                        val promiseDates = resp.result.dates
                        continuation.resume(promiseDates)
                    } else {
                        Log.d(NETWORK, "CalendarFragment - getMonthlyPlan() 실행 결과 - 안좋음")
                        continuation.resumeWithException(Exception("Response not successful"))
                    }
                }

                override fun onFailure(call: Call<ResponseGetMonthlyPlan>, t: Throwable) {
                    Log.d(NETWORK, "CalendarFragment - getMonthlyPlan() 실행 결과 - 실패\nbecause : $t")
                    continuation.resumeWithException(t)
                }
            })
        }
    }

    private fun initUpdatePlan(today: CalendarDay) {
        binding.viewCanlendar.setTitleFormatter { "${today.year}년  ${today.month + 1}월" }
        binding.viewCanlendar.selectedDate = today
        binding.viewCanlendar.setDateTextAppearance(R.style.CalendarDateTextStyle)
        binding.viewCanlendar.setHeaderTextAppearance(R.style.CalendarHeaderTextStyle)
        binding.viewCanlendar.setWeekDayTextAppearance(R.style.CalendarWeekdayTextStyle)
    }

}