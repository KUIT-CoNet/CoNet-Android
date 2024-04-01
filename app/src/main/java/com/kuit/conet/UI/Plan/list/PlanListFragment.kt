package com.kuit.conet.UI.Plan.list

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.kuit.conet.Network.RetrofitClient
import com.kuit.conet.UI.Home.RecyclerView.ConfirmRecyclerAdapter
import com.kuit.conet.Utils.LIFECYCLE
import com.kuit.conet.Utils.NETWORK
import com.kuit.conet.Utils.getRefreshToken
import com.kuit.conet.data.dto.response.plan.ResponseGetSidebarPlan
import com.kuit.conet.databinding.FragmentPlanListBinding
import com.kuit.conet.domain.entity.plan.DecidedPlan
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class PlanListFragment : Fragment() {

    private var _binding: FragmentPlanListBinding? = null
    private val binding: FragmentPlanListBinding
        get() = requireNotNull(_binding) { "PlanListFragment's binding is null" }

    private val option: Int by lazy { requireNotNull(arguments?.getInt(PlanVPAdapter.BUNDLE_OPTION)) { "PlanListFragment's option is null" } }      // 0,else : 다가오는 약속, 1 : 지난 약속
    private val groupId: Int by lazy { requireNotNull(arguments?.getInt(PlanVPAdapter.BUNDLE_GROUP_ID)) { "PlanListFragment's groupId is null" } }
    private val initDeferred = CompletableDeferred<Unit>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanListBinding.inflate(inflater, container, false)
        Log.d(LIFECYCLE, "PlanListFragment - onCreateView() called")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(LIFECYCLE, "PlanListFragment - onViewCreated() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(LIFECYCLE, "PlanListFragment - onResume() called")

        lifecycleScope.launch {
            val plans = if (option == 1) {      // 지난 약속
                showSideConfirmplaninfo()
            } else {                            // 다가오는 약속
                showSideLastPlan()
            }

            binding.rvConfirmlist.adapter = ConfirmRecyclerAdapter(requireContext(), option, plans)
            initDeferred.complete(Unit)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
        Log.d(LIFECYCLE, "PlanListFragment - onDestroyView() called")
    }

    private suspend fun showSideConfirmplaninfo(): List<DecidedPlan> { // 지난 약속
        return suspendCoroutine { continuation2 ->
            RetrofitClient.planInstance.getSidebarPlan(
                authorization = "Bearer ${getRefreshToken(requireContext())}",
                teamId = groupId.toLong(),
                period = "past"
            ).enqueue(object : Callback<ResponseGetSidebarPlan> {
                override fun onResponse(
                    call: Call<ResponseGetSidebarPlan>,
                    response: Response<ResponseGetSidebarPlan>
                ) {
                    if (response.isSuccessful) {
                        Log.d(NETWORK, "PlanListFragment - getSidebarPlan() 실행 결과 - 성공")
                        val resp =
                            requireNotNull(response.body()) { "PlanListFragment - getSidebarPlan() 실행 결과 불러오기 실패" }
                        continuation2.resume(resp.result.plans.map { it.asDecidedPlan() })
                    } else {
                        Log.d(NETWORK, "PlanListFragment - getSidebarPlan() 실행 결과 - 안좋음")
                        continuation2.resumeWithException(Exception("Response not successful"))
                    }
                }

                override fun onFailure(call: Call<ResponseGetSidebarPlan>, t: Throwable) {
                    Log.d(NETWORK, "PlanListFragment - getSidebarPlan() 실행 결과 - 실패\nbecause : $t")
                    continuation2.resumeWithException(t)
                }
            })
        }

    }

    private suspend fun showSideLastPlan(): List<DecidedPlan> { // 다가오는 약속
        return suspendCoroutine { continuation2 ->
            RetrofitClient.planInstance.getSidebarPlan(
                authorization = "Bearer ${getRefreshToken(requireContext())}",
                teamId = groupId.toLong(),
                period = "oncoming"
            ).enqueue(object : Callback<ResponseGetSidebarPlan> {
                override fun onResponse(
                    call: Call<ResponseGetSidebarPlan>,
                    response: Response<ResponseGetSidebarPlan>
                ) {
                    if (response.isSuccessful) {
                        Log.d(NETWORK, "PlanListFragment - getSidebarPlan() 실행 결과 - 성공")
                        val resp =
                            requireNotNull(response.body()) { "PlanListFragment - getSidebarPlan() 실행 결과 불러오기 실패" }
                        continuation2.resume(resp.result.plans.map { it.asDecidedPlan() })
                    } else {
                        Log.d(NETWORK, "PlanListFragment - getSidebarPlan() 실행 결과 - 안좋음")
                        continuation2.resumeWithException(Exception("Response not successful"))
                    }
                }

                override fun onFailure(call: Call<ResponseGetSidebarPlan>, t: Throwable) {
                    Log.d(NETWORK, "PlanListFragment - getSidebarPlan() 실행 결과 - 실패\nbecause : $t")
                    continuation2.resumeWithException(t)
                }
            })
        }
    }
}