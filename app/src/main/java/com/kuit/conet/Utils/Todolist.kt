package com.kuit.conet.Utils

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.kuit.conet.Network.HomePlanInfo
import com.kuit.conet.Network.RetrofitInterface
import com.kuit.conet.Network.getRetrofit
import com.kuit.conet.Network.plans
import com.kuit.conet.UI.Home.RecyclerView.TodoRecyclerAdapter
import com.kuit.conet.databinding.FragmentTodolistBinding
import com.kuit.conet.getRefreshToken
import com.prolificinteractive.materialcalendarview.CalendarDay
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

class Todolist(date : CalendarDay, groupId : Int) : Fragment() { // -1은 홈메뉴의 확정 약속 보여줌 1부터는 모임탭의 확정약속 보여줌(이 숫자가 teamId의 역할을 할 거임)
    lateinit var binding : FragmentTodolistBinding
    lateinit var todoRecyclerAdapter : TodoRecyclerAdapter
    var plans = ArrayList<plans>()
    val date = date
    val groupId = groupId

    private val initDeferred = CompletableDeferred<Unit>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTodolistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            if(groupId < 0){
                plans = showplaninfo(date)
            }
            else{
                plans = showGroupplaninfo(date)
            }
            initRecycler(plans)
            initDeferred.complete(Unit)
        }
    }

    suspend fun showGroupplaninfo(date: CalendarDay): ArrayList<plans> {
        return suspendCoroutine { continuation2->
            Log.d("callDate","${date}")
            val year = (date.year).toString()
            val month = if(date.month + 1 < 10) "0" + (date.month + 1).toString() else (date.month + 1).toString()
            val day = if(date.day < 10) "0" + (date.day).toString() else (date.day).toString()
            val oncalldate = year + "-" + month + "-" + day
            val responsePlan = getRetrofit().create(RetrofitInterface::class.java) // 이걸 따로 빼내는 방법....
            responsePlan.ShowGroupConfirmPlan(
                groupId,
                oncalldate
            ).enqueue(object :
                Callback<HomePlanInfo> { // 서버와 비동기적으로 데이터 주고받을 수 있는 방법 enqueue사용
                override fun onResponse( // 통신에 성공했을 경우
                    call: Call<HomePlanInfo>,
                    response: Response<HomePlanInfo>
                ) {
                    if (response.isSuccessful) {
                        val resp = response.body()// 성공했을 경우 response body불러오기
                        Log.d("SIGNUP/SUCCESS", resp.toString())
                        Log.d("성공!","success")
                        continuation2.resume(resp!!.result.plans)
                    }
                    else{
                        continuation2.resumeWithException(Exception("Response not successful"))
                    }
                }

                override fun onFailure(call: Call<HomePlanInfo>, t: Throwable) { // 통신에 실패했을 경우
                    Log.d("SIGNUP/FAILURE", t.message.toString()) // 실패한 이유 메세지 출력
                    continuation2.resumeWithException(t)
                }

            })
        }

    }

    suspend fun showplaninfo(date : CalendarDay) : ArrayList<plans>{
        return suspendCoroutine { continuation2->
            Log.d("callDate","${date}")
            val year = (date.year).toString()
            val month = if(date.month + 1 < 10) "0" + (date.month + 1).toString() else (date.month + 1).toString()
            val day = if(date.day < 10) "0" + (date.day).toString() else (date.day).toString()
            val oncalldate = year + "-" + month + "-" + day
            val responsePlan = getRetrofit().create(RetrofitInterface::class.java)
            val refreshToken = getRefreshToken(requireContext())
            responsePlan.homepromiseinfo(
                "Bearer $refreshToken",
                oncalldate
            ).enqueue(object :
                Callback<HomePlanInfo> { // 서버와 비동기적으로 데이터 주고받을 수 있는 방법 enqueue사용
                override fun onResponse( // 통신에 성공했을 경우
                    call: Call<HomePlanInfo>,
                    response: Response<HomePlanInfo>
                ) {
                    if (response.isSuccessful) {
                        val resp = response.body()// 성공했을 경우 response body불러오기
                        Log.d("SIGNUP/SUCCESS", resp.toString())
                        Log.d("성공!","success")
                        continuation2.resume(resp!!.result.plans)
                    }
                    else{
                        continuation2.resumeWithException(Exception("Response not successful"))
                    }
                }

                override fun onFailure(call: Call<HomePlanInfo>, t: Throwable) { // 통신에 실패했을 경우
                    Log.d("SIGNUP/FAILURE", t.message.toString()) // 실패한 이유 메세지 출력
                    continuation2.resumeWithException(t)
                }

            })
        }

    }

    suspend fun waitForInit(){
        initDeferred.await()
    }

    fun initRecycler(plans : ArrayList<plans>?) {
        Log.d("todolist","${plans}")
        if(plans != null){
            todoRecyclerAdapter = TodoRecyclerAdapter(requireContext())
            binding.rvTodolist.adapter = todoRecyclerAdapter
            binding.rvTodolist.layoutManager = LinearLayoutManager(context)
            todoRecyclerAdapter.itemList = plans
            todoRecyclerAdapter.notifyDataSetChanged()
//            binding.tvPromiseCount.text = plans.count().toString()
        }
        else{
            todoRecyclerAdapter = TodoRecyclerAdapter(requireContext())
            binding.rvTodolist.adapter = todoRecyclerAdapter
            binding.rvTodolist.layoutManager = LinearLayoutManager(context)
            todoRecyclerAdapter.itemList = ArrayList()
            todoRecyclerAdapter.notifyDataSetChanged()
//            binding.tvPromiseCount.text = "0"
        }
    }

    fun returnsize() : Int{
        return todoRecyclerAdapter.itemList.size
    }

}


