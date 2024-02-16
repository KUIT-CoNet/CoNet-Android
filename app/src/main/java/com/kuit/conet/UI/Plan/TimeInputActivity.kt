package com.kuit.conet.UI.Plan

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.kuit.conet.Network.InputMyTime
import com.kuit.conet.Network.PossibleDateTime
import com.kuit.conet.Network.ResponseInputMyTime
import com.kuit.conet.Network.RetrofitInterface
import com.kuit.conet.Network.ShowMyTime
import com.kuit.conet.Network.getRetrofit
import com.kuit.conet.R
import com.kuit.conet.databinding.ActivityTimeInputBinding
import com.kuit.conet.getRefreshToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.time.LocalDate
import kotlin.coroutines.suspendCoroutine

class TimeInputActivity : AppCompatActivity() {
    lateinit var binding: ActivityTimeInputBinding
    var page = 1
    var disable = false
    var planId = 0

    private var time1: PossibleDateTime = PossibleDateTime("", arrayListOf())
    private var time2: PossibleDateTime = PossibleDateTime("", arrayListOf())
    private var time3: PossibleDateTime = PossibleDateTime("", arrayListOf())
    private var time4: PossibleDateTime = PossibleDateTime("", arrayListOf())
    private var time5: PossibleDateTime = PossibleDateTime("", arrayListOf())
    private var time6: PossibleDateTime = PossibleDateTime("", arrayListOf())
    private var time7: PossibleDateTime = PossibleDateTime("", arrayListOf())

    private var isCheckDay = Array(7) { Array(24) { false } }

    private lateinit var day1: ArrayList<View>
    private lateinit var day2: ArrayList<View>
    private lateinit var day3: ArrayList<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimeInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        planId = intent.getIntExtra("planId", 0)

        day1 = arrayListOf(
            binding.vTimeInputDay100,
            binding.vTimeInputDay101,
            binding.vTimeInputDay102,
            binding.vTimeInputDay103,
            binding.vTimeInputDay104,
            binding.vTimeInputDay105,
            binding.vTimeInputDay106,
            binding.vTimeInputDay107,
            binding.vTimeInputDay108,
            binding.vTimeInputDay109,
            binding.vTimeInputDay110,
            binding.vTimeInputDay111,
            binding.vTimeInputDay112,
            binding.vTimeInputDay113,
            binding.vTimeInputDay114,
            binding.vTimeInputDay115,
            binding.vTimeInputDay116,
            binding.vTimeInputDay117,
            binding.vTimeInputDay118,
            binding.vTimeInputDay119,
            binding.vTimeInputDay120,
            binding.vTimeInputDay121,
            binding.vTimeInputDay122,
            binding.vTimeInputDay123
        )
        day2 = arrayListOf(
            binding.vTimeInputDay200,
            binding.vTimeInputDay201,
            binding.vTimeInputDay202,
            binding.vTimeInputDay203,
            binding.vTimeInputDay204,
            binding.vTimeInputDay205,
            binding.vTimeInputDay206,
            binding.vTimeInputDay207,
            binding.vTimeInputDay208,
            binding.vTimeInputDay209,
            binding.vTimeInputDay210,
            binding.vTimeInputDay211,
            binding.vTimeInputDay212,
            binding.vTimeInputDay213,
            binding.vTimeInputDay214,
            binding.vTimeInputDay215,
            binding.vTimeInputDay216,
            binding.vTimeInputDay217,
            binding.vTimeInputDay218,
            binding.vTimeInputDay219,
            binding.vTimeInputDay220,
            binding.vTimeInputDay221,
            binding.vTimeInputDay222,
            binding.vTimeInputDay223
        )
        day3 = arrayListOf(
            binding.vTimeInputDay300,
            binding.vTimeInputDay301,
            binding.vTimeInputDay302,
            binding.vTimeInputDay303,
            binding.vTimeInputDay304,
            binding.vTimeInputDay305,
            binding.vTimeInputDay306,
            binding.vTimeInputDay307,
            binding.vTimeInputDay308,
            binding.vTimeInputDay309,
            binding.vTimeInputDay310,
            binding.vTimeInputDay311,
            binding.vTimeInputDay312,
            binding.vTimeInputDay313,
            binding.vTimeInputDay314,
            binding.vTimeInputDay315,
            binding.vTimeInputDay316,
            binding.vTimeInputDay317,
            binding.vTimeInputDay318,
            binding.vTimeInputDay319,
            binding.vTimeInputDay320,
            binding.vTimeInputDay321,
            binding.vTimeInputDay322,
            binding.vTimeInputDay323
        )

        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            getFrame(planId) //get api 연결 : planName, planStartPeriod, colorNum 범위 지정
            //여기서 사용자 Id 가져와서 색칠 필요
        }

        binding.ivTimeInputBackBtn.setOnClickListener {
            val intent = Intent(this, PlanTimeActivity::class.java)
            intent.putExtra("planId", planId)
            intent.putExtra("planStartPeriod", intent.getStringExtra("planStartPeriod"))
            intent.putExtra("groupId", intent.getIntExtra("groupId", 0))
            startActivity(intent)
            finish()
        }

        binding.ivTimeInputPrevBtn.setOnClickListener {
            if (page == 2) {
                tableToPDT(2)
                page = 1
                setFrame(page)
                if (!disable) {
                    updateClickListener(1, true)
                    pdtToTable(1, time1, time2, time3)
                } else {
                    updateDisable(1, true)
                }
            } else if (page == 3) {
                tableToPDT(3)
                page = 2
                setFrame(page)
                if (!disable) {
                    updateClickListener(4, true)
                    pdtToTable(4, time4, time5, time6)
                } else {
                    updateDisable(4, true)
                }
            }
        }

        binding.ivTimeInputNextBtn.setOnClickListener {
            if (page == 1) {
                tableToPDT(1)
                page = 2
                setFrame(page)
                if (!disable) {
                    updateClickListener(4, true)
                    pdtToTable(4, time4, time5, time6)
                } else {
                    updateDisable(4, true)
                }
            } else if (page == 2) {
                tableToPDT(2)
                page = 3
                setFrame(page)
                if (!disable) {
                    updateClickListener(7, true)
                    pdtToTable(7, time7, null, null)
                } else {
                    updateDisable(7, true)
                }
            }
        }

        updateClickListener(1, true)

        binding.vTimeInputClockBorder.setOnClickListener {
            disable = !disable
            for (i in intArrayOf(1,4,7)) {
                updateDisable(i, disable)
            }
        }
    }

    private suspend fun getFrame(planId: Int) {
        Log.d("TimeInputActivity", "Get showMemTime api 실행 중")
        return suspendCoroutine { continuation ->
            val showMyTimeService = getRetrofit().create(RetrofitInterface::class.java)
            val refreshToken = getRefreshToken(this)
            showMyTimeService.ShowMyTime(
                "Bearer $refreshToken",
                planId
            ).enqueue(object : retrofit2.Callback<ShowMyTime> {
                override fun onResponse(
                    call: Call<ShowMyTime>,
                    response: Response<ShowMyTime>
                ) {
                    if (response.isSuccessful) {
                        val resp = response.body()
                        Log.i("L:ShowMyTime/성공", resp.toString())

                        //초기 세팅
                        setDateOfTime(intent.getStringExtra("planStartPeriod").toString())

                        if (!resp!!.result.hasRegisteredTime) {
                            Log.d("L:입력한시간없었음", resp.result.hasRegisteredTime.toString())

                        } else {
                            if (resp.result.hasPossibleTime) {
                                Log.d("L:입력한시간있음", resp.result.hasPossibleTime.toString())
                                initTime(resp.result.possibleTime)
                            } else {
                                Log.d("L:가능한시간없음", resp.result.hasPossibleTime.toString())
                                disable = true
                                updateDisable(1, disable)
                            }
                        }
                        setFrame(page)
                    }
                }

                override fun onFailure(call: Call<ShowMyTime>, t: Throwable) {
                    Log.i("L:ShowMyTime/실패", t.message.toString())
                }

            })
        }

    }

    //Time마다 date 입력
    private fun setDateOfTime(date: String) {
        time1.date = date.replace(".", "-")
        val startDate = LocalDate.parse(time1.date)
        time2.date = startDate.plusDays(1).toString().replace(".", "-")
        time3.date = startDate.plusDays(2).toString().replace(".", "-")
        time4.date = startDate.plusDays(3).toString().replace(".", "-")
        time5.date = startDate.plusDays(4).toString().replace(".", "-")
        time6.date = startDate.plusDays(5).toString().replace(".", "-")
        time7.date = startDate.plusDays(6).toString().replace(".", "-")
    }

    private fun initTime(possibleTimeList: ArrayList<PossibleDateTime>) {
        for (j in 0 until possibleTimeList[0].time.size) time1.time.add(possibleTimeList[0].time[j])
        for (j in 0 until possibleTimeList[1].time.size) time2.time.add(possibleTimeList[0].time[j])
        for (j in 0 until possibleTimeList[2].time.size) time3.time.add(possibleTimeList[0].time[j])
        for (j in 0 until possibleTimeList[3].time.size) time4.time.add(possibleTimeList[0].time[j])
        for (j in 0 until possibleTimeList[4].time.size) time5.time.add(possibleTimeList[0].time[j])
        for (j in 0 until possibleTimeList[5].time.size) time6.time.add(possibleTimeList[0].time[j])
        for (j in 0 until possibleTimeList[6].time.size) time7.time.add(possibleTimeList[0].time[j])
    }

    //클릭 동작 활성화 함수 (true 넣으면 동작)
    private fun updateClickListener(day: Int, res: Boolean) {
        if (res) {
            if (day==7) {
                for (i in 0..23 step (1)) day1[i].setOnClickListener { updateCheck(day, i) }
            } else {
                for (i in 0..23 step (1)) {
                    day1[i].setOnClickListener { updateCheck(day, i) }
                    day2[i].setOnClickListener { updateCheck(day + 1, i) }
                    day3[i].setOnClickListener { updateCheck(day + 2, i) }
                }
            }
        } else {
            for (i in 0..23 step (1)) {
                day1[i].setOnClickListener { }
                day2[i].setOnClickListener { }
                day3[i].setOnClickListener { }
            }
        }
    }

    private fun getDay(date: LocalDate): String { //요일 구하기
        val n: String = date.dayOfWeek.toString()
        var day = ""
        when (n) {
            "SUNDAY" -> day = "일"
            "MONDAY" -> day = "월"
            "TUESDAY" -> day = "화"
            "WEDNESDAY" -> day = "수"
            "THURSDAY" -> day = "목"
            "FRIDAY" -> day = "금"
            "SATURDAY" -> day = "토"
        }
        return day
    }

    private fun setFrame(page: Int) { //옆으로 이동시 화면 세팅
        val startDate = LocalDate.parse(intent.getStringExtra("planStartPeriod"))
        when (page) {
            1 -> {
                binding.tvTimeInputDate1.text = time1.date.substring(5).replace("-", ".")
                binding.tvTimeInputDate2.text = time2.date.substring(5).replace("-", ".")
                binding.tvTimeInputDate3.text = time3.date.substring(5).replace("-", ".")
                binding.tvTimeInputDay1.text = getDay(startDate)
                binding.tvTimeInputDay2.text = getDay(startDate.plusDays(1))
                binding.tvTimeInputDay3.text = getDay(startDate.plusDays(2))
                binding.ivTimeInputPrevBtn.visibility = View.GONE
                binding.ivTimeInputNextBtn.visibility = View.VISIBLE
                pdtToTable(1, time1, time2, time3)
            }

            2 -> {
                binding.tvTimeInputDate1.text = time4.date.substring(5).replace("-", ".")
                binding.tvTimeInputDate2.text = time5.date.substring(5).replace("-", ".")
                binding.tvTimeInputDate3.text = time6.date.substring(5).replace("-", ".")
                binding.tvTimeInputDate2.visibility = View.VISIBLE
                binding.tvTimeInputDate3.visibility = View.VISIBLE
                binding.tvTimeInputDay1.text = getDay(startDate.plusDays(3))
                binding.tvTimeInputDay2.text = getDay(startDate.plusDays(4))
                binding.tvTimeInputDay3.text = getDay(startDate.plusDays(5))
                binding.tvTimeInputDay2.visibility = View.VISIBLE
                binding.tvTimeInputDay3.visibility = View.VISIBLE
                binding.ivTimeInputPrevBtn.visibility = View.VISIBLE
                binding.ivTimeInputNextBtn.visibility = View.VISIBLE
                for (i in 0..23) {
                    day2[i].visibility = View.VISIBLE
                    day3[i].visibility = View.VISIBLE
                }
                pdtToTable(4, time4, time5, time6)
            }

            3 -> {
                binding.tvTimeInputDate1.text = time7.date.substring(5).replace("-", ".")
                binding.tvTimeInputDate2.visibility = View.GONE
                binding.tvTimeInputDate3.visibility = View.GONE
                binding.tvTimeInputDay1.text = getDay(startDate.plusDays(6))
                binding.tvTimeInputDay2.visibility = View.GONE
                binding.tvTimeInputDay3.visibility = View.GONE
                binding.ivTimeInputPrevBtn.visibility = View.VISIBLE
                binding.ivTimeInputNextBtn.visibility = View.GONE
                for (i in 0..23) {
                    day2[i].visibility = View.GONE
                    day3[i].visibility = View.GONE
                }
                pdtToTable(7, time7, null, null)
            }
        }
    }

    private fun updateCheck(day: Int, i: Int) { //선택된 칸의 색칠 유무 결정
        isCheckDay[day - 1][i] = !isCheckDay[day - 1][i]
        if (isCheckDay[day - 1][i]) {
            when (day) {
                1, 4, 7 -> {
                    day1[i].setBackgroundResource(R.drawable.view_border_check)
                }

                2, 5 -> {
                    day2[i].setBackgroundResource(R.drawable.view_border_check)
                }

                3, 6 -> {
                    day3[i].setBackgroundResource(R.drawable.view_border_check)
                }
            }
            updateSaveBtn() //저장버튼 활성화
        } else {
            when (day) {
                1, 4, 7 -> {
                    day1[i].setBackgroundResource(R.drawable.view_border)
                }

                2, 5 -> {
                    day2[i].setBackgroundResource(R.drawable.view_border)
                }

                3, 6 -> {
                    day3[i].setBackgroundResource(R.drawable.view_border)
                }
            }
            updateSaveBtn() //저장버튼 비활성화
        }
    }

    private fun tableToPDT(page: Int) { //표에 색칠된 부분을 PossibleDateTime 형식으로 저장
        when (page) {
            1 -> {
                time1.time.clear()
                time2.time.clear()
                time3.time.clear()
                for (i in 0..23) {
                    if (isCheckDay[0][i]) {
                        time1.time.add(i)
                    }
                    if (isCheckDay[1][i]) {
                        time2.time.add(i)
                    }
                    if (isCheckDay[2][i]) {
                        time3.time.add(i)
                    }
                }
            }

            2 -> {
                time4.time.clear()
                time5.time.clear()
                time6.time.clear()
                for (i in 0..23) {
                    if (isCheckDay[3][i]) {
                        time4.time.add(i)
                    }
                    if (isCheckDay[4][i]) {
                        time5.time.add(i)
                    }
                    if (isCheckDay[5][i]) {
                        time6.time.add(i)
                    }
                }
            }

            3 -> {
                time7.time.clear()
                for (i in 0..23) {
                    if (isCheckDay[6][i]) {
                        time7.time.add(i)
                    }
                }
            }
        }
    }

    private fun pdtToTable( //저장된 PossibleDateTime 정보를 받아 이를 바탕으로 화면 구성
        day: Int, //시작 날이 1,4,7
        pdt1: PossibleDateTime,
        pdt2: PossibleDateTime?,
        pdt3: PossibleDateTime?
    ) {
        clearTimetable() //화면만 초기화
        clearIsCheck(day)
        for (i in 0 until pdt1.time.size) {
            day1[pdt1.time[i]].setBackgroundResource(R.drawable.view_border_check)
            isCheckDay[day - 1][pdt1.time[i]] = true
        }
        if (pdt2 != null && pdt3 != null) {
            for (i in 0 until pdt2.time.size) {
                day2[pdt2.time[i]].setBackgroundResource(R.drawable.view_border_check)
                isCheckDay[day][pdt2.time[i]] = true

            }
            for (i in 0 until pdt3.time.size) {
                day3[pdt3.time[i]].setBackgroundResource(R.drawable.view_border_check)
                isCheckDay[day + 1][pdt3.time[i]] = true

            }
        }
    }

    private fun allClearTime() { //모든 일주일 다 초기화
        clearTimetable() //화면초기화
        for (i in intArrayOf(1,4,7)) { //ischeck 초기화
            clearIsCheck(i)
        }
    }

    private fun clearTimetable() { //화면 초기화
        for (i in 0..23 step (1)) {
            day1[i].setBackgroundResource(R.drawable.view_border)
            day2[i].setBackgroundResource(R.drawable.view_border)
            day3[i].setBackgroundResource(R.drawable.view_border)
        }
    }

    private fun clearIsCheck(day: Int) { //check 부분 초기화
        if (day==7) {
            for (i in 0..23 step (1)) {
                isCheckDay[day -1][i] = false
            }
        } else {
            for (i in 0..23 step (1)) {
                isCheckDay[day - 1][i] = false
                isCheckDay[day][i] = false
                isCheckDay[day + 1][i] = false
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    fun updateDisable(day: Int, disable: Boolean) { //모든 시간대 전부 '가능한 시간 없음' 버튼 클릭
        if (disable) { //'가능한 시간 없음' 선택
            binding.vTimeInputClockBorder.setBackgroundResource(R.drawable.view_clock_check)
            binding.tvTimeInputClockText.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.purpleMain
                )
            )
            time1.time.clear()
            time2.time.clear()
            time3.time.clear()
            time4.time.clear()
            time5.time.clear()
            time6.time.clear()
            time7.time.clear()
            allClearTime() //표 및 isCheck 초기화

            for (i in 0..23 step (1)) {
                day1[i].setBackgroundResource(R.drawable.view_border_disable)
                day2[i].setBackgroundResource(R.drawable.view_border_disable)
                day3[i].setBackgroundResource(R.drawable.view_border_disable)
            }

            updateSaveBtn() //저장버튼 비활성화
            updateClickListener(day, false) //클릭 기능 해제 하기

        } else { //'가능한 시간 없음' 해제
            binding.vTimeInputClockBorder.setBackgroundResource(R.drawable.view_clock_clear)
            binding.tvTimeInputClockText.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.gray200
                )
            )
            for (i in 0..23 step (1)) {
                day1[i].setBackgroundResource(R.drawable.view_border)
                day2[i].setBackgroundResource(R.drawable.view_border)
                day3[i].setBackgroundResource(R.drawable.view_border)
            }
            updateClickListener(day, true) //클릭 기능 활성화
        }
    }

    private fun updateSaveBtn() { //저장버튼 색 및 기능 업데이트 함수
        binding.cvTimeInputSaveBtn.setBackgroundResource(R.drawable.background_rectangular_gray_10)
        binding.cvTimeInputSaveBtn.setOnClickListener { }

        for (i in 0..6) {
            for (j in 0..23) {
                if (isCheckDay[i][j]) {
                    binding.cvTimeInputSaveBtn.setBackgroundResource(R.drawable.background_rectangular_purple_10)
                    activateSaveBtn()
                    return
                }
            }
        }
    }

    private fun activateSaveBtn() { //저장버튼 활성화 함수
        binding.cvTimeInputSaveBtn.setOnClickListener {
            //해당 페이지 변경 내용 저장
            tableToPDT(page)
            //서버 연결
            inputTime(getMyTime())
            val intent = Intent(this, PlanTimeActivity::class.java)
            intent.putExtra("planId", planId)
            intent.putExtra("planStartPeriod", intent.getStringExtra("planStartPeriod"))
            intent.putExtra("groupId", intent.getIntExtra("groupId", 0))
            startActivity(intent)
            finish()
        }
    }

    private fun getMyTime(): InputMyTime {
        return InputMyTime(
            planId = intent.getIntExtra("planId", 0),
            possibleDateTimes = arrayListOf(time1, time2, time3, time4, time5, time6, time7)
        )
    }

    private fun inputTime(inputMyTime: InputMyTime) {
        val refreshToken = getRefreshToken(this)
        val inputTimeService = getRetrofit().create(RetrofitInterface::class.java)

        inputTimeService.InputMyTime(
            "Bearer $refreshToken",
            inputMyTime
        ).enqueue(object : retrofit2.Callback<ResponseInputMyTime> {
            override fun onResponse(
                call: Call<ResponseInputMyTime>,
                response: Response<ResponseInputMyTime>
            ) {
                if (response.isSuccessful) {
                    val resp = response.body()
                    Log.d("API-plan/time/Success", resp.toString())
                }
            }

            override fun onFailure(call: Call<ResponseInputMyTime>, t: Throwable) {
                Log.d("API-plan/time/Fail", t.message.toString())
            }
        })
    }
}