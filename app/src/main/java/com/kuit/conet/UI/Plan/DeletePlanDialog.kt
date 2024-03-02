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
    lateinit var groupDetail: GroupData

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

        binding.tvDelete.setOnClickListener {
            val intent = Intent(requireContext(), GroupMainActivity::class.java)
            deletePlan(planId)
            //그룹 상세조회
            intent.putExtra(GroupAdapter.INTENT_GROUP, groupDetail)
            startActivity(intent)
            parentFragmentManager.beginTransaction().remove(this).commit()
        }

        return binding.root
    }

    private fun deletePlan(planId: Int) {
        val deletePlanService = getRetrofit().create(RetrofitInterface::class.java)
        val authHeader = "Bearer ${getRefreshToken(requireContext())}"
        deletePlanService.deletePlan(
            authHeader,
            planId
        ).enqueue(object : retrofit2.Callback<ResponseDeletePlan> {
            override fun onResponse(
                call: Call<ResponseDeletePlan>,
                response: Response<ResponseDeletePlan>
            ) {
                if (response.isSuccessful) {
                    val resp = response.body()
                    Log.d("API-deletePlan/Success", resp.toString())
                }
            }

            override fun onFailure(call: Call<ResponseDeletePlan>, t: Throwable) {
                Log.d("API-eletePlan/Fail", t.message.toString())
            }

        })
    }

    private fun getGroupDetail(teamId: Int) {
        val getGroupDataService = getRetrofit().create(RetrofitInterface::class.java)
        val authHeader = "Bearer ${getRefreshToken(requireContext())}"

        getGroupDataService.getGroupDetail(
            authHeader,
            teamId
        ).enqueue(object : retrofit2.Callback<ResponseGetGroupDetail> {
            override fun onResponse(
                call: Call<ResponseGetGroupDetail>,
                response: Response<ResponseGetGroupDetail>
            ) {
                if (response.isSuccessful) {
                    val resp = response.body()
                    Log.i(NETWORK, "onResponse: Success\n$resp")
                    if (resp != null) {
                        groupDetail = GroupData(resp.result.groupId, resp.result.groupName, resp.result.groupImgUrl, resp.result.groupMemberCount, resp.result.bookmark)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseGetGroupDetail>, t: Throwable) {
                Log.i(NETWORK, "onResponse: Failure\n${t.message}")
            }
        })
    }
}