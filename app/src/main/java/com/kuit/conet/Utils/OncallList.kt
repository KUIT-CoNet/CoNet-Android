package com.kuit.conet.Utils

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.kuit.conet.Network.HomeOncall
import com.kuit.conet.Network.Oncall
import com.kuit.conet.Network.RetrofitInterface
import com.kuit.conet.Network.getRetrofit
import com.kuit.conet.UI.Home.RecyclerView.AllTodoRecyclerAdapter
import com.kuit.conet.databinding.FragmentOncallBinding
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class OncallList(groupId : Int, option : Int) : Fragment() { // -1이면 홈 화면 용 1이상이면 모임 화면 용(이 값이 groupId역할을 할 거임)
    lateinit var binding : FragmentOncallBinding
    lateinit var allTodoRecyclerAdapter: AllTodoRecyclerAdapter
    lateinit var mContext : Context
    var oncall = ArrayList<Oncall>()
    val groupId = groupId
    val option = option // 사이드바 메뉴에 띄울 때랑 그냥 메인 페이지에 띄울 때랑 그림자 속성이 달라서 option변수 사용

    private val initDeferred = CompletableDeferred<Unit>()
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentOncallBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            if(groupId < 0){
                oncall = showOncall()
            }
            else{
                oncall = showGroupOncall()
            }
            initAllRecycler(oncall)
            initDeferred.complete(Unit)
        }
    }

    suspend fun waitForInit(){
        initDeferred.await()
    }

    fun initAllRecycler(oncall : ArrayList<Oncall>){
        if(oncall != null){
            allTodoRecyclerAdapter = AllTodoRecyclerAdapter(option,mContext)
            binding.rvAlltodolist.adapter = allTodoRecyclerAdapter
            binding.rvAlltodolist.layoutManager = LinearLayoutManager(context)
            allTodoRecyclerAdapter.datas = oncall
            allTodoRecyclerAdapter.notifyDataSetChanged()
//            binding.tvPromiseallCount.text = date.count().toString()
        }
        else{
            allTodoRecyclerAdapter = AllTodoRecyclerAdapter(option,mContext)
            binding.rvAlltodolist.adapter = allTodoRecyclerAdapter
            binding.rvAlltodolist.layoutManager = LinearLayoutManager(context)
            allTodoRecyclerAdapter.datas = ArrayList()
            allTodoRecyclerAdapter.notifyDataSetChanged()
//            binding.tvPromiseallCount.text = "0"
        }

    }

    suspend fun showOncall() : ArrayList<Oncall>{
        return suspendCoroutine {
            continuation ->
            val responseOncall = getRetrofit().create(RetrofitInterface::class.java) // 이걸 따로 빼내는 방법....
            val refreshToken = getRefreshToken(requireContext())
            responseOncall.homeoncallshow(
                "Bearer $refreshToken",
            ).enqueue(object :
                Callback<HomeOncall> { // 서버와 비동기적으로 데이터 주고받을 수 있는 방법 enqueue사용
                override fun onResponse( // 통신에 성공했을 경우
                    call: Call<HomeOncall>,
                    response: Response<HomeOncall>
                ) {
                    if (response.isSuccessful) {
                        val resp = response.body()// 성공했을 경우 response body불러오기
                        Log.d("SIGNUP/SUCCESS", resp.toString())
                        Log.d("성공!","success")
                        continuation.resume(resp!!.result.plans)
                    }
                    else{
                        continuation.resumeWithException(Exception("Response not successful"))
                    }
                }

                override fun onFailure(call: Call<HomeOncall>, t: Throwable) { // 통신에 실패했을 경우
                    Log.d("SIGNUP/FAILURE", t.message.toString()) // 실패한 이유 메세지 출력
                    continuation.resumeWithException(t)
                }

            })
        }

    }

    suspend fun showGroupOncall() : ArrayList<Oncall>{
        return suspendCoroutine {
                continuation ->
            val responseOncall = getRetrofit().create(RetrofitInterface::class.java) // 이걸 따로 빼내는 방법....
            responseOncall.ShowGroupOncall(
                groupId,
            ).enqueue(object :
                Callback<HomeOncall> { // 서버와 비동기적으로 데이터 주고받을 수 있는 방법 enqueue사용
                override fun onResponse( // 통신에 성공했을 경우
                    call: Call<HomeOncall>,
                    response: Response<HomeOncall>
                ) {
                    if (response.isSuccessful) {
                        val resp = response.body()// 성공했을 경우 response body불러오기
                        Log.d("SIGNUP/SUCCESS", resp.toString())
                        Log.d("성공!","success")
                        continuation.resume(resp!!.result.plans)
                    }
                    else{
                        continuation.resumeWithException(Exception("Response not successful"))
                    }
                }

                override fun onFailure(call: Call<HomeOncall>, t: Throwable) { // 통신에 실패했을 경우
                    Log.d("SIGNUP/FAILURE", t.message.toString()) // 실패한 이유 메세지 출력
                    continuation.resumeWithException(t)
                }

            })
        }
    }

    fun returnsize() : Int{
        return allTodoRecyclerAdapter.datas.size
    }
}
