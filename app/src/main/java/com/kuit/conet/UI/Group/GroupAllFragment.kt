package com.kuit.conet.UI.Group

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.kuit.conet.Network.RetrofitClient
import com.kuit.conet.UI.application.CoNetApplication
import com.kuit.conet.Utils.LIFECYCLE
import com.kuit.conet.Utils.NETWORK
import com.kuit.conet.databinding.FragmentGroupListBinding
import com.kuit.conet.data.dto.response.team.ResponseGetGroups
import com.kuit.conet.domain.entity.group.GroupSimple
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class GroupAllFragment : Fragment() {

    private var _binding: FragmentGroupListBinding? = null
    private val binding: FragmentGroupListBinding
        get() = requireNotNull(_binding) { "GroupAllFragment's binding is null" }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(LIFECYCLE, "GroupAllFragment - onCreateView() called")
        _binding = FragmentGroupListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(LIFECYCLE, "GroupAllFragment - onViewCreated() called")
    }

    // onResume에서 통신을 하는 이유 : ViewPager의 화면 전환은 onResume 라이프 사이클을 보냄
    override fun onResume() {
        super.onResume()
        Log.d(LIFECYCLE, "GroupAllFragment - onResume() 실행")

        val handler = CoroutineExceptionHandler { coroutineContext, throwable ->
            Log.e("Error", "GroupAllFragment - CoroutineExceptionHandler : $throwable")
            coroutineContext.cancel()
        }

        viewLifecycleOwner.lifecycleScope.launch(handler) {
            val bearerAccessToken =
                CoNetApplication.getInstance().getDataStore().bearerAccessToken.first()
            val groupList = getGroups(bearerAccessToken)
            GroupFragment.binding.tvGroupCount.text = groupList.count().toString()
            binding.rvGroupList.adapter =
                GroupAdapter(requireContext(), groupList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d(LIFECYCLE, "GroupAllFragment - onDestroyView() called")
    }

    private suspend fun getGroups(accessToken: String): List<GroupSimple> =
        suspendCancellableCoroutine { continuation ->

            val call = RetrofitClient.teamInstance.getGroups(
                authorization = accessToken,
            )

            continuation.invokeOnCancellation { // 취소될 때 실행
                Log.d(NETWORK, "GroupAllFragment - getGroups() 실행 취소됨")
                call.cancel()
            }

            call.enqueue(object : retrofit2.Callback<ResponseGetGroups> {
                override fun onResponse(
                    call: Call<ResponseGetGroups>,
                    response: Response<ResponseGetGroups>
                ) {
                    if (response.isSuccessful) {
                        Log.d(NETWORK, "GroupAllFragment - getTeam()실행 결과 - 성공")

                        val resp =
                            requireNotNull(response.body()) { "GroupAllFragment's getGroups 결과 불러오기 실패" }
                        val groupList = resp.result.map { it.asGroupSimple() }
                        continuation.resume(groupList)
                    } else {
                        Log.d(
                            NETWORK, "GroupAllFragment - getTeam()실행 결과 - 안좋음\nresponse : $response"
                        )
                        continuation.resume(GroupSimple.EMPTY_LIST)
                    }
                }

                override fun onFailure(call: Call<ResponseGetGroups>, t: Throwable) {
                    Log.d(NETWORK, "GroupAllFragment - getTeam()실행 결과 - 실패\nbecause: $t")
                    continuation.resumeWithException(t)
                }
            })
        }

}