package com.kuit.conet.UI.Group

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.kuit.conet.UI.Plan.DetailFixActivity
import com.kuit.conet.Data.GroupList.groupList
import com.kuit.conet.Network.ResponseGetGroup
import com.kuit.conet.Network.RetrofitClient
import com.kuit.conet.UI.Plan.DetailPastActivity
import com.kuit.conet.Utils.TAG
import com.kuit.conet.databinding.FragmentGroupCatetoryAllBinding
import com.kuit.conet.getAccessToken
import retrofit2.Call
import retrofit2.Response

class GroupAllFragment: Fragment() {

    private lateinit var binding: FragmentGroupCatetoryAllBinding

    private lateinit var mContext:Context


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupCatetoryAllBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "GroupAllFragment - onResume() 실행")
//        TODO 서버에서 해당 내용 받아오기
        RetrofitClient.instance.getGroup("Bearer" + getAccessToken(requireContext())).enqueue(object : retrofit2.Callback<ResponseGetGroup>{
            override fun onResponse(
                call: Call<ResponseGetGroup>,
                response: Response<ResponseGetGroup>
            ) {
                if(response.isSuccessful) {
                    Log.d(TAG, "GroupFragment - getTeam()실행 결과 - 성공")
                    Log.d(TAG, "response : $response")
                    Log.d(TAG, "response.body : ${response.body()}")

                    groupList = response.body()!!.result
//                    val groupAdapter = GroupAdapter(requireContext(), groupList)
                    val groupAdapter = GroupAdapter(mContext, groupList)

                    GroupFragment.binding.groupCountTv.text = groupList.count().toString()

                    binding.groupListRv.adapter = groupAdapter
                    binding.groupListRv.layoutManager = GridLayoutManager(context, 2)
                } else {
                    Log.d(TAG, "GroupFragment - getTeam()실행 결과 - 안좋음")
                    Log.d(TAG, "response - ${response}")
                }
            }
            override fun onFailure(call: Call<ResponseGetGroup>, t: Throwable) {
                Log.d(TAG, "GroupFragment - getTeam()실행 결과 - 실패")
            }
        })
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "GroupAllFragment - onStart() 실행")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

}