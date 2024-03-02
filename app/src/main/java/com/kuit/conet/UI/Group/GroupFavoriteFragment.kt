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
import com.kuit.conet.Utils.getAccessToken
import retrofit2.Call
import retrofit2.Response

class GroupFavoriteFragment : Fragment() {

    private var _binding: FragmentGroupListBinding? = null
    private val binding: FragmentGroupListBinding
        get() = requireNotNull(_binding) { "GroupFavoriteFragment's binding is null" }
    private lateinit var favoriteGroupList: List<ResultGetGroup>

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

    override fun onStart() {
        super.onStart()
        Log.d(LIFECYCLE, "GroupFavoriteFragment - onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(LIFECYCLE, "GroupFavoriteFragment - onResume() called")

        RetrofitClient.jsonInstance.getBookmarkGroup("Bearer" + getAccessToken(requireContext()))
            .enqueue(object : retrofit2.Callback<ResponseGetGroup> {
                override fun onResponse(
                    call: Call<ResponseGetGroup>,
                    response: Response<ResponseGetGroup>
                ) {
                    if (response.isSuccessful) {
                        Log.d(
                            NETWORK, "GroupFavoriteFragment - Retrofit getTeam()실행 결과 - 성공\n" +
                                    "response : $response\n" +
                                    "response.body : ${response.body()}"
                        )

                        favoriteGroupList = response.body()!!.result

                        GroupFragment.binding.tvGroupCount.text =
                            favoriteGroupList.count().toString()

                        val groupAdapter = GroupAdapter(requireContext(), favoriteGroupList)
                        binding.rvGroupList.adapter = groupAdapter
                        binding.rvGroupList.layoutManager = GridLayoutManager(context, 2)
                    } else {
                        Log.d(NETWORK, "GroupFavoriteFragment - Retrofit getTeam()실행 결과 - 안좋음")
                    }
                }

                override fun onFailure(call: Call<ResponseGetGroup>, t: Throwable) {
                    Log.d(NETWORK, "GroupFavoriteFragment - Retrofit getTeam()실행 결과 - 실패")
                }
            })
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
        Log.d(LIFECYCLE, "GroupAllFragment - onDestroyView() called")
    }
}