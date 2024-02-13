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
import com.kuit.conet.R
import com.kuit.conet.databinding.DialogFixPlanBinding
import org.threeten.bp.LocalDate
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

        var groupId = requireArguments().getInt("groupId")
        var planName = requireArguments().getString("planName")
        val planId = requireArguments().getInt("planId")
        val fixedDate = requireArguments().getString("fixedDate")
        val fixedTime = requireArguments().getInt("fixedTime")
        val userId = requireArguments().getIntegerArrayList("userId")
        val userName = requireArguments().getStringArrayList("userName")


        val year = fixedDate!!.substring(0, 4)
        val month = fixedDate!!.substring(5, 7)
        val date = fixedDate!!.substring(8, 10)
        //var Date = LocalDate.parse(fixedDate!!.replace("-","."))

        var changeDate = "${year}년 ${month}월 ${date}일 "
        //var changeDay  = "${getDay(Date)}요일"

        binding.tvTime.text = "$fixedTime:00"
        binding.tvDate.text = changeDate

        var userNames = ""
        for (i in 0 until userName!!.size) userNames = userNames + " " + userName[i].toString()
        binding.tvParticipants.text = userNames


        binding.tvCancel.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .remove(this)
                .commit()
        }

        binding.tvConfirm.setOnClickListener {
            postFixPlan(fixPlan(planId, fixedDate!!, fixedTime, userId!!)) //api 연동
            parentFragmentManager.beginTransaction()
                .remove(this)
                .commit()

            val intent = Intent(requireContext(), FixPlanConfirmActivity::class.java)
            intent.putExtra("planId", planId)
            intent.putExtra("planName", planName)
            intent.putExtra("groupId", groupId)
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
        userId: ArrayList<Int>
    ): FixPlan {
        return FixPlan(
            planId = planId,
            fixed_date = fixedDate,
            fixed_time = fixedTime,
            userId = userId
        )
    }

    private fun postFixPlan(fixPlan: FixPlan) {
        val fixPlanService = getRetrofit().create(RetrofitInterface::class.java)
        fixPlanService.FixPlan(fixPlan).enqueue(object : retrofit2.Callback<ResponseFixPlan> {
            override fun onResponse(
                call: Call<ResponseFixPlan>,
                response: Response<ResponseFixPlan>
            ) {
                if (response.isSuccessful) {
                    val resp = response.body()
                    Log.d("FixPlan/SUCCESS", resp.toString())
                }
            }

            override fun onFailure(call: Call<ResponseFixPlan>, t: Throwable) {
                Log.d("FixPlan/FAILURE", t.message.toString())
            }

        })
    }
}