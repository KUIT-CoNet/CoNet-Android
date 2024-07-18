package com.kuit.conet.Utils

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.kuit.conet.Network.RetrofitClient
import com.kuit.conet.UI.Home.RecyclerView.TodoRecyclerAdapter
import com.kuit.conet.UI.application.CoNetApplication
import com.kuit.conet.data.dto.response.home.ResponseGetDailyPlan
import com.kuit.conet.data.dto.response.plan.ResponseGetGroupDailyDecidedPlan
import com.kuit.conet.databinding.FragmentTodolistBinding
import com.kuit.conet.domain.entity.plan.DecidedPlan
import com.prolificinteractive.materialcalendarview.CalendarDay
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class Todolist(
    private val date: CalendarDay,
    private val groupId: Int        // -1 : HomeFragment, 1 이상(groupId) : GroupMainActivity
) : Fragment() {

    private var _binding: FragmentTodolistBinding? = null
    private val binding: FragmentTodolistBinding
        get() = requireNotNull(_binding) { "Todolist's binding is null" }

    private lateinit var todoRecyclerAdapter: TodoRecyclerAdapter
    private val initDeferred = CompletableDeferred<Unit>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodolistBinding.inflate(inflater, container, false)
        Log.d(LIFECYCLE, "Todolist - onCreateView() called")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(LIFECYCLE, "Todolist - onViewCreated() called")

        viewLifecycleOwner.lifecycleScope.launch {
            val plans = if (groupId < 0) {      // HomeFragment
                val bearerAccessToken =
                    CoNetApplication.getInstance().getDataStore().bearerAccessToken.first()
                getPlans(bearerAccessToken, date)
            } else {                            // GroupMainActivity
                getGroupPlans(date)
            }

            initRecycler(plans)
            initDeferred.complete(Unit)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
        Log.d(LIFECYCLE, "Todolist - onDestroyView() called")
    }

    private suspend fun getPlans(
        bearerAccessToken: String,
        date: CalendarDay
    ): List<DecidedPlan> = suspendCancellableCoroutine { continuation ->
        val year = (date.year).toString()
        val month = if (date.month < 9) "0${date.month + 1}" else "${date.month + 1}"
        val day = if (date.day < 10) "0${date.day}" else "${date.day}"

        val call = RetrofitClient.homeInstance.getDailyPlan(
            authorization = bearerAccessToken,
            searchDate = "$year-$month-$day",
        )

        continuation.invokeOnCancellation {
            Log.d(NETWORK, "Todolist - getDailyPlan() 실행 취소됨")
            call.cancel()
        }

        call.enqueue(object : Callback<ResponseGetDailyPlan> {
            override fun onResponse(
                call: Call<ResponseGetDailyPlan>,
                response: Response<ResponseGetDailyPlan>
            ) {
                if (response.isSuccessful) {
                    Log.d(NETWORK, "Todolist - getDailyPlan() 실행 결과 - 성공")
                    val resp =
                        requireNotNull(response.body()) { "Todolist - getDailyPlan() 실행 결과 불러오기 실패" }
                    continuation.resume(resp.result.plans.map { it.asDecidedPlan() })
                } else {
                    Log.d(NETWORK, "Todolist - getDailyPlan() 실행 결과 - 안좋음")
                    continuation.resume(DecidedPlan.emptyList())
                }
            }

            override fun onFailure(call: Call<ResponseGetDailyPlan>, t: Throwable) {
                Log.d(NETWORK, "Todolist - getDailyPlan() 실행 결과 - 실해\nbecause : $t")
                continuation.resumeWithException(t)
            }

        })
    }


    private suspend fun getGroupPlans(date: CalendarDay): List<DecidedPlan> =
        suspendCancellableCoroutine { continuation ->
            val year = date.year
            val month = if (date.month < 9) "0${date.month + 1}" else "${date.month + 1}"
            val day = if (date.day < 10) "0${date.day}" else "${date.day}"

            val call = RetrofitClient.planInstance.getGroupDailyDecidedPlan(
                teamId = groupId.toLong(),
                searchDate = "$year-$month-$day",
            )

            continuation.invokeOnCancellation {
                Log.d(NETWORK, "Todolist - getGroupDailyDecidedPlan() 실행 취소됨")
                call.cancel()
            }

            call.enqueue(object : Callback<ResponseGetGroupDailyDecidedPlan> {
                override fun onResponse(
                    call: Call<ResponseGetGroupDailyDecidedPlan>,
                    response: Response<ResponseGetGroupDailyDecidedPlan>
                ) {
                    if (response.isSuccessful) {
                        Log.d(NETWORK, "Todolist - getGroupDailyDecidedPlan() 실행 결과 - 성공")
                        val resp =
                            requireNotNull(response.body()) { "Todolist - getGroupDailyDecidedPlan() 실행 결과 불러오기 실패" }
                        continuation.resume(resp.result.plans.map { it.asDecidedPlan() })
                    } else {
                        Log.d(NETWORK, "Todolist - getGroupDailyDecidedPlan() 실행 결과 - 안좋음")
                        continuation.resume(DecidedPlan.emptyList())
                    }
                }

                override fun onFailure(call: Call<ResponseGetGroupDailyDecidedPlan>, t: Throwable) {
                    Log.d(NETWORK, "Todolist - getGroupDailyDecidedPlan() 실행 결과 - 실패\nbacause : $t")
                    continuation.resumeWithException(t)
                }
            })
        }


    private fun initRecycler(plans: List<DecidedPlan>) {
        todoRecyclerAdapter = TodoRecyclerAdapter(requireContext(), plans)
        binding.rvTodolist.adapter = todoRecyclerAdapter
    }

    suspend fun waitForInit() {
        initDeferred.await()
    }

    fun returnsize(): Int = todoRecyclerAdapter.itemCount

}


