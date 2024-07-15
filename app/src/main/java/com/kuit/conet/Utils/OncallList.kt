package com.kuit.conet.Utils

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.kuit.conet.Network.RetrofitClient
import com.kuit.conet.UI.Home.RecyclerView.AllTodoRecyclerAdapter
import com.kuit.conet.UI.application.CoNetApplication
import com.kuit.conet.data.dto.response.home.ResponseGetWaitingPlan
import com.kuit.conet.data.dto.response.plan.ResponseGetGroupWaitingPlan
import com.kuit.conet.databinding.FragmentOncallBinding
import com.kuit.conet.domain.entity.plan.UndecidedPlan
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class OncallList(
    private val groupId: Int,   // -1 : HomeFragment, 1 이상(groupId) : GroupMainActivity
    private val option: Int     // 1 : HomeFragment, 2 : GroupMainActivity
) : Fragment() {

    private var _binding: FragmentOncallBinding? = null
    private val binding: FragmentOncallBinding
        get() = requireNotNull(_binding) { "OncallList's binding is null" }

    private lateinit var allTodoRecyclerAdapter: AllTodoRecyclerAdapter
    private val initDeferred = CompletableDeferred<Unit>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOncallBinding.inflate(inflater, container, false)
        Log.d(LIFECYCLE, "OncallList - onCreateView() called")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(LIFECYCLE, "OncallList - onViewCreated() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(LIFECYCLE, "OncallList - onResume() called")

        viewLifecycleOwner.lifecycleScope.launch {
            val oncall = if (groupId < 0) {
                showOncall()
            } else {
                showGroupOncall()
            }

            initAllRecycler(oncall)
            initDeferred.complete(Unit)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
        Log.d(LIFECYCLE, "OncallList - onDestroyView() called")
    }

    private suspend fun showOncall(): List<UndecidedPlan> {
        val bearerAccessToken =
            CoNetApplication.getInstance().getDataStore().bearerAccessToken.first()

        return suspendCoroutine { continuation ->
            RetrofitClient.homeInstance.getWaitingPlan(
                authorization = bearerAccessToken,
            ).enqueue(object : Callback<ResponseGetWaitingPlan> {
                override fun onResponse(
                    call: Call<ResponseGetWaitingPlan>,
                    response: Response<ResponseGetWaitingPlan>
                ) {
                    if (response.isSuccessful) {
                        Log.d(NETWORK, "OncallList - getWaitingPlan() 실행결과 - 성공")
                        val resp =
                            requireNotNull(response.body()) { "OncallList - getWaitingPlan() 결과 불러오기 실패" }
                        continuation.resume(resp.result.plans.map { it.asUndecidedPlan() })
                    } else {
                        Log.d(NETWORK, "OncallList - getWaitingPlan() 실행결과 - 안좋음")
                        continuation.resumeWithException(Exception("Response not successful"))
                    }
                }

                override fun onFailure(call: Call<ResponseGetWaitingPlan>, t: Throwable) {
                    Log.d(NETWORK, "OncallList - getWaitingPlan() 실행결과 - 실패\nbecause : $t")
                    continuation.resumeWithException(t)
                }

            })
        }

    }

    private suspend fun showGroupOncall(): List<UndecidedPlan> {
        return suspendCoroutine { continuation ->
            RetrofitClient.planInstance.getGroupWaitingPlan(
                teamId = groupId.toLong()
            ).enqueue(object : Callback<ResponseGetGroupWaitingPlan> {
                override fun onResponse(
                    call: Call<ResponseGetGroupWaitingPlan>,
                    response: Response<ResponseGetGroupWaitingPlan>
                ) {
                    if (response.isSuccessful) {
                        Log.d(NETWORK, "OncallList - getGroupWaitingPlan() 실행결과 - 성공")
                        val resp =
                            requireNotNull(response.body()) { "OncallList - getGroupWaitingPlan() 실행결과 불러오기 실패" }
                        continuation.resume(resp.result.plans.map { it.asUndecidedPlan() })
                    } else {
                        Log.d(NETWORK, "OncallList - getGroupWaitingPlan() 실행결과 - 안좋음")
                        continuation.resumeWithException(Exception("Response not successful"))
                    }
                }

                override fun onFailure(call: Call<ResponseGetGroupWaitingPlan>, t: Throwable) {
                    Log.d(NETWORK, "OncallList - getGroupWaitingPlan() 실행결과 - 실패\nbecause : $t")
                    continuation.resumeWithException(t)
                }

            })
        }
    }

    private fun initAllRecycler(oncall: List<UndecidedPlan>) {
        val data = oncall.ifEmpty { listOf() }
        allTodoRecyclerAdapter = AllTodoRecyclerAdapter(data, option, requireActivity())
        binding.rvAlltodolist.adapter = allTodoRecyclerAdapter
    }

    suspend fun waitForInit() {
        initDeferred.await()
    }

    fun returnsize(): Int = allTodoRecyclerAdapter.itemCount

}
