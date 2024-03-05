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
import com.kuit.conet.data.dto.response.team.ResponseGetGroups
import retrofit2.Call
import retrofit2.Response

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

        RetrofitClient.teamInstance.getGroups(
            authorization = "Bearer ${getAccessToken(requireContext())}"
        ).enqueue(object : retrofit2.Callback<ResponseGetGroups> {
            override fun onResponse(
                call: Call<ResponseGetGroups>,
                response: Response<ResponseGetGroups>
            ) {
                if (response.isSuccessful) {
                    Log.d(NETWORK, "GroupAllFragment - getTeam()실행 결과 - 성공")

                    val resp =
                        requireNotNull(response.body()) { "GroupAllFragment's getGroups 결과 불러오기 실패" }
                    val groupList = resp.result.map { it.asGroupSimple() }
                    GroupFragment.binding.tvGroupCount.text = groupList.count().toString()
                    binding.rvGroupList.adapter = GroupAdapter(requireContext(), groupList)
                } else {
                    Log.d(
                        NETWORK, "GroupAllFragment - getTeam()실행 결과 - 안좋음\nresponse : $response"
                    )
                }
            }

            override fun onFailure(call: Call<ResponseGetGroups>, t: Throwable) {
                Log.d(NETWORK, "GroupAllFragment - getTeam()실행 결과 - 실패\nbecause: $t")
            }
        })
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
        Log.d(LIFECYCLE, "GroupAllFragment - onDestroyView() called")
    }

}