package com.kuit.conet.UI.Plan

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.kuit.conet.Network.ResponseUpdateWaiting
import com.kuit.conet.Network.RetrofitInterface
import com.kuit.conet.Network.UpdateWaiting
import com.kuit.conet.Network.getRetrofit
import com.kuit.conet.R
import com.kuit.conet.databinding.ActivityEditPlanBinding
import com.kuit.conet.getRefreshToken
import retrofit2.Call
import retrofit2.Response

class EditPlanActivity: AppCompatActivity() {
    lateinit var binding : ActivityEditPlanBinding
    var isNameChange = false
    var planId =0
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityEditPlanBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val planName = intent.getStringExtra("planName")
        val planDate = intent.getStringExtra("planDate")
        planId = intent.getIntExtra("planId", 0)

        binding.etEditPlanName.setText(planName.toString())
        binding.tvEditNameLength.text = "${planName!!.length}/20"
        binding.tvEditPlanDate.text = planDate.toString().replace("-",".")

        binding.ivEditBackBtn.setOnClickListener {
            finish()
        }

        binding.etEditPlanName.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("texting","입력전")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("texting","입력중")
                binding.tvEditNameLength.text=getString(R.string.plan_name_length, p0!!.length)
                binding.llEditUnderline1.setBackgroundResource(R.color.purpleMain)
                binding.ivEditTextCancel.visibility = View.VISIBLE
            }

            override fun afterTextChanged(p0: Editable?) {
                Log.d("texting","입력끝")
                if(p0!!.isNotEmpty()){
                    binding.ivEditTextCancel.visibility = View.VISIBLE
                    //원래 이름과 같은지 판별
                    isNameChange = !p0.toString().equals(planName)
                }
                else {
                    binding.ivEditTextCancel.visibility = View.GONE
                    isNameChange=false
                }
                binding.llEditUnderline1.setBackgroundResource(R.color.gray200)
                changeBtn(isNameChange, planId)
            }

        })

        binding.ivEditTextCancel.setOnClickListener {
            binding.etEditPlanName.text = null
        }
    }

    fun changeBtn(isNameChange: Boolean, planId: Int){
        if(isNameChange){
            binding.clEditDoneBtn.setBackgroundResource(R.color.purpleMain)

            binding.cvEditDoneBtn.setOnClickListener {
                updateWaiting()
                finish()
            }
        }
        else {
            binding.clEditDoneBtn.setBackgroundResource(R.color.gray200)
            binding.cvEditDoneBtn.setOnClickListener { }
        }
    }

    private fun updateWaiting() {
        val updateWaitingService = getRetrofit().create(RetrofitInterface::class.java)
        val refreshToken = getRefreshToken(this)
        val authHeader = "Bearer $refreshToken"
        updateWaitingService.UpdateWaiting(
            authHeader,
            waitingInfo()
        ).enqueue(object : retrofit2.Callback<ResponseUpdateWaiting>{
                override fun onResponse(
                    call: Call<ResponseUpdateWaiting>,
                    response: Response<ResponseUpdateWaiting>
                ) {
                    if (response.isSuccessful){
                        val resp = response.body()
                        Log.d("API-updateWaiting/Success", resp.toString())
                    }
                }

                override fun onFailure(call: Call<ResponseUpdateWaiting>, t: Throwable) {
                    Log.d("API-updateWaiting/Fail", t.message.toString())
                }
            })
    }

    private fun waitingInfo(): UpdateWaiting {
        return UpdateWaiting(
            planId = intent.getIntExtra("planId",0),
            planName = binding.etEditPlanName.text.toString()
        )
    }
}