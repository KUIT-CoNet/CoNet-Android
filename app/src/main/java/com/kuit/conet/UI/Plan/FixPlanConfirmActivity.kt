package com.kuit.conet.UI.Plan

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.kuit.conet.Network.ResponseGetPlanDetail
import com.kuit.conet.Network.RetrofitClient
import com.kuit.conet.UI.Plan.detail.ParticipantAdapter
import com.kuit.conet.Utils.NETWORK
import com.kuit.conet.databinding.ActivityFixPlanConfirmBinding
import retrofit2.Call
import retrofit2.Response

class FixPlanConfirmActivity : AppCompatActivity() {
    lateinit var binding: ActivityFixPlanConfirmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFixPlanConfirmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivFixConfirmClose.setOnClickListener {
            finish()
        }

        setFrame()

        binding.cvFixConfirmBtn.setOnClickListener {
            finish()
        }
    }

    private fun setFrame() {
        RetrofitClient.instance.getPlanDetail(intent.getIntExtra("planId",0))
            .enqueue(object : retrofit2.Callback<ResponseGetPlanDetail> {
                override fun onResponse(
                    call: Call<ResponseGetPlanDetail>,
                    response: Response<ResponseGetPlanDetail>
                ) {
                    if (response.isSuccessful) {
                        var data = response.body()!!.result
                        Log.d(
                            NETWORK, "FixConfirmActivity - Retrofit getPlanDetail() 실행결과 - 성공\n" +
                                    "data : $data"
                        )

                        binding.tvFixConfirmName.text = data.planName
                        binding.tvFixConfirmDate.text = data.date
                        binding.tvFixConfirmTime.text = data.time

                        val participantList = response.body()!!.result.members
                        binding.rvFixConfirmParticipants.adapter =
                            ParticipantAdapter(this@FixPlanConfirmActivity, participantList, 0)
                        binding.rvFixConfirmParticipants.layoutManager =
                            GridLayoutManager(this@FixPlanConfirmActivity, 2)
                    } else {
                        Log.d(NETWORK, "FixConfirmActivity - Retrofit getPlanDetail() 실행결과 - 안좋음")
                    }
                }

                override fun onFailure(call: Call<ResponseGetPlanDetail>, t: Throwable) {
                    Log.d(
                        NETWORK, "FixConfirmActivity - Retrofit getPlanDetail() 실행결과 - 실패\n" +
                                "t : $t"
                    )
                }
            })
    }
}