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
import com.kuit.conet.data.dto.response.member.ResponseGetBookmarkGroups
import com.kuit.conet.domain.entity.group.GroupSimple
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class GroupFavoriteFragment : Fragment() {

    private var _binding: FragmentGroupListBinding? = null
    private val binding: FragmentGroupListBinding
        get() = requireNotNull(_binding) { "GroupFavoriteFragment's binding is null" }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(LIFECYCLE, "GroupFavoriteFragment - onCreateView() called")
        _binding = FragmentGroupListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(LIFECYCLE, "GroupFavoriteFragment - onViewCreated() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(LIFECYCLE, "GroupFavoriteFragment - onResume() called")

        viewLifecycleOwner.lifecycleScope.launch {
            val bearerAccessToken =
                CoNetApplication.getInstance().getDataStore().bearerAccessToken.first()
            val groupList = getBookmarkGroups(bearerAccessToken)
            GroupFragment.binding.tvGroupCount.text = groupList.count().toString()
            binding.rvGroupList.adapter = GroupAdapter(requireContext(), groupList)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
        Log.d(LIFECYCLE, "GroupAllFragment - onDestroyView() called")
    }

    private suspend fun getBookmarkGroups(accessToken: String): List<GroupSimple> =
        suspendCancellableCoroutine { continuation ->

            val call = RetrofitClient.memberInstance.getBookmarkGroups(
                authorization = accessToken,
            )

            continuation.invokeOnCancellation {
                Log.e(NETWORK, "GroupFavoriteFragment - getBookmarkGroups() 취소됨")
                call.cancel()
            }

            call.enqueue(object : retrofit2.Callback<ResponseGetBookmarkGroups> {
                override fun onResponse(
                    call: Call<ResponseGetBookmarkGroups>,
                    response: Response<ResponseGetBookmarkGroups>
                ) {
                    if (response.isSuccessful) {
                        Log.d(
                            NETWORK,
                            "GroupFavoriteFragment - Retrofit getBookmarkGroups()실행 결과 - 성공"
                        )
                        val resp =
                            requireNotNull(response.body()) { "GroupFavoriteFragment's getBookmarkGroups 결과 불러오기 실패" }
                        val groupList = resp.result.map { it.asGroupSimple() }
                        continuation.resume(groupList)
                        /*GroupFragment.binding.tvGroupCount.text = groupList.count().toString()
                        binding.rvGroupList.adapter = GroupAdapter(requireContext(), groupList)*/
                    } else {
                        Log.d(
                            NETWORK,
                            "GroupFavoriteFragment - Retrofit getBookmarkGroups()실행 결과 - 안좋음"
                        )
                        continuation.resume(GroupSimple.EMPTY_LIST)
                    }
                }

                override fun onFailure(call: Call<ResponseGetBookmarkGroups>, t: Throwable) {
                    Log.d(NETWORK, "GroupFavoriteFragment - Retrofit getBookmarkGroups()실행 결과 - 실패")
                    continuation.resumeWithException(t)
                }
            })
        }
}