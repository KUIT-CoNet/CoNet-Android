package com.kuit.conet.UI.Group

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.kuit.conet.Network.ResponseGetGroup
import com.kuit.conet.Network.ResultGetGroup
import com.kuit.conet.Network.RetrofitClient
import com.kuit.conet.Utils.LIFECYCLE
import com.kuit.conet.Utils.NETWORK
import com.kuit.conet.databinding.FragmentGroupListBinding
import com.kuit.conet.getAccessToken
import retrofit2.Call
import retrofit2.Response

class GroupAllFragment : Fragment() {

    private var _binding: FragmentGroupListBinding? = null
    private val binding: FragmentGroupListBinding
        get() = requireNotNull(_binding) { "GroupAllFragment's binding is null" }
    private lateinit var groupList: List<ResultGetGroup>


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
    // TODO onResume에서 매번 네트워크 통신을 통해 groupList를 가져오는 것이 맞는 것일까?
    override fun onResume() {
        super.onResume()
        Log.d(LIFECYCLE, "GroupAllFragment - onResume() 실행")

        RetrofitClient.instance.getGroup("Bearer " + getAccessToken(requireContext()))
            .enqueue(object : retrofit2.Callback<ResponseGetGroup> {
                override fun onResponse(
                    call: Call<ResponseGetGroup>,
                    response: Response<ResponseGetGroup>
                ) {
                    if (response.isSuccessful) {
                        Log.d(
                            NETWORK, "GroupAllFragment - getTeam()실행 결과 - 성공\n" +
                                    "response : $response\n" +
                                    "response.body : ${response.body()}"
                        )

                        groupList = response.body()!!.result
                        GroupFragment.binding.tvGroupCount.text = groupList.count().toString()
                        binding.rvGroupList.adapter = GroupAdapter(requireContext(), groupList)
                        binding.rvGroupList.layoutManager = GridLayoutManager(context, 2)

                    } else {
                        Log.d(
                            NETWORK, "GroupAllFragment - getTeam()실행 결과 - 안좋음\n" +
                                    "response - $response"
                        )
                    }
                }

                override fun onFailure(call: Call<ResponseGetGroup>, t: Throwable) {
                    Log.d(NETWORK, "GroupAllFragment - getTeam()실행 결과 - 실패")
                }
            })
    }

}