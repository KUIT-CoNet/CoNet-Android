package com.kuit.conet.UI.Plan

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.kuit.conet.Network.ResponseDeletePlan
import com.kuit.conet.Network.RetrofitInterface
import com.kuit.conet.Network.getRetrofit
import com.kuit.conet.UI.application.CoNetApplication
import com.kuit.conet.Utils.NETWORK
import com.kuit.conet.databinding.DialogDeletePlanBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

class DeletePlanDialog : Fragment() {

    private lateinit var binding: DialogDeletePlanBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogDeletePlanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val planId = requireArguments().getInt("planId")

        binding.tvDialogDeleteBtnCancel.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .remove(this)
                .commit()
        }

        binding.tvDialogDeleteBtnDelete.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val bearerAccessToken =
                    CoNetApplication.getInstance().getDataStore().bearerAccessToken.first()
                deletePlan(bearerAccessToken, planId)
            }

            requireActivity().finish()
        }
    }

    private fun deletePlan(accessToken: String, planId: Int) {
        val deletePlanService = getRetrofit().create(RetrofitInterface::class.java)

        deletePlanService.deletePlan(
            authorization = accessToken,
            planId = planId
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