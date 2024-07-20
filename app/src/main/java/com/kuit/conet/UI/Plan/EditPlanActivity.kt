package com.kuit.conet.UI.Plan

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kuit.conet.Network.ResponseUpdateWaiting
import com.kuit.conet.Network.RetrofitInterface
import com.kuit.conet.Network.UpdateWaiting
import com.kuit.conet.Network.getRetrofit
import com.kuit.conet.R
import com.kuit.conet.UI.application.CoNetApplication
import com.kuit.conet.databinding.ActivityEditPlanBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

class EditPlanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditPlanBinding
    var isNameChange = false
    var planId = 0

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
        binding.tvEditPlanDate.text = planDate.toString().replace("-", ".")

        binding.ivEditBackBtn.setOnClickListener {
            finish()
        }

        binding.etEditPlanName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("texting", "입력전")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("texting", "입력중")
                binding.tvEditNameLength.text = getString(R.string.plan_name_length, p0!!.length)
                binding.llEditUnderline1.setBackgroundResource(R.color.purpleMain)
                binding.ivEditTextCancel.visibility = View.VISIBLE
            }

            override fun afterTextChanged(p0: Editable?) {
                Log.d("texting", "입력끝")
                if (p0!!.isNotEmpty()) {
                    binding.ivEditTextCancel.visibility = View.VISIBLE
                    isNameChange = p0.toString() != planName //원래 이름과 같은지 판별
                } else {
                    binding.ivEditTextCancel.visibility = View.GONE
                    isNameChange = false
                }
                binding.llEditUnderline1.setBackgroundResource(R.color.gray200)
                changeBtn(isNameChange, planId)
            }

        })

        binding.ivEditTextCancel.setOnClickListener {
            binding.etEditPlanName.text = null
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if (view != null) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                view.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    fun changeBtn(isNameChange: Boolean, planId: Int) {
        if (isNameChange) {
            binding.clEditDoneBtn.setBackgroundResource(R.color.purpleMain)
            binding.cvEditDoneBtn.setOnClickListener {
                lifecycleScope.launch {
                    updateWaiting()
                    finish()
                }
            }
        } else {
            binding.clEditDoneBtn.setBackgroundResource(R.color.gray200)
            binding.cvEditDoneBtn.setOnClickListener { }
        }
    }

    private suspend fun updateWaiting() {
        val updateWaitingService = getRetrofit().create(RetrofitInterface::class.java)
        val bearerAccessToken =
            CoNetApplication.getInstance().getDataStore().bearerAccessToken.first()

        updateWaitingService.UpdateWaiting(
            authorization = bearerAccessToken,
            updateWaiting = waitingInfo()
        ).enqueue(object : retrofit2.Callback<ResponseUpdateWaiting> {
            override fun onResponse(
                call: Call<ResponseUpdateWaiting>,
                response: Response<ResponseUpdateWaiting>
            ) {
                if (response.isSuccessful) {
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
            planId = intent.getIntExtra("planId", 0),
            planName = binding.etEditPlanName.text.toString()
        )
    }
}