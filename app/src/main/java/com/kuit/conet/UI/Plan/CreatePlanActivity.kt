package com.kuit.conet.UI.Plan

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.kuit.conet.Network.*
import com.kuit.conet.R
import com.kuit.conet.UI.Plan.dialog.CalenderDialog
import com.kuit.conet.UI.application.CoNetApplication
import com.kuit.conet.Utils.NETWORK
import com.kuit.conet.databinding.ActivityCreatePlanBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.suspendCoroutine

class CreatePlanActivity : AppCompatActivity() {
    lateinit var binding: ActivityCreatePlanBinding
    var isNameInput = false
    var isDateInput = false

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCreatePlanBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val teamId = intent.getIntExtra("GroupId", -1)

        binding.tvCreatePlanNameLength.text = "0/20"

        binding.ivCreateBackBtn.setOnClickListener {
            finish()
        }

        binding.etCreatePlanName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("texting", "입력전")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("texting", "입력중")
                binding.tvCreatePlanNameLength.text =
                    getString(R.string.plan_name_length, p0!!.length)
                binding.vCreateUnderline2.visibility = View.VISIBLE
                binding.vCreateUnderline1.visibility = View.GONE
            }

            override fun afterTextChanged(p0: Editable?) {
                Log.d("texting", "입력끝")
                if (p0!!.isNotEmpty()) {
                    binding.ivCreateTextCancel.visibility = View.VISIBLE
                    isNameInput = true
                } else {
                    binding.ivCreateTextCancel.visibility = View.GONE
                    isNameInput = false
                }
                binding.vCreateUnderline2.visibility = View.GONE
                binding.vCreateUnderline1.visibility = View.VISIBLE
            }
        })

        binding.ivCreateTextCancel.setOnClickListener {
            binding.etCreatePlanName.text = null
            isNameInput = false
        }

        val calenderDialog = CalenderDialog()
        binding.tvCreatePlanDate.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.to_up, R.anim.from_down)
                .replace(R.id.fl_create_plan, calenderDialog)
                .commitAllowingStateLoss()

            binding.ivCreateCalendar.setColorFilter(R.color.black)
            binding.tvCreatePlanDate.setTextColor(R.color.black)
        }

        calenderDialog.setOnButtonClickListener(object : CalenderDialog.OnButtonClickListener {
            override fun onButtonClicked(date: String) {
                binding.tvCreatePlanDate.text = date
                binding.ivCreateCalendar.setColorFilter(R.color.gray300)
                //binding.ivCreateCalendar.setImageResource(R.drawable.calendar_gray)
                binding.tvCreatePlanDate.setTextColor(R.color.black)
                isDateInput = true

                binding.clCreateDoneBtn.setBackgroundResource(R.color.purpleMain)
                binding.tvCreateDoneBtn.text = "다음"

                supportFragmentManager.beginTransaction()
                    .remove(calenderDialog)
                    .commit()
            }
        })

        binding.cvCreateDoneBtn.setOnClickListener {
            if (isDateInput && isNameInput) {
                val coroutineScope = CoroutineScope(Dispatchers.Main)
                val intent = Intent(this, PlanTimeActivity::class.java)
                coroutineScope.launch {
                    makePlan(
                        binding.etCreatePlanName.text!!.toString(),
                        teamId,
                        binding.tvCreatePlanDate.text!!.toString(),
                        intent
                    )
                }
            }
        }
    }

    private suspend fun makePlan(
        planName: String,
        teamId: Int,
        planStartDate: String,
        intent: Intent
    ) {
        val accessToken = CoNetApplication.getInstance().getDataStore().accessToken.first()

        return suspendCoroutine {
            val planStartDate = planStartDate.replace(".", "-")
            val responsePlan = getRetrofit().create(RetrofitInterface::class.java)
            responsePlan.makePlan(
                "Bearer $accessToken",
                MakePlanInfo(
                    teamId,
                    planName,
                    planStartDate
                )
            ).enqueue(object :
                Callback<ResponseMakePlan> {
                override fun onResponse(
                    call: Call<ResponseMakePlan>,
                    response: Response<ResponseMakePlan>
                ) {
                    if (response.isSuccessful) {
                        val resp = response.body()// 성공했을 경우 response body불러오기
                        Log.d(NETWORK, "Success\n$resp")

                        val planId = resp!!.result.planId
                        intent.putExtra("planName", binding.etCreatePlanName.text.toString())
                        intent.putExtra("planStartDate", binding.tvCreatePlanDate.text.toString())
                        intent.putExtra("teamId", teamId)
                        intent.putExtra("planId", planId)
                        startActivity(intent)
                        finish()
                    } else {

                    }
                }

                override fun onFailure(call: Call<ResponseMakePlan>, t: Throwable) {
                    Log.d(NETWORK, t.message.toString())
                }
            })
        }
    }
}