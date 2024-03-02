package com.kuit.conet.UI.Plan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kuit.conet.Network.ResponseDeletePlan
import com.kuit.conet.Network.ResponseUpdateWaiting
import com.kuit.conet.Network.RetrofitInterface
import com.kuit.conet.Network.getRetrofit
import com.kuit.conet.UI.GroupMain.GroupMainActivity
import com.kuit.conet.databinding.DialogDeletePlanBinding
import com.kuit.conet.getRefreshToken
import retrofit2.Call
import retrofit2.Response

class DeletePlanDialog : Fragment() {
    lateinit var binding : DialogDeletePlanBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogDeletePlanBinding.inflate(inflater, container, false)

        var planId = requireArguments().getInt("planId")
        var teamId = requireArguments().getInt("teamId")

        binding.tvCancel.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .remove(this)
                .commit()
        }

        binding.tvDelete.setOnClickListener{
            val intent = Intent(requireContext(), GroupMainActivity::class.java)
            deletePlan(planId)
            intent.putExtra("teamId", teamId)
            startActivity(intent)
            parentFragmentManager.beginTransaction().remove(this).commit()
        }

        return binding.root
    }

    private fun deletePlan(planId: Int) {
        val deletePlanService = getRetrofit().create(RetrofitInterface::class.java)
        val refreshToken = getRefreshToken(requireContext())
        val authHeader = "Bearer $refreshToken"
        deletePlanService.deletePlan(
            authHeader,
            planId
        ).enqueue(object : retrofit2.Callback<ResponseDeletePlan>{
                override fun onResponse(
                    call: Call<ResponseDeletePlan>,
                    response: Response<ResponseDeletePlan>
                ) {
                    if (response.isSuccessful){
                        val resp = response.body()
                        Log.d("API-deletePlan/Success", resp.toString())
                    }
                }

                override fun onFailure(call: Call<ResponseDeletePlan>, t: Throwable) {
                    Log.d("API-eletePlan/Fail", t.message.toString())
                }

            })
    }
}