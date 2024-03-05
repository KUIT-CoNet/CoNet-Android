package com.kuit.conet.UI.Plan

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kuit.conet.Network.FixPlan
import com.kuit.conet.Network.ResponseFixPlan
import com.kuit.conet.Network.RetrofitInterface
import com.kuit.conet.Network.getRetrofit
import com.kuit.conet.Utils.NETWORK
import com.kuit.conet.databinding.DialogFixPlanBinding
import com.kuit.conet.Utils.getRefreshToken
import retrofit2.Call
import retrofit2.Response
import kotlin.collections.ArrayList

class FixPlanDialog : Fragment() {
    lateinit var binding: DialogFixPlanBinding

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogFixPlanBinding.inflate(layoutInflater, container, false)

        val teamId = requireArguments().getInt("teamId")
        val planName = requireArguments().getString("planName")
        val planId = requireArguments().getInt("planId")
        val fixedDate = requireArguments().getString("fixedDate")
        val fixedTime = requireArguments().getInt("fixedTime")
        val memberIds = requireArguments().getIntegerArrayList("memberIds")
        val userName = requireArguments().getStringArrayList("userName")


        val year = fixedDate?.substring(0, 4)
        val month = fixedDate?.substring(5, 7)
        val date = fixedDate?.substring(8, 10)

        val changeDate = "${year}년 ${month}월 ${date}일 "

        binding.tvTime.text = "$fixedTime:00"
        binding.tvDialogFixDate.text = changeDate

        var userNames = ""
        for (i in 0 until userName!!.size) userNames = userNames + " " + userName[i].toString()
        binding.tvDialogFixParticipants.text = userNames

        binding.tvDialogFixCancel.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .remove(this)
                .commit()
        }

        binding.tvDialogFixConfirm.setOnClickListener {
            postFixPlan(fixPlan(planId, fixedDate!!, fixedTime, memberIds!!)) //api 연동
            parentFragmentManager.beginTransaction()
                .remove(this)
                .commit()

            val intent = Intent(requireContext(), FixPlanConfirmActivity::class.java)
            intent.putExtra("planId", planId)
            intent.putExtra("planName", planName)
            intent.putExtra("teamId", teamId)
            intent.putExtra("fixedDate", fixedDate)
            intent.putExtra("fixedTime", fixedTime)
            startActivity(intent)
        }

        return binding.root
    }

    private fun fixPlan(
        planId: Int,
        fixedDate: String,
        fixedTime: Int,
        memberIds: ArrayList<Int>
    ): FixPlan {
        return FixPlan(
            planId = planId,
            fixedDate = fixedDate,
            fixedTime = fixedTime,
            memberIds = memberIds
        )
    }

    private fun postFixPlan(fixPlan: FixPlan) {
        val fixPlanService = getRetrofit().create(RetrofitInterface::class.java)
        val refreshToken = getRefreshToken(requireContext())
        val authHeader = "Bearer $refreshToken"
        fixPlanService.FixPlan(
            authHeader,
            fixPlan
        ).enqueue(object : retrofit2.Callback<ResponseFixPlan> {
            override fun onResponse(
                call: Call<ResponseFixPlan>,
                response: Response<ResponseFixPlan>
            ) {
                if (response.isSuccessful) {
                    val resp = response.body()
                    Log.d(NETWORK, "FixPlan api Successful\n$resp")
                }
            }

            override fun onFailure(call: Call<ResponseFixPlan>, t: Throwable) {
                Log.d(NETWORK, "FixPlan api Failure\n${t.message}")
            }

        })
    }
}