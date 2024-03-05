package com.kuit.conet.Utils

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kuit.conet.Network.HomePlanShow
import com.kuit.conet.Network.RetrofitInterface
import com.kuit.conet.Network.getRetrofit
import com.kuit.conet.R
import com.kuit.conet.UI.Home.Calendar.*
import com.kuit.conet.UI.Home.MonthPicker
import com.kuit.conet.databinding.FragmentCalendarBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

//모임 메인 메뉴에 넣을 캘린더뷰
class GroupCalendarFragment (planId : Int) : Fragment() {
    lateinit var binding : FragmentCalendarBinding
    lateinit var eventDecorator : planDotDecorator

    val planId = planId // 모임 아이디
    val sundayDecorator = SundayDecorator()
    var onedayDecorator = OnedayDecorator()

    var promiseDates = ArrayList<Int>() // 서버로 부터 약속 있는 날짜 받아오기

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
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val today = CalendarDay.today()
        Log.d("today","${today}")

        binding.clSelectDate.setOnClickListener {
            Log.d("click!","클릭감지!!!")
            showDialog()
        }
        binding.viewCanlendar.selectedDate = CalendarDay.today()
        binding.viewCanlendar.setDateTextAppearance(R.style.CalendarDateTextStyle)
        binding.viewCanlendar.setHeaderTextAppearance(R.style.CalendarHeaderTextStyle)
        binding.viewCanlendar.setTitleFormatter { day -> "${day!!.year}년  ${day.month + 1}월" }


        val selectionDecortor = SelectionDecortor(requireContext(), R.color.purpleCircle)
        binding.viewCanlendar.addDecorator(selectionDecortor)

        CoroutineScope(Dispatchers.Main).launch {
            val year = today.year.toString()
            val month = if(today.month + 1 < 10) "0" + (today.month + 1).toString() else (today.month + 1).toString()
            val callDate = year + "-" + month
            promiseDates = UpdatePlanDates(callDate)
            eventDecorator = planDotDecorator(requireContext(), R.color.mainsub1, promiseDates) // 일정 있으면 날짜 밑에 점 찍기
            binding.viewCanlendar.addDecorator(eventDecorator)
            binding.viewCanlendar.setWeekDayTextAppearance(R.style.CalendarWeekdayTextStyle)
            binding.viewCanlendar.addDecorator(sundayDecorator)
            binding.viewCanlendar.addDecorator(onedayDecorator)
        }

        val customWeekDayFormatter = CustomWeekDayFormatter(requireContext())
        binding.viewCanlendar.setWeekDayFormatter(customWeekDayFormatter)

        binding.viewCanlendar.setOnDateChangedListener { widget, date, selected ->
            onDateChangedListener?.onDateSelected(widget, date, selected)
            Log.d("호출","${date}")
            selectionDecortor.setSelectedDate(date)
            binding.viewCanlendar.invalidateDecorators()
        }

        binding.viewCanlendar.setOnMonthChangedListener { widget, date ->
            Log.d("월","${date}")
            val year = date.year.toString()
            val month = if(date.month + 1 < 10) "0" + (date.month + 1).toString() else (date.month + 1).toString()
            val callDate = year + "-" + month
            val coroutineScope = CoroutineScope(Dispatchers.Main)
            coroutineScope.launch {
                promiseDates = UpdatePlanDates(callDate)
                Log.d("delete","데코 지우기")
                binding.viewCanlendar.removeDecorator(eventDecorator)
                eventDecorator = planDotDecorator(requireContext(), R.color.mainsub1, promiseDates)
                binding.viewCanlendar.addDecorator(eventDecorator)
                binding.viewCanlendar.removeDecorator(onedayDecorator)
                onedayDecorator = OnedayDecorator()
                binding.viewCanlendar.addDecorator(onedayDecorator)
                binding.viewCanlendar.invalidateDecorators()
            }
            //UpdatePlanDates(callDate)
        }


    }

    fun showDialog(){
        binding.flSelectDate.visibility = View.VISIBLE
        val chooseDateDialog = MonthPicker()
        chooseDateDialog.setOnButtonClickListener(object :
            MonthPicker.OnButtonClickListener {
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

    suspend fun UpdatePlanDates(callDate : String) : ArrayList<Int>{
        return suspendCoroutine { continuation->
            Log.d("callDate","${callDate}")
            val responseDate = getRetrofit().create(RetrofitInterface::class.java) // 이걸 따로 빼내는 방법....
            responseDate.ShowGroupMonth(
                planId,
                callDate
            ).enqueue(object :
                Callback<HomePlanShow> { // 서버와 비동기적으로 데이터 주고받을 수 있는 방법 enqueue사용
                override fun onResponse( // 통신에 성공했을 경우
                    call: Call<HomePlanShow>,
                    response: Response<HomePlanShow>
                ) {
                    if (response.isSuccessful) {
                        val resp = response.body()// 성공했을 경우 response body불러오기
                        Log.d("SIGNUP/SUCCESS", resp.toString())
                        Log.d("성공!","success")
                        promiseDates = resp!!.result.dates
                        continuation.resume(promiseDates)
                    }
                    else{
                        continuation.resumeWithException(Exception("Response not successful"))
                    }
                }

                override fun onFailure(call: Call<HomePlanShow>, t: Throwable) { // 통신에 실패했을 경우
                    Log.d("SIGNUP/FAILURE", t.message.toString()) // 실패한 이유 메세지 출력
                    continuation.resumeWithException(t)
                }

            })
        }

    }

}