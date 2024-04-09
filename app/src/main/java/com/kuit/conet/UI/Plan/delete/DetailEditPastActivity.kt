/*
package com.kuit.conet.UI.Plan.delete

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.kuit.conet.Network.*
import com.kuit.conet.R
import com.kuit.conet.UI.Plan.detail.ParticipantAdapter
import com.kuit.conet.UI.Plan.dialog.DateDialog
import com.kuit.conet.UI.Plan.dialog.TimeDialog
import com.kuit.conet.Utils.LIFECYCLE
import com.kuit.conet.Utils.NETWORK
import com.kuit.conet.Utils.TAG
import com.kuit.conet.Utils.getRefreshToken
import com.kuit.conet.Utils.intent.intentSerializable
import com.kuit.conet.data.dto.request.plan.RequestUpdateFixedPlan
import com.kuit.conet.data.dto.response.plan.ResponseUpdateFixedPlan
import com.kuit.conet.databinding.ActivityDetailEditPastBinding
import com.kuit.conet.domain.entity.member.Member
import retrofit2.Call
import retrofit2.Response
import com.kuit.conet.domain.entity.plan.PlanDetail

class DetailEditPastActivity
    : AppCompatActivity(), View.OnClickListener, TimeDialog.BottomSheetListener,
    DateDialog.OnButtonClickListener {

    private lateinit var binding: ActivityDetailEditPastBinding

    //    private lateinit var data: ResultGetPlanDetail
    private lateinit var planDetail: PlanDetail // 기존의 data 변수를 대체할 변수
    private lateinit var participantAdapter: ParticipantAdapter
    private lateinit var dateData: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailEditPastBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d(LIFECYCLE, "DetailEditPastActivity - onCreate() called")

        */
/*data = intent.getParcelableExtra<ResultGetPlanDetail>("data")!!
        Log.d(
            TAG, "DetailEditPastActivity - onCreat() intent로 데이터 받아오기 성공!\n" +
                    "data : $data"
        )*//*


        planDetail = requireNotNull(
            intent.intentSerializable(
                DetailPastActivity.INTENT_KEY,
                PlanDetail::class.java
            )
        ) { "DetailEditPastActivity's 직렬화 객체 이상!" }
        */
/* = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(
                DetailPastActivity.INTENT_KEY,
                PlanDetail::class.java
            ) ?: PlanDetail.empty
        } else {
            intent.getSerializableExtra(DetailPastActivity.INTENT_KEY) as PlanDetail
        }*//*

        Log.d(
            TAG,
            "DetailEditPastActivity - onCreate()\nintent로 데이터 받아오기\nplanDetail : $planDetail"
        )

        val date = planDetail.date.split(". ")
        dateData = arrayListOf(date[0], date[1], date[2])

        binding.nameTf.setText(planDetail.planName)
        binding.dateTf.setText(planDetail.date)
        binding.timeTf.setText(planDetail.time)

        binding.backIv.setOnClickListener(this)
        binding.finishTv.setOnClickListener(this)
        binding.dateTil.setOnClickListener(this)
        binding.dateTf.setOnClickListener(this)
        binding.timeTil.setOnClickListener(this)
        binding.timeTf.setOnClickListener(this)

        checkEnable()

        val members = planDetail.members.map { it.asMembers() } as ArrayList<Members>
        members.add(Members(0, "추가하기", ""))
        participantAdapter =
            ParticipantAdapter(
                context = this,
                membersList = members,
                option = 1,
            )
        participantAdapter.planId = planDetail.id
        participantAdapter.supportFragmentManager = supportFragmentManager
        binding.participantsRv.adapter = participantAdapter

        binding.nameTf.addTextChangedListener {
            checkEnable()
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.back_iv -> {
                finish()
            }

            R.id.finish_tv -> {
                if (binding.nameTf.text!!.isEmpty()) {
                    Log.d(
                        TAG, "DetailEditPastActivity - 완료버튼 클릭됨\n" +
                                "nameTf 빈값이라 안됨"
                    )
                } else if (binding.dateTf.text!!.isEmpty()) {
                    Log.d(
                        TAG, "DetailEditPastActivity - 완료버튼 클릭됨\n" +
                                "dateTf 빈값이라 안됨"
                    )
                } else if (binding.timeTf.text!!.isEmpty()) {
                    Log.d(
                        TAG, "DetailEditFPastActivity - 완료버튼 클릭됨\n" +
                                "timeTf 빈값이라 안됨"
                    )
                } else {
                    val members: ArrayList<Int> = participantAdapter.membersList
                        .filter { it.name != "추가하기" }
                        .map { it.id } as ArrayList<Int>
                    members.sort()

//                  TODO 서버에 내용 전달하기 -> /plan/update/fixed
                    RetrofitClient.planInstance.updateFixedPlan(
                        authorization = "Bearer ${getRefreshToken(this)}",
                        planDetail = RequestUpdateFixedPlan(
                            planId = planDetail.id,
                            planName = binding.nameTf.text.toString(),
                            date = binding.dateTf.text.toString(),
                            time = binding.timeTf.text.toString(),
                            membersIds = members.toList()
                        )
                    ).enqueue(object : retrofit2.Callback<ResponseUpdateFixedPlan> {
                        override fun onResponse(
                            call: Call<ResponseUpdateFixedPlan>,
                            response: Response<ResponseUpdateFixedPlan>
                        ) {
                            if (response.isSuccessful) {
                                Log.d(
                                    NETWORK,
                                    "DetailEditPastActivity - Retrofit updatePlanDetail() 실행 결과 - 성공"
                                )
                                finish()
                            } else {
                                Log.d(
                                    NETWORK,
                                    "DetailEditPastActivity - Retrofit updatePlanDetail() 실행 결과 - 안좋음"
                                )
                            }
                        }

                        override fun onFailure(call: Call<ResponseUpdateFixedPlan>, t: Throwable) {
                            Log.d(
                                NETWORK,
                                "DetailEditPastActivity - Retrofit updatePlanDetail() 실행 결과 - 실패\nbecause : $t"
                            )
                        }

                    })

                    finish()
                }
            }

            R.id.date_til, R.id.date_tf -> {
                val dateDialog = DateDialog(dateData[0], dateData[1], dateData[2])
                dateDialog.setOnButtonClickListener(this)
                dateDialog.show(supportFragmentManager, dateDialog.tag)
            }

            R.id.time_til, R.id.time_tf -> {
                val timeDialog = TimeDialog(planDetail.time)
                timeDialog.setBottomSheetListener(this)
                timeDialog.show(supportFragmentManager, timeDialog.tag)
            }
        }
    }

    override fun onAdditionalInfoSubmitted(info: String) {
        binding.timeTf.setText(info)
        checkEnable()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action === MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is TextInputEditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun checkEnable() {
        if (binding.nameTf.text!!.isNotEmpty() && binding.dateTf.text!!.isNotEmpty() && binding.timeTf.text!!.isNotEmpty()) {
            binding.finishTv.isEnabled = true
        }
    }

    override fun onButtonClicked(year: String, month: String, date: String) {
        binding.dateTf.setText("$year. $month. $date")
        dateData = arrayListOf(year, month, date)
        checkEnable()
    }
}*/
