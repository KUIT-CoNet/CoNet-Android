package com.kuit.conet.UI.History

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import com.kuit.conet.Network.*
import com.kuit.conet.UI.Home.RecyclerView.ConfirmRecyclerAdapter
import com.kuit.conet.databinding.ActivityNonregistedlastpromiseBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.suspendCoroutine

class NonResigestedActivity : AppCompatActivity() {
    lateinit var binding : ActivityNonregistedlastpromiseBinding
    lateinit var confirmRecyclerAdapter : ConfirmRecyclerAdapter
    val groupId : Int by lazy{intent.getIntExtra("groupId",0)}
    var item = ArrayList<SidePlanInfo>()
    override fun onResume() {
        super.onResume()
        binding = ActivityNonregistedlastpromiseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ShowNonHistoryInfo()
        binding.btnClose.setOnClickListener {
            finish()
        }

    }

    fun initRecycler(){
        confirmRecyclerAdapter = ConfirmRecyclerAdapter(this ,3)
        binding.lastListRv.adapter = confirmRecyclerAdapter
        binding.lastListRv.layoutManager = LinearLayoutManager(this)
        confirmRecyclerAdapter.datas = item
        confirmRecyclerAdapter.notifyDataSetChanged()
    }

    fun ShowNonHistoryInfo(){

        val responseHistory = getRetrofit().create(RetrofitInterface::class.java)
        responseHistory.ShownonHistory(
            groupId
        ).enqueue(object : Callback<ResponseSidePlan> {
            override fun onResponse(
                call: Call<ResponseSidePlan>,
                response: Response<ResponseSidePlan>
            ) {
                if (response.isSuccessful) {
                    val resp = response.body()// 성공했을 경우 response body불러오기
                    item = resp!!.result
                    initRecycler()
                }
                else{

                }
            }

            override fun onFailure(call: Call<ResponseSidePlan>, t: Throwable) {
                Log.d("SIGNUP/FAILURE", t.message.toString()) // 실패한 이유 메세지 출력

            }

        })

    }
}