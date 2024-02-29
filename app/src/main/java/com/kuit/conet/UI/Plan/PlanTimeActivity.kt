package com.kuit.conet.UI.Plan

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.kuit.conet.Network.AvailableMemberDateTime
import com.kuit.conet.Network.EndNumberForEachSection
import com.kuit.conet.Network.PossibleMember
import com.kuit.conet.Network.RetrofitInterface
import com.kuit.conet.Network.ShowMemTime
import com.kuit.conet.Network.getRetrofit
import com.kuit.conet.R
import com.kuit.conet.UI.GroupMain.GroupMainActivity
import com.kuit.conet.Utils.NETWORK
import com.kuit.conet.Utils.TAG
import com.kuit.conet.databinding.ActivityPlanTimeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.time.LocalDate
import kotlin.coroutines.suspendCoroutine

class PlanTimeActivity : AppCompatActivity() {
    lateinit var binding: ActivityPlanTimeBinding
    var page = 1
    var teamId = 0
    var planId = -1
    var planName = ""
    private var planStartDate = ""
    lateinit var endNumberForEachSection: EndNumberForEachSection

    //view 만드는 3개의 날
    private lateinit var day1: ArrayList<View>
    private lateinit var day2: ArrayList<View>
    private lateinit var day3: ArrayList<View>

    //일주일 정보를 각각 담음
    private var day1Info: ArrayList<PossibleMember> = arrayListOf()
    private var day2Info: ArrayList<PossibleMember> = arrayListOf()
    private var day3Info: ArrayList<PossibleMember> = arrayListOf()
    private var day4Info: ArrayList<PossibleMember> = arrayListOf()
    private var day5Info: ArrayList<PossibleMember> = arrayListOf()
    private var day6Info: ArrayList<PossibleMember> = arrayListOf()
    private var day7Info: ArrayList<PossibleMember> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlanTimeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        day1 = arrayListOf(
            binding.day100, binding.day101, binding.day102, binding.day103, binding.day104,
            binding.day105, binding.day106, binding.day107, binding.day108, binding.day109,
            binding.day110, binding.day111, binding.day112, binding.day113, binding.day114,
            binding.day115, binding.day116, binding.day117, binding.day118, binding.day119,
            binding.day120, binding.day121, binding.day122, binding.day123
        )
        day2 = arrayListOf(
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


        planStartDate = intent.getStringExtra("planStartDate").toString()
        planId = intent.getIntExtra("planId", 0)
        val planName = intent.getStringExtra("planName")

        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            getFrame(planId) //api로 정보 받아오기
        }

        binding.btnCloseIv.setOnClickListener {
            val intent = Intent(this, GroupMainActivity::class.java)
            intent.putExtra("teamId", teamId)
            startActivity(intent)
            finish()
        }

        binding.ivPrev.setOnClickListener {
            if (page == 2) page = 1 else if (page == 3) page = 2
            setFrame(page)
        }

        binding.ivNext.setOnClickListener {
            if (page == 1) page = 2 else if (page == 2) page = 3
            setFrame(page)
        }

        binding.ivBtn.setOnClickListener {
            val planMenuDialog = PlanMenuDialog()
            val bundle = Bundle()
            bundle.putString("planName", planName)
            bundle.putString("planDate", planStartDate)
            bundle.putInt("planId", planId)
            bundle.putInt("teamId", teamId)
            planMenuDialog.arguments = bundle
            supportFragmentManager.beginTransaction()
                .replace(R.id.fl_plan, planMenuDialog)
                .commitAllowingStateLoss()
        }

        binding.btnCv.setOnClickListener {
            val intent = Intent(this, TimeInputActivity::class.java)
            intent.putExtra("planId", planId)
            intent.putExtra("planStartPeriod", planStartDate)
            startActivity(intent)
            finish()
        }
    }

    //요일 구하기
    private fun getDay(date: LocalDate): String {
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

    private suspend fun getFrame(planId: Int) {
        Log.d(NETWORK, "Get showMemTime api 실행 중")
        return suspendCoroutine {
            val showMemberTimeService = getRetrofit().create(RetrofitInterface::class.java)
            showMemberTimeService.ShowMemTime(planId)
                .enqueue(object : retrofit2.Callback<ShowMemTime> {
                    override fun onResponse(
                        call: Call<ShowMemTime>,
                        response: Response<ShowMemTime>
                    ) {
                        if (response.isSuccessful) {
                            val resp = response.body()
                            Log.i(NETWORK, "getFrame : ${resp.toString()}")

                            teamId = resp!!.result.teamId
                            this@PlanTimeActivity.planId = resp!!.result.planId
                            planName = resp!!.result.planName
                            planStartDate = resp!!.result.planStartPeriod
                            endNumberForEachSection = resp!!.result.endNumberForEachSection

                            initInfo(resp!!.result.availableMemberDateTime)
                            setColorNumText(resp!!.result.endNumberForEachSection)

                            binding.tvPlanName.text = planName

                            setFrame(page) // 화면 구성
                        } else {
                            Log.d(NETWORK, "onResponse: getFrame 실패")
                        }
                    }

                    override fun onFailure(call: Call<ShowMemTime>, t: Throwable) {
                        Log.i(NETWORK, "getFrame() Failure : ${t.message.toString()}")
                    }

                })
        }
    }

    private fun initInfo(amdtList: ArrayList<AvailableMemberDateTime>) {
        for (i in 0..6) {
            for (j in 0..23) {
                var possibleMember= PossibleMember(
                    amdtList[i].sectionAndAvailableTimes[j].time,
                    amdtList[i].sectionAndAvailableTimes[j].section,
                    amdtList[i].sectionAndAvailableTimes[j].memberNames,
                    amdtList[i].sectionAndAvailableTimes[j].memberIds
                )

                when (i) {
                    0 -> day1Info.add(possibleMember)
                    1 -> day2Info.add(possibleMember)
                    2 -> day3Info.add(possibleMember)
                    3 -> day4Info.add(possibleMember)
                    4 -> day5Info.add(possibleMember)
                    5 -> day6Info.add(possibleMember)
                    6 -> day7Info.add(possibleMember)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setColorNumText(endNumberForEachSection: EndNumberForEachSection) {
        Log.d(TAG, "setColorNumText() : $endNumberForEachSection")
        val section1 = endNumberForEachSection.section1
        val section2 = endNumberForEachSection.section2
        val section3 = endNumberForEachSection.section3

        if (section1 == 1) {
            binding.tvColorNum1.text = section1.toString()
        } else {
            binding.tvColorNum1.text = "1-$section1"
        }

        if (section2 == section1 + 1) {
            binding.tvColorNum2.text = section2.toString()
        } else {
            binding.tvColorNum2.text = "${section1 + 1}-$section2"
        }

        if (section3 == section2 + 1) {
            binding.tvColorNum3.text = section3.toString()
        } else {
            binding.tvColorNum3.text = "${section2 + 1}-$section3"
        }
    }

    fun setFrame(page: Int) { //날짜 입력 후, 표채우기로 넘김
        Log.d(TAG, "setFrame() : page = $page")
        var startDate = LocalDate.parse(planStartDate.replace(". ", "-"))
        when (page) {
            1 -> {
                binding.tvDate1.text = startDate.toString().substring(5).replace("-", ".")
                binding.tvDate2.text =
                    startDate.plusDays(1).toString().substring(5).replace("-", ".")
                binding.tvDate3.text =
                    startDate.plusDays(2).toString().substring(5).replace("-", ".")
                binding.tvDay1.text = getDay(startDate)
                binding.tvDay2.text = getDay(startDate.plusDays(1))
                binding.tvDay3.text = getDay(startDate.plusDays(2))
                binding.ivPrev.visibility = View.GONE
                binding.ivNext.visibility = View.VISIBLE
            }

            2 -> {
                binding.tvDate1.text =
                    startDate.plusDays(3).toString().substring(5).replace("-", ".")
                binding.tvDate2.text =
                    startDate.plusDays(4).toString().substring(5).replace("-", ".")
                binding.tvDate3.text =
                    startDate.plusDays(5).toString().substring(5).replace("-", ".")
                binding.tvDate2.visibility = View.VISIBLE
                binding.tvDate3.visibility = View.VISIBLE
                binding.tvDay1.text = getDay(startDate.plusDays(3))
                binding.tvDay2.text = getDay(startDate.plusDays(4))
                binding.tvDay3.text = getDay(startDate.plusDays(5))
                binding.tvDay2.visibility = View.VISIBLE
                binding.tvDay3.visibility = View.VISIBLE

                binding.ivPrev.visibility = View.VISIBLE
                binding.ivNext.visibility = View.VISIBLE
                for (i in 0..23) {
                    day2[i].visibility = View.VISIBLE
                    day3[i].visibility = View.VISIBLE
                }
            }

            3 -> {
                binding.tvDate1.text =
                    startDate.plusDays(6).toString().substring(5).replace("-", ".")
                binding.tvDate2.visibility = View.GONE
                binding.tvDate3.visibility = View.GONE
                binding.tvDay1.text = getDay(startDate.plusDays(6))
                binding.tvDay2.visibility = View.GONE
                binding.tvDay3.visibility = View.GONE

                binding.ivPrev.visibility = View.VISIBLE
                binding.ivNext.visibility = View.GONE
                for (i in 0..23) {
                    day2[i].visibility = View.GONE
                    day3[i].visibility = View.GONE
                }
            }
        }
        colorTimeTable(page)
    }

    private fun colorTimeTable(page: Int) {
        Log.d(TAG, "colorTimeTable() : page : $page")
        when (page) {
            1 -> for (i in 0..23) {
                updateClickListener(1, i, true)
                updateClickListener(2, i, true)
                updateClickListener(3, i, true)
                when (day1Info[i]!!.section) {
                    0 -> {
                        day1[i].setBackgroundResource(R.drawable.view_border)
                        updateClickListener(1, i, false)
                    }

                    1 -> day1[i].setBackgroundResource(R.drawable.view_border_section1)
                    2 -> day1[i].setBackgroundResource(R.drawable.view_border_section2)
                    3 -> day1[i].setBackgroundResource(R.drawable.view_border_section3)
                }
                when (day2Info[i]!!.section) {
                    0 -> {
                        day2[i].setBackgroundResource(R.drawable.view_border)
                        updateClickListener(2, i, false)
                    }

                    1 -> day2[i].setBackgroundResource(R.drawable.view_border_section1)
                    2 -> day2[i].setBackgroundResource(R.drawable.view_border_section2)
                    3 -> day2[i].setBackgroundResource(R.drawable.view_border_section3)
                }
                when (day3Info[i]!!.section) {
                    0 -> {
                        day3[i].setBackgroundResource(R.drawable.view_border)
                        updateClickListener(3, i, false)
                    }

                    1 -> day3[i].setBackgroundResource(R.drawable.view_border_section1)
                    2 -> day3[i].setBackgroundResource(R.drawable.view_border_section2)
                    3 -> day3[i].setBackgroundResource(R.drawable.view_border_section3)
                }
            }

            2 -> for (i in 0..23) {
                updateClickListener(1, i, true)
                updateClickListener(2, i, true)
                updateClickListener(3, i, true)
                when (day4Info[i]!!.section) {
                    0 -> {
                        day1[i].setBackgroundResource(R.drawable.view_border)
                        updateClickListener(1, i, false)
                    }

                    1 -> day1[i].setBackgroundResource(R.drawable.view_border_section1)
                    2 -> day1[i].setBackgroundResource(R.drawable.view_border_section2)
                    3 -> day1[i].setBackgroundResource(R.drawable.view_border_section3)
                }
                when (day5Info[i]!!.section) {
                    0 -> {
                        day2[i].setBackgroundResource(R.drawable.view_border)
                        updateClickListener(2, i, false)
                    }

                    1 -> day2[i].setBackgroundResource(R.drawable.view_border_section1)
                    2 -> day2[i].setBackgroundResource(R.drawable.view_border_section2)
                    3 -> day2[i].setBackgroundResource(R.drawable.view_border_section3)
                }
                when (day6Info[i]!!.section) {
                    0 -> {
                        day3[i].setBackgroundResource(R.drawable.view_border)
                        updateClickListener(3, i, false)
                    }

                    1 -> day3[i].setBackgroundResource(R.drawable.view_border_section1)
                    2 -> day3[i].setBackgroundResource(R.drawable.view_border_section2)
                    3 -> day3[i].setBackgroundResource(R.drawable.view_border_section3)
                }
            }

            3 -> for (i in 0..23) {
                updateClickListener(1, i, true)
                when (day7Info[i]!!.section) {
                    0 -> {
                        day1[i].setBackgroundResource(R.drawable.view_border)
                        updateClickListener(1, i, false)
                    }

                    1 -> day1[i].setBackgroundResource(R.drawable.view_border_section1)
                    2 -> day1[i].setBackgroundResource(R.drawable.view_border_section2)
                    3 -> day1[i].setBackgroundResource(R.drawable.view_border_section3)
                }
            }
        }
    }

    private fun updateClickListener(day: Int, i: Int, activation: Boolean) {
        if (!activation) { //마지막 인자값을 false로 하면 클릭기능을 비활성화
            when (day) {
                1 -> day1[i].setOnClickListener { }
                2 -> day2[i].setOnClickListener { }
                3 -> day3[i].setOnClickListener { }
            }
        } else { //클릭기능을 활성화
            when (day) {
                1 -> day1[i].setOnClickListener { getFixPlanDialog(1, i) }
                2 -> day2[i].setOnClickListener { getFixPlanDialog(2, i) }
                3 -> day3[i].setOnClickListener { getFixPlanDialog(3, i) }
            }
        }
    }

    private fun getFixPlanDialog(day: Int, i: Int) {
        var startDate = LocalDate.parse(planStartDate.replace(".", "-"))
        var fixedDate = ""
        var fixedTime = i
        var userId: ArrayList<Int> = arrayListOf()
        var userName: ArrayList<String> = arrayListOf()

        when (day) {
            1 -> {
                if (page == 1) {
                    fixedDate = startDate.toString()
                    userId = day1Info[i].memberIds
                    userName = day1Info[i].memberNames
                } else if (page == 2) {
                    fixedDate = startDate.plusDays(3).toString()
                    userId = day4Info[i].memberIds
                    userName = day4Info[i].memberNames
                } else if (page == 3) {
                    fixedDate = startDate.plusDays(6).toString()
                    userId = day7Info[i].memberIds
                    userName = day7Info[i].memberNames
                }
            }

            2 -> {
                if (page == 1) {
                    fixedDate = startDate.plusDays(1).toString()
                    userId = day2Info[i].memberIds
                    userName = day2Info[i].memberNames
                } else if (page == 2) {
                    fixedDate = startDate.plusDays(4).toString()
                    userId = day5Info[i].memberIds
                    userName = day5Info[i].memberNames
                }
            }

            3 -> {
                if (page == 1) {
                    fixedDate = startDate.plusDays(2).toString()
                    userId = day3Info[i].memberIds
                    userName = day3Info[i].memberNames
                } else if (page == 2) {
                    fixedDate = startDate.plusDays(5).toString()
                    userId = day6Info[i].memberIds
                    userName = day6Info[i].memberNames
                }
            }
        }

        var fixPlanDialog = FixPlanDialog()
        val bundle = Bundle()
        bundle.putInt("teamId", teamId)
        bundle.putString("planName", planName)
        bundle.putInt("planId", planId)
        bundle.putString("fixedDate", fixedDate)
        bundle.putInt("fixedTime", fixedTime)
        bundle.putIntegerArrayList("userId", userId)
        bundle.putStringArrayList("userName", userName)
        fixPlanDialog.arguments = bundle
        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_plan, fixPlanDialog)
            .commitAllowingStateLoss()
    }

}