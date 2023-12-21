package com.kuit.conet.UI.Group

import com.kuit.conet.databinding.FragmentGroupCategoryFavoriteBinding
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.kuit.conet.Data.GroupList
import com.kuit.conet.Data.GroupList.favoriteGroupList
import com.kuit.conet.Network.ResponseGetGroup
import com.kuit.conet.Network.RetrofitClient
import com.kuit.conet.Utils.TAG
import com.kuit.conet.getAccessToken
import retrofit2.Call
import retrofit2.Response

class GroupFavoriteFragment: Fragment() {

    private lateinit var binding: FragmentGroupCategoryFavoriteBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupCategoryFavoriteBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "GroupFavoriteFragment - getTeam()실행 결과 - 안좋음")
        RetrofitClient.instance.getBookmarkGroup("Bearer" + getAccessToken(requireContext())).enqueue(object : retrofit2.Callback<ResponseGetGroup>{
            override fun onResponse(
                call: Call<ResponseGetGroup>,
                response: Response<ResponseGetGroup>
            ) {
                if(response.isSuccessful) {
                    Log.d(TAG, "GroupFavoriteFragment - Retrofit getTeam()실행 결과 - 성공")
                    Log.d(TAG, "response : $response")
                    Log.d(TAG, "response.body : ${response.body()}")

                    favoriteGroupList = response.body()!!.result

                    GroupFragment.binding.tvGroupCount.text = favoriteGroupList.count().toString()

                    val groupAdapter = GroupAdapter(requireContext(), favoriteGroupList)
                    binding.groupListRv.adapter = groupAdapter
                    binding.groupListRv.layoutManager = GridLayoutManager(context, 2)
                } else {
                    Log.d(TAG, "GroupFavoriteFragment - Retrofit getTeam()실행 결과 - 안좋음")
                }
            }
            override fun onFailure(call: Call<ResponseGetGroup>, t: Throwable) {
                Log.d(TAG, "GroupFavoriteFragment - Retrofit getTeam()실행 결과 - 실패")
            }
        })
    }
}