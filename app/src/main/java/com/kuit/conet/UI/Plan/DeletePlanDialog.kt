package com.kuit.conet.UI.Plan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kuit.conet.Network.ResponseDeletePlan
import com.kuit.conet.Network.ResponseGetGroupDetail
import com.kuit.conet.Network.ResponseUpdateWaiting
import com.kuit.conet.Network.RetrofitInterface
import com.kuit.conet.Network.getRetrofit
import com.kuit.conet.UI.Group.GroupAdapter
import com.kuit.conet.UI.Group.GroupData
import com.kuit.conet.UI.GroupMain.GroupMainActivity
import com.kuit.conet.Utils.NETWORK
import com.kuit.conet.Utils.TAG
import com.kuit.conet.databinding.DialogDeletePlanBinding
import com.kuit.conet.getRefreshToken
import retrofit2.Call
import retrofit2.Response
import retrofit2.create

class DeletePlanDialog : Fragment() {
    lateinit var binding: DialogDeletePlanBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogDeletePlanBinding.inflate(inflater, container, false)

        val planId = requireArguments().getInt("planId")

        binding.tvDialogDeleteBtnCancel.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .remove(this)
                .commit()
        }

        binding.tvDialogDeleteBtnDelete.setOnClickListener {
            deletePlan(planId)
            requireActivity().finish()
        }

        return binding.root
    }

    private fun deletePlan(planId: Int) {
        val deletePlanService = getRetrofit().create(RetrofitInterface::class.java)
        deletePlanService.deletePlan(
            "Bearer ${getRefreshToken(requireContext())}",
            planId
        ).enqueue(object : retrofit2.Callback<ResponseDeletePlan> {
            override fun onResponse(
                call: Call<ResponseDeletePlan>,
                response: Response<ResponseDeletePlan>
            ) {
                if (response.isSuccessful) {
                    val resp = response.body()
                    Log.d(NETWORK, "Success\n$resp")
                }
            }

            override fun onFailure(call: Call<ResponseDeletePlan>, t: Throwable) {
                Log.d(NETWORK, "Failure\n" + t.message.toString())
            }

        })
    }
}