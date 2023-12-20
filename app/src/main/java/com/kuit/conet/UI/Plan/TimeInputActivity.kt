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
    lateinit var binding : ActivityTimeInputBinding
    var page = 1
    var disable = false
    var planId = 0

    var Time1 : PossibleDateTime= PossibleDateTime("", arrayListOf())
    var Time2 : PossibleDateTime= PossibleDateTime("", arrayListOf())
    var Time3 : PossibleDateTime= PossibleDateTime("", arrayListOf())
    var Time4 : PossibleDateTime= PossibleDateTime("", arrayListOf())
    var Time5 : PossibleDateTime= PossibleDateTime("", arrayListOf())
    var Time6 : PossibleDateTime= PossibleDateTime("", arrayListOf())
    var Time7 : PossibleDateTime= PossibleDateTime("", arrayListOf())

    lateinit var day1 : ArrayList<View>
    lateinit var day2 : ArrayList<View>
    lateinit var day3 : ArrayList<View>
    lateinit var isCheckDay1 : ArrayList<Boolean>
    lateinit var isCheckDay2 : ArrayList<Boolean>
    lateinit var isCheckDay3 : ArrayList<Boolean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimeInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        planId = intent.getIntExtra("planId", 0)

        day1 = arrayListOf(
            binding.day100, binding.day101, binding.day102, binding.day103, binding.day104,
            binding.day105, binding.day106, binding.day107, binding.day108, binding.day109,
            binding.day110, binding.day111, binding.day112, binding.day113, binding.day114,
            binding.day115, binding.day116, binding.day117, binding.day118, binding.day119,
            binding.day120, binding.day121, binding.day122, binding.day123
        )
        day2  = arrayListOf(
            binding.day200, binding.day201, binding.day202, binding.day203, binding.day204,
            binding.day205, binding.day206, binding.day207, binding.day208, binding.day209,
            binding.day210, binding.day211, binding.day212, binding.day213, binding.day214,
            binding.day215, binding.day216, binding.day217, binding.day218, binding.day219,
            binding.day220, binding.day221, binding.day222, binding.day223
        )
        day3 = arrayListOf(
            binding.day300, binding.day301, binding.day302, binding.day303, binding.day304,
            binding.day305, binding.day306, binding.day307, binding.day308, binding.day309,
            binding.day310, binding.day311, binding.day312, binding.day313, binding.day314,
            binding.day315, binding.day316, binding.day317, binding.day318, binding.day319,
            binding.day320, binding.day321, binding.day322, binding.day323
        )
        isCheckDay1  = arrayListOf(
            false, false, false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false, false, false,
            false, false, false, false
        )
        isCheckDay2  = arrayListOf(
            false, false, false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false, false, false,
            false, false, false, false
        )
        isCheckDay3  = arrayListOf(
            false, false, false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false, false, false,
            false, false, false, false
        )

        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            getFrame(planId)//get api 연결 : planName, planStartPeriod, colorNum 범위 지정
        }

        binding.btnBackIv.setOnClickListener { finish() }

        binding.ivPrev.setOnClickListener {
            if(page==2){
                TableToPDT(2)
                page=1
                setFrame(page)
                if (!disable) {
                    if (Time1.time.size==0 && Time2.time.size==0 && Time3.time.size==0) allClearTime()
                    else PDTtoTable(Time1, Time2, Time3)
                } //disable이 true라면 가능한 시간 없음 선택 상태
            }
            else if (page==3){
                TableToPDT(3)
                page=2
                setFrame(page)
                if (!disable) {
                    if (Time4.time.size==0 && Time5.time.size==0 && Time6.time.size==0) allClearTime()
                    else PDTtoTable(Time4, Time5, Time6)
                }
            }
        }

        binding.ivNext.setOnClickListener {
            if (page==1) {
                TableToPDT(1)
                page=2
                setFrame(page)
                if (!disable) {
                    if (Time4.time.size==0 && Time5.time.size==0 && Time6.time.size==0) allClearTime()
                    else PDTtoTable(Time4, Time5, Time6)
                }
            } 
            else if (page==2) {
                TableToPDT(2)
                page=3
                setFrame(page)
                if (!disable) {
                    if (Time7.time.size==0) allClearTime()
                    else PDTtoTable(Time7, null, null)
                }
            }
        }

        updateClickListener(true)

        binding.mcvClock.setOnClickListener {
            disable =! disable
            updateDisable(disable)
        }

        binding.btnCv.setOnClickListener {
            //해당 페이지 변경내용 저장
            TableToPDT(page)
            //서버 연결
            inputTime(getMyTime())
            val intent = Intent(this, PlanTimeActivity::class.java)
            intent.putExtra("planId", planId)
            intent.putExtra("planStartPeriod", intent.getStringExtra("planStartPeriod"))
            intent.putExtra("groupId", intent.getIntExtra("groupId",0))
            Log.d("넘기기전 값들", "start : ${intent.getStringExtra("planStartPeriod")} / groupId : ${intent.getIntExtra("groupId",0)}")
            startActivity(intent)
            finish()
        }
    }

    private suspend fun getFrame(planId : Int) {
        Log.d("TimeInputActivity", "Get showMemTime api 실행 중")
        return suspendCoroutine { continuation ->
            val showMyTimeService = getRetrofit().create(RetrofitInterface::class.java)
            val refreshToken = getRefreshToken(this)
            showMyTimeService.ShowMyTime(
                "Bearer $refreshToken",
                planId
            ).enqueue(object : retrofit2.Callback<ShowMyTime>{
                override fun onResponse(
                    call: Call<ShowMyTime>,
                    response: Response<ShowMyTime>
                ) {
                    if (response.isSuccessful){
                        val resp = response.body()
                        Log.i("L:ShowMyTime/성공", resp.toString())

                        //초기 세팅
                        setDateOfTime(intent.getStringExtra("planStartPeriod").toString())

                        if (!resp!!.result.hasRegisteredTime) {
                            Log.d("L:입력한시간없었음", resp!!.result.hasRegisteredTime.toString())

                        }
                        else {
                            if (resp!!.result.hasPossibleTime){
                                Log.d("L:입력한시간있음", resp!!.result.hasPossibleTime.toString())
                                initTime(resp!!.result.possibleTime)
                            }
                            else {
                                Log.d("L:가능한시간없음", resp!!.result.hasPossibleTime.toString())
                                disable = true
                                updateDisable(disable)
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
        Log.d("L:setDateOFTime()실행", true.toString())
        val planStartPeriod = date
        Time1.date = planStartPeriod.replace(".","-")
        val startDate = LocalDate.parse(Time1.date)
        Time2.date = startDate.plusDays(1).toString().replace(".","-")
        Time3.date = startDate.plusDays(2).toString().replace(".","-")
        Time4.date = startDate.plusDays(3).toString().replace(".","-")
        Time5.date = startDate.plusDays(4).toString().replace(".","-")
        Time6.date = startDate.plusDays(5).toString().replace(".","-")
        Time7.date = startDate.plusDays(6).toString().replace(".","-")
    }

    private fun initTime(possibleTimeList: ArrayList<PossibleDateTime>) {
        for (j in 0 until possibleTimeList[0].time.size) Time1.time.add(possibleTimeList[0].time[j])
        for (j in 0 until possibleTimeList[1].time.size) Time2.time.add(possibleTimeList[0].time[j])
        for (j in 0 until possibleTimeList[2].time.size) Time3.time.add(possibleTimeList[0].time[j])
        for (j in 0 until possibleTimeList[3].time.size) Time4.time.add(possibleTimeList[0].time[j])
        for (j in 0 until possibleTimeList[4].time.size) Time5.time.add(possibleTimeList[0].time[j])
        for (j in 0 until possibleTimeList[5].time.size) Time6.time.add(possibleTimeList[0].time[j])
        for (j in 0 until possibleTimeList[6].time.size) Time7.time.add(possibleTimeList[0].time[j])
    }

    //클릭 동작 활성화 함수 (true넣으면 동작)
    private fun updateClickListener(res: Boolean) {
        if (res) {
            for (i in 0..23 step(1)) {
                day1[i].setOnClickListener { updateCheck(1, i) }
                day2[i].setOnClickListener { updateCheck(2, i) }
                day3[i].setOnClickListener { updateCheck(3, i) }
            }
        }
        else {
            for (i in 0..23 step(1)) {
                day1[i].setOnClickListener {  }
                day2[i].setOnClickListener {  }
                day3[i].setOnClickListener {  }
            }
        }
    }

    //요일 구하기
    fun getDay(date : LocalDate):String{
        var n : String = date.dayOfWeek.toString()
        var day = ""

        when (n) {
            "SUNDAY" -> day="일"
            "MONDAY" -> day="월"
            "TUESDAY" -> day="화"
            "WEDNESDAY" -> day="수"
            "THURSDAY" -> day="목"
            "FRIDAY" -> day="금"
            "SATURDAY" -> day="토"
        }

        return day
    }

    //옆으로 이동시 화면 세팅
    private fun setFrame(page: Int) {
        Log.d("L:setFrame실행", "page : ${page}")
        val startDate = LocalDate.parse(intent.getStringExtra("planStartPeriod"))
        when (page) {
            1 -> {
                binding.tvDate1.text = Time1.date.substring(5).replace("-",".")
                binding.tvDate2.text = Time2.date.substring(5).replace("-",".")
                binding.tvDate3.text = Time3.date.substring(5).replace("-",".")
                binding.tvDay1.text = getDay(startDate)
                binding.tvDay2.text = getDay(startDate.plusDays(1))
                binding.tvDay3.text = getDay(startDate.plusDays(2))
                binding.ivPrev.visibility = View.GONE
                binding.ivNext.visibility = View.VISIBLE

                PDTtoTable(Time1, Time2, Time3)
            }
            2 -> {
                binding.tvDate1.text = Time4.date.substring(5).replace("-",".")
                binding.tvDate2.text = Time5.date.substring(5).replace("-",".")
                binding.tvDate3.text = Time6.date.substring(5).replace("-",".")
                binding.tvDate2.visibility = View.VISIBLE
                binding.tvDate3.visibility = View.VISIBLE
                binding.tvDay1.text = getDay(startDate.plusDays(3))
                binding.tvDay2.text = getDay(startDate.plusDays(4))
                binding.tvDay3.text = getDay(startDate.plusDays(5))
                binding.tvDay2.visibility = View.VISIBLE
                binding.tvDay3.visibility = View.VISIBLE

                binding.ivPrev.visibility = View.VISIBLE
                binding.ivNext.visibility = View.VISIBLE
                for (i in 0..23){
                    day2[i].visibility = View.VISIBLE
                    day3[i].visibility = View.VISIBLE
                }

                PDTtoTable(Time4, Time5, Time6)
            }
            3 -> {
                binding.tvDate1.text = Time7.date.substring(5).replace("-",".")
                binding.tvDate2.visibility = View.GONE
                binding.tvDate3.visibility = View.GONE
                binding.tvDay1.text = getDay(startDate.plusDays(6))
                binding.tvDay2.visibility = View.GONE
                binding.tvDay3.visibility = View.GONE

                binding.ivPrev.visibility = View.VISIBLE
                binding.ivNext.visibility = View.GONE
                for (i in 0..23){
                    day2[i].visibility = View.GONE
                    day3[i].visibility = View.GONE
                }

                PDTtoTable(Time7, null, null)
            }
        }
    }

    //입력한 시간 Time에 저장
    fun TableToPDT(page : Int){
        Log.d("L:TableToPDT 실행", "page : $page")
        when (page) {
            1 -> {
                Time1.time.clear()
                Time2.time.clear()
                Time3.time.clear()
                for (j in 0..23){
                    if (isCheckDay1[j]) Time1.time.add(j)
                    if (isCheckDay2[j]) Time2.time.add(j)
                    if (isCheckDay3[j]) Time3.time.add(j)
                }
            }
            2 -> {
                Time4.time.clear()
                Time5.time.clear()
                Time6.time.clear()
                for (j in 0..23){
                    if (isCheckDay1[j]) Time4.time.add(j)
                    if (isCheckDay2[j]) Time5.time.add(j)
                    if (isCheckDay3[j]) Time6.time.add(j)
                }
            }
            3 -> {
                Time7.time.clear()
                for (j in 0..23){
                    if (isCheckDay1[j]) Time7.time.add(j)
                }
            }
        }
    }

    //저장된 PossibleDateTime 불러와서 표에 표시
    fun PDTtoTable(PDT1 : PossibleDateTime, PDT2 : PossibleDateTime?, PDT3 : PossibleDateTime?){
        for(i in 0..23){
            if (PDT1.time.size==0)  clearTimetable(1)  //time 입력된 것이 없으면 clear
            else { //있으면 그 부분만 색칠&true로 변경
                for(j in 0 until PDT1.time.size){
                    val index = PDT1.time[j]
                    if (i==index) {
                        day1[i].setBackgroundResource(R.drawable.view_border_check)
                        isCheckDay1[i]=true
                        break
                    }
                    else {
                        day1[i].setBackgroundResource(R.drawable.view_border)
                        isCheckDay1[i]=false
                    }
                }
            }

            if (PDT2!=null && PDT3!=null) { //page1,2에서만 동작
                if (PDT2.time.size==0) clearTimetable(2) //time 입력된 것이 없으면 clear
                else {
                    for(j in 0 until PDT2.time.size){
                        val index = PDT2.time[j]
                        if (i==index) {
                            day2[i].setBackgroundResource(R.drawable.view_border_check)
                            isCheckDay2[i]=true
                            break
                        } else {
                            day2[i].setBackgroundResource(R.drawable.view_border)
                            isCheckDay2[i]=false
                        }
                    }
                }
                if (PDT3.time.size==0) clearTimetable(3) //time 입력된 것이 없으면 clear
                else {
                    for(j in 0 until PDT3.time.size){
                        val index = PDT3.time[j]
                        if (i==index) {
                            day3[i].setBackgroundResource(R.drawable.view_border_check)
                            isCheckDay3[i]=true
                            break
                        } else {
                            day3[i].setBackgroundResource(R.drawable.view_border)
                            isCheckDay3[i]=false
                        }
                    }
                }
            }
        }
    }

    //day 하나만 clear
    fun clearTimetable(num:Int){
        for (i in 0..23 step(1)){
            when (num) {
                1 -> {
                    day1[i].setBackgroundResource(R.drawable.view_border)
                    isCheckDay1[i]=false
                }
                2 -> {
                    day2[i].setBackgroundResource(R.drawable.view_border)
                    isCheckDay2[i]=false
                }
                3 -> {
                    day3[i].setBackgroundResource(R.drawable.view_border)
                    isCheckDay3[i]=false
                }
            }
        }
    }

    //day 3개 다 clear
    private fun allClearTime() {
        for (i in 1..3) clearTimetable(i)
    }

    //모든 시간대 전부 '가능한 시간 없음'
    @SuppressLint("ResourceAsColor")
    fun updateDisable(disable : Boolean){
        if(disable){ //'가능한 시간 없음' 선택
            binding.mcvClock.setBackgroundResource(R.drawable.view_clock_check)
            binding.tv.setTextColor(
                ContextCompat.getColor(this,
                R.color.purpleMain
            ))
            Time1.time.clear()
            Time2.time.clear()
            Time3.time.clear()
            Time4.time.clear()
            Time5.time.clear()
            Time6.time.clear()
            Time7.time.clear()
            allClearTime()

            for (i in 0..23 step(1)) {
                day1[i].setBackgroundResource(R.drawable.view_border_disable)
                day2[i].setBackgroundResource(R.drawable.view_border_disable)
                day3[i].setBackgroundResource(R.drawable.view_border_disable)
            }
            //클릭 기능 해제하기
            updateClickListener(false)
        }
        else{ //'가능한 시간 없음' 해제
            binding.mcvClock.setBackgroundResource(R.drawable.view_clock_clear)
            binding.tv.setTextColor(
                ContextCompat.getColor(this,
                R.color.gray200
            ))
            for (i in 0..23 step(1)) {
                day1[i].setBackgroundResource(R.drawable.view_border)
                day2[i].setBackgroundResource(R.drawable.view_border)
                day3[i].setBackgroundResource(R.drawable.view_border)
            }
            //클릭 기능 활성화
            updateClickListener(true)
        }
    }

    private fun updateCheck(day: Int, i: Int) {
        when (day) {
            1 -> {
                if (!isCheckDay1[i]) day1[i].setBackgroundResource(R.drawable.view_border_check)
                else day1[i].setBackgroundResource(R.drawable.view_border)
                isCheckDay1[i] =! isCheckDay1[i]
            }
            2 -> {
                if (!isCheckDay2[i])  day2[i].setBackgroundResource(R.drawable.view_border_check)
                else day2[i].setBackgroundResource(R.drawable.view_border)
                isCheckDay2[i] =! isCheckDay2[i]
            }
            3 -> {
                if (!isCheckDay3[i]) day3[i].setBackgroundResource(R.drawable.view_border_check)
                else day3[i].setBackgroundResource(R.drawable.view_border)
                isCheckDay3[i] =! isCheckDay3[i]
            }
        }
    }

    private fun getMyTime() : InputMyTime{
        return InputMyTime(
            planId = intent.getIntExtra("planId",0),
            possibleDateTimes = arrayListOf(Time1, Time2, Time3, Time4, Time5, Time6, Time7)
        )
    }

    private fun inputTime(inputMyTime: InputMyTime){
        val refreshToken = getRefreshToken(this)
        val inputTimeService = getRetrofit().create(RetrofitInterface::class.java)

        inputTimeService.InputMyTime(
            "Bearer $refreshToken",
            inputMyTime
        ).enqueue(object : retrofit2.Callback<ResponseInputMyTime>{
            override fun onResponse(
                call: Call<ResponseInputMyTime>,
                response: Response<ResponseInputMyTime>
            ) {
                if (response.isSuccessful){
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