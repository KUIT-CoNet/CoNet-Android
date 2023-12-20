package com.kuit.conet.UI.Plan

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.calenderdialog.CalenderDialog
import com.kuit.conet.Network.*
import com.kuit.conet.R
import com.kuit.conet.databinding.ActivityCreatePlanBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.suspendCoroutine

class CreatePlanActivity() : AppCompatActivity() {
    lateinit var binding : ActivityCreatePlanBinding
    var isNameInput = false
    var isDateInput = false

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCreatePlanBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val groupId = intent.getIntExtra("GroupId", -1)

        binding.planNameLength.text = "0/20"

        binding.btnBackIv.setOnClickListener {
            finish()
        }

        binding.planNameEt.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("texting","입력전")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("texting","입력중")
                binding.planNameLength.text=getString(R.string.plan_name_length, p0!!.length)
                binding.planUnderline2.visibility = View.VISIBLE
                binding.planUnderline1.visibility = View.GONE
            }

            override fun afterTextChanged(p0: Editable?) {
                Log.d("texting","입력끝")
                if(p0!!.isNotEmpty()){
                    binding.btnTextCancle.visibility = View.VISIBLE
                    isNameInput=true
                }
                else {
                    binding.btnTextCancle.visibility = View.GONE
                    isNameInput=false
                }
                binding.planUnderline2.visibility = View.GONE
                binding.planUnderline1.visibility = View.VISIBLE
            }
        })

        binding.btnTextCancle.setOnClickListener {
            binding.planNameEt.text = null
            isNameInput=false
        }

        val calenderDialog = CalenderDialog()
        binding.tvPlanDate.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.to_up, R.anim.from_down)
                .replace(R.id.fl_calendar, calenderDialog)
                .commitAllowingStateLoss()

            binding.ivCalendar.setImageResource(R.drawable.calendar_black)
            binding.tvPlanDate.setTextColor(R.color.black)
        }

        calenderDialog.setOnButtonClickListener(object : CalenderDialog.OnButtonClickListener{
            override fun onButtonClicked(date: String) {
                binding.tvPlanDate.text = date
                binding.ivCalendar.setImageResource(R.drawable.calendar_gray)
                binding.tvPlanDate.setTextColor(R.color.black)
                isDateInput=true

                binding.btnCl.setBackgroundResource(R.color.purpleMain)
                binding.btnText.text = "다음"

                supportFragmentManager.beginTransaction()
                    .remove(calenderDialog)
                    .commit()
            }
        })

        binding.btnCv.setOnClickListener {
            if (isDateInput && isNameInput){
                val coroutineScope = CoroutineScope(Dispatchers.Main)
                val intent = Intent(this, PlanTimeActivity::class.java)
                coroutineScope.launch {
                    MakePlan(binding.planNameEt.text!!.toString(), groupId, binding.tvPlanDate.text!!.toString(), intent)
                }
            }
        }
    }

    suspend fun MakePlan(planName : String, groupId : Int, planStartPeriod : String, intent : Intent){
        return suspendCoroutine { continuation2->
            val planStartPeriod = planStartPeriod.replace(".", "-")
            val responsePlan = getRetrofit().create(RetrofitInterface::class.java) // 이걸 따로 빼내는 방법....
            responsePlan.MakePlan(
                MakePlanInfo(
                    groupId,
                    planName,
                    planStartPeriod
                )
            ).enqueue(object :
                Callback<ResponseMakePlan> { // 서버와 비동기적으로 데이터 주고받을 수 있는 방법 enqueue사용
                override fun onResponse( // 통신에 성공했을 경우
                    call: Call<ResponseMakePlan>,
                    response: Response<ResponseMakePlan>
                ) {
                    if (response.isSuccessful) {
                        val resp = response.body()// 성공했을 경우 response body불러오기
                        Log.d("API-CREATE/SUCCESS", resp.toString())
                        Log.d("성공!","success")

                        val planId =  resp!!.result.planId
                        intent.putExtra("planName", binding.planNameEt.text.toString())
                        intent.putExtra("planStartPeriod",binding.tvPlanDate.text.toString())
                        intent.putExtra("GroupId",groupId)
                        intent.putExtra("planId",planId)
                        startActivity(intent)
                        Log.d("PlanTimeActivity 실행 완료", "startDate :"+binding.tvPlanDate.text.toString())
                        finish()
                    }
                    else{

                    }
                }

                override fun onFailure(call: Call<ResponseMakePlan>, t: Throwable) { // 통신에 실패했을 경우
                    Log.d("API-CREATE/FAILURE", t.message.toString()) // 실패한 이유 메세지 출력
                }

            })

        }

    }


}