package com.kuit.conet.UI.Plan.detail

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.kuit.conet.Data.Group
import com.kuit.conet.Network.Members
import com.kuit.conet.Network.ResultGetPlanDetail
import com.kuit.conet.R
import com.kuit.conet.UI.Plan.dialog.DateDialog
import com.kuit.conet.UI.Plan.dialog.TimeDialog
import com.kuit.conet.Utils.TAG
import com.kuit.conet.databinding.ActivityDetailEditFixBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class DetailEditFixActivity
    : AppCompatActivity(), View.OnClickListener, TimeDialog.BottomSheetListener, DateDialog.OnButtonClickListener {

    private lateinit var binding: ActivityDetailEditFixBinding
    private lateinit var participantAdapter: ParticipantAdapter
    lateinit var data: ResultGetPlanDetail
    lateinit var dateData: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityDetailEditFixBinding.inflate(layoutInflater)
        setContentView(binding.root)

        data = intent.getParcelableExtra<ResultGetPlanDetail>("data")!!
        Log.d(
            TAG, "DetailEditPastActivity - onCreat() intent로 데이터 받아오기 성공!\n" +
                    "data : $data"
        )

        val date = data.date.split(". ")
        dateData = arrayListOf(date[0], date[1], date[2])

        binding.nameTf.setText(data.planName)
        binding.dateTf.setText(data.date)
        binding.timeTf.setText(data.time)

        checkEnable()

        binding.backIv.setOnClickListener(this)
        binding.finishTv.setOnClickListener(this)
        binding.dateTil.setOnClickListener(this)
        binding.dateTf.setOnClickListener(this)
        binding.timeTil.setOnClickListener(this)
        binding.timeTf.setOnClickListener(this)


        data.members.add(ResultGetPlanDetail.Members(0, "추가하기", ""))
        participantAdapter = ParticipantAdapter(this, data.members.map { it.asMembers() } as ArrayList<Members>, 1)
        participantAdapter.supportFragmentManager = supportFragmentManager
        binding.participantsRv.adapter = participantAdapter
        participantAdapter.groupId = Group.groupId
        binding.participantsRv.layoutManager = GridLayoutManager(this, 2)

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
                Log.d(TAG, "DetailEditFixActivity - 완료버튼 클릭됨")
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
                }
//                여기에 picture와 content도 검사해야 하지 않나?
                else {
//                    TODO 서버에 해당 내용 전송하기
                    val members: ArrayList<Int> = arrayListOf()

                    Log.d(
                        "내용", "DetailEditFixActivity 최종 확인 members\n" +
                                "members : ${participantAdapter.membersList}"
                    )

                    for (i in 0 until participantAdapter.membersList.count()) {
                        if (participantAdapter.membersList[i].name == "추가하기") continue
                        members.add(participantAdapter.membersList[i].id)
                    }
                    members.sort()
                    Log.d(
                        "내용", "DetailEditFixActivity 최종 확인 members\n" +
                                "members : ${members}"
                    )


                    val jsonString = "{\"planId\":${data.planId}," +
                            "\"planName\":\"${binding.nameTf.text}\"," +
                            "\"date\":\"${dateData[0]}-${dateData[1]}-${dateData[2]}\"," +
                            "\"time\":\"${binding.timeTf.text}\"," +
                            "\"members\":${members}," +
                            "\"isRegisteredToHistory\":false," +
                            "\"historyDescription\":null}"
                    val jsonList = jsonString.toRequestBody("application/json".toMediaTypeOrNull())

                    Log.d(TAG, "jsonList : $jsonString")

                    /*RetrofitClient.instance.updatePlanDetail(jsonList, null).enqueue(object: retrofit2.Callback<ResponseUpdatePlanDetail>{
                        override fun onResponse(
                            call: Call<ResponseUpdatePlanDetail>,
                            response: Response<ResponseUpdatePlanDetail>
                        ) {
                            if(response.isSuccessful){
                                Log.d(TAG, "DetailEditPastActivity - Retrofit updatePlanDetail() 실행 결과 - 성공")
                                finish()
                            }else {
                                Log.d(TAG, "DetailEditPastActivity - Retrofit updatePlanDetail() 실행 결과 - 안좋음\n" +
                                        "response : $response")
                            }
                        }
                        override fun onFailure(call: Call<ResponseUpdatePlanDetail>, t: Throwable) {
                            Log.d(TAG, "DetailEditPastActivity - Retrofit updatePlanDetail() 실행 결과 - 실패\n" +
                                    "t : $t")
                        }

                    })*/
                    finish()
                }
            }

            R.id.date_til, R.id.date_tf -> {
                val dateDialog = DateDialog(dateData[0], dateData[1], dateData[2]) //dateData
                dateDialog.setOnButtonClickListener(this)
                dateDialog.show(supportFragmentManager, dateDialog.tag)
            }

            R.id.time_til, R.id.time_tf -> {
                val timeDialog = TimeDialog(data.time)
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

    fun checkEnable() {
        if (binding.nameTf.text!!.isNotEmpty() && binding.dateTf.text!!.isNotEmpty() && binding.timeTf.text!!.isNotEmpty() && binding.nameTil.error == null) {
            binding.finishTv.isEnabled = true
        }
    }

    override fun onButtonClicked(year: String, month: String, date: String) {
        Log.d(
            TAG, "DetailEditFixActivity - onButtonClicked Date에서 날짜 받아오기\n" +
                    "year : $year, month : $month, date: $date"
        )
        binding.dateTf.setText("$year. $month. $date")
        dateData = arrayListOf(year, month, date)
        checkEnable()
    }
}