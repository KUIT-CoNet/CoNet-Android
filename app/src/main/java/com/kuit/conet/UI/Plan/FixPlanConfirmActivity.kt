package com.kuit.conet.UI.Plan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.kuit.conet.Network.RetrofitClient
import com.kuit.conet.UI.GroupMain.GroupMainActivity
import com.kuit.conet.UI.Plan.detail.ParticipantAdapter
import com.kuit.conet.Utils.LIFECYCLE
import com.kuit.conet.Utils.NETWORK
import com.kuit.conet.Utils.TAG
import com.kuit.conet.databinding.ActivityFixPlanConfirmBinding
import com.kuit.conet.Utils.getRefreshToken
import com.kuit.conet.data.dto.response.plan.ResponseGetPlanDetail
import retrofit2.Call
import retrofit2.Response

class FixPlanConfirmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFixPlanConfirmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFixPlanConfirmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d(LIFECYCLE, "FixConfirmActivity - onCreate() called")

        binding.ivFixConfirmClose.setOnClickListener {
            finish()
        }

        val planId = intent.getIntExtra("planId", 0).toLong()
        if (planId == 0L) {
            Log.d(TAG, "FixConfirmActivity - planId가 제대로 넘어오지 않았습니다.")
            finish()
        }
        setFrame(planId)

        binding.cvFixConfirmBtn.setOnClickListener {
            val intent = Intent(this, GroupMainActivity::class.java)
            intent.putExtra("teamId", intent.getStringExtra("teamId"))
            startActivity(intent)
            finish()
        }
    }

    private fun setFrame(planId: Long) {
        RetrofitClient.planInstance.getPlanDetail(
            authorization = "Bearer ${getRefreshToken(this)}",
            planId = planId
        ).enqueue(object : retrofit2.Callback<ResponseGetPlanDetail> {
            override fun onResponse(
                call: Call<ResponseGetPlanDetail>,
                response: Response<ResponseGetPlanDetail>
            ) {
                if (response.isSuccessful) {
                    Log.d(NETWORK, "FixConfirmActivity - getPlanDetail() 실행결과 - 성공")
                    val data =
                        requireNotNull(response.body()) { "FixConfirmActivity - getPlanDetail() 결과 불러오기 실패" }
                    val result = data.result


                    binding.tvFixConfirmName.text = result.planName
                    binding.tvFixConfirmDate.text = result.date
                    binding.tvFixConfirmTime.text = result.time

                    val participantList = result.members
                    binding.rvFixConfirmParticipants.adapter = ParticipantAdapter(
                        context = this@FixPlanConfirmActivity,
                        membersList = participantList
                            .map { it.asMember() }
                            .toMutableList(),
                        option = 0
                    )
                } else {
                    Log.d(NETWORK, "FixConfirmActivity - getPlanDetail() 실행결과 - 안좋음")
                }
            }

            override fun onFailure(call: Call<ResponseGetPlanDetail>, t: Throwable) {
                Log.d(
                    NETWORK,
                    "FixConfirmActivity - getPlanDetail() 실행결과 - 실패\nbecause : $t"
                )
            }
        })
    }

}