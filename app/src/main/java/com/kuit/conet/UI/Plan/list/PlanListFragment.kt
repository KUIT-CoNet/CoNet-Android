package com.kuit.conet.UI.Plan.list

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.kuit.conet.Network.ResponseSidePlan
import com.kuit.conet.Network.RetrofitInterface
import com.kuit.conet.Network.SidePlanInfo
import com.kuit.conet.Network.getRetrofit
import com.kuit.conet.R
import com.kuit.conet.UI.Home.RecyclerView.ConfirmRecyclerAdapter
import com.kuit.conet.databinding.FragmentGroupBinding
import com.kuit.conet.databinding.FragmentPlanListBinding
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
    private val option: Int by lazy { requireNotNull(arguments?.getInt(PlanVPAdapter.BUNDLE_OPTION)) { "PlanListFragment's option is null" } }
    private val groupId: Int by lazy { requireNotNull(arguments?.getInt(PlanVPAdapter.BUNDLE_GROUP_ID)) { "PlanListFragment's groupId is null" } }

    private val initDeferred = CompletableDeferred<Unit>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            val plans = if (option == 1) {
                showSideConfirmplaninfo()
            } else {
                showSideLastPlan()
            }

            initConfirmRecycler(plans, option)
            initDeferred.complete(Unit)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun initConfirmRecycler(item: ArrayList<SidePlanInfo>, option: Int) {
        val confirmRecyclerAdapter = ConfirmRecyclerAdapter(requireContext(), option)
        binding.rvConfirmlist.adapter = confirmRecyclerAdapter
        binding.rvConfirmlist.layoutManager = LinearLayoutManager(context)
        confirmRecyclerAdapter.datas = item
        confirmRecyclerAdapter.notifyDataSetChanged()
    }

    private suspend fun showSideConfirmplaninfo(): ArrayList<SidePlanInfo> { // 사이드바 확정된 약속 조회
        return suspendCoroutine { continuation2 ->
            val responsePlan =
                getRetrofit().create(RetrofitInterface::class.java) // 이걸 따로 빼내는 방법....
            responsePlan.ShowSideBarConfirm(groupId).enqueue(object :
                Callback<ResponseSidePlan> { // 서버와 비동기적으로 데이터 주고받을 수 있는 방법 enqueue사용
                override fun onResponse( // 통신에 성공했을 경우
                    call: Call<ResponseSidePlan>,
                    response: Response<ResponseSidePlan>
                ) {
                    if (response.isSuccessful) {
                        val resp = response.body()// 성공했을 경우 response body불러오기
                        Log.d("SIGNUP/SUCCESS", resp.toString())
                        Log.d("성공!", "success")
                        continuation2.resume(resp!!.result)
                    } else {
                        continuation2.resumeWithException(Exception("Response not successful"))
                    }
                }

                override fun onFailure(call: Call<ResponseSidePlan>, t: Throwable) { // 통신에 실패했을 경우
                    Log.d("SIGNUP/FAILURE", t.message.toString()) // 실패한 이유 메세지 출력
                    continuation2.resumeWithException(t)
                }
            })
        }

    }

    private suspend fun showSideLastPlan(): ArrayList<SidePlanInfo> { // 지난 약속 조회
        return suspendCoroutine { continuation2 ->
            val responsePlan =
                getRetrofit().create(RetrofitInterface::class.java) // 이걸 따로 빼내는 방법....
            responsePlan.ShowLastPlan(
                groupId,
            ).enqueue(object :
                Callback<ResponseSidePlan> { // 서버와 비동기적으로 데이터 주고받을 수 있는 방법 enqueue사용
                override fun onResponse( // 통신에 성공했을 경우
                    call: Call<ResponseSidePlan>,
                    response: Response<ResponseSidePlan>
                ) {
                    if (response.isSuccessful) {
                        val resp = response.body()// 성공했을 경우 response body불러오기
                        Log.d("SIGNUP/SUCCESS", resp.toString())
                        Log.d("성공!", "success")
                        continuation2.resume(resp!!.result)
                    } else {
                        continuation2.resumeWithException(Exception("Response not successful"))
                    }
                }

                override fun onFailure(call: Call<ResponseSidePlan>, t: Throwable) { // 통신에 실패했을 경우
                    Log.d("SIGNUP/FAILURE", t.message.toString()) // 실패한 이유 메세지 출력
                    continuation2.resumeWithException(t)
                }
            })
        }
    }
}