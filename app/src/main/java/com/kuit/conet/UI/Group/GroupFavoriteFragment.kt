package com.kuit.conet.UI.Group

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kuit.conet.Network.RetrofitClient
import com.kuit.conet.Utils.LIFECYCLE
import com.kuit.conet.Utils.NETWORK
import com.kuit.conet.databinding.FragmentGroupListBinding
import com.kuit.conet.Utils.getAccessToken
import com.kuit.conet.data.dto.response.member.ResponseGetBookmarkGroups
import retrofit2.Call
import retrofit2.Response

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

        RetrofitClient.memberInstance.getBookmarkGroups(
            authorization = "Bearer ${getAccessToken(requireContext())}"
        ).enqueue(object : retrofit2.Callback<ResponseGetBookmarkGroups> {
            override fun onResponse(
                call: Call<ResponseGetBookmarkGroups>,
                response: Response<ResponseGetBookmarkGroups>
            ) {
                if (response.isSuccessful) {
                    Log.d(NETWORK, "GroupFavoriteFragment - Retrofit getBookmarkGroups()실행 결과 - 성공")
                    val resp =
                        requireNotNull(response.body()) { "GroupFavoriteFragment's getBookmarkGroups 결과 불러오기 실패" }
                    val groupList = resp.result.map { it.asGroupSimple() }
                    GroupFragment.binding.tvGroupCount.text = groupList.count().toString()
                    binding.rvGroupList.adapter = GroupAdapter(requireContext(), groupList)
                } else {
                    Log.d(
                        NETWORK,
                        "GroupFavoriteFragment - Retrofit getBookmarkGroups()실행 결과 - 안좋음"
                    )
                }
            }

            override fun onFailure(call: Call<ResponseGetBookmarkGroups>, t: Throwable) {
                Log.d(NETWORK, "GroupFavoriteFragment - Retrofit getBookmarkGroups()실행 결과 - 실패")
            }
        })
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
        Log.d(LIFECYCLE, "GroupAllFragment - onDestroyView() called")
    }
}