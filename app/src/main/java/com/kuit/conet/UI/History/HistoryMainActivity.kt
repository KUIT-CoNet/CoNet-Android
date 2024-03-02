//package com.kuit.conet.UI.History
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.kuit.conet.Network.HistoryInfo
//import com.kuit.conet.Network.ResponseShowHistory
//import com.kuit.conet.Network.RetrofitInterface
//import com.kuit.conet.Network.getRetrofit
//import com.kuit.conet.databinding.ActivityHistoryMainBinding
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import kotlin.coroutines.suspendCoroutine
//
//class HistoryMainActivity() : AppCompatActivity() {
//    lateinit var binding : ActivityHistoryMainBinding
//    lateinit var historyAdapter: HistoryAdapter
//    val groupId : Int by lazy{intent.getIntExtra("groupId",0)}
//    var historyList = ArrayList<HistoryInfo>()
//    override fun onCreate(savedInstanceState: Bundle?) {
//        binding = ActivityHistoryMainBinding.inflate(layoutInflater)
//        super.onCreate(savedInstanceState)
//        setContentView(binding.root)
//
//        CoroutineScope(Dispatchers.Main).launch {
//            ShowHistoryInfo()
//        }
//
//        binding.btnBackIv.setOnClickListener {
//            finish()
//        }
//
//        binding.tvAdd.setOnClickListener {
//            val intent = Intent(this, NonResigestedActivity :: class.java)
//            intent.putExtra("groupId", groupId)
//            startActivity(intent)
//        }
//
//    }
//
//    fun initRecycler(){
//        Log.d("히스토리","어댑터 작동")
//        historyAdapter = HistoryAdapter(this)
//        binding.historyListRv.adapter = historyAdapter
//        binding.historyListRv.layoutManager = LinearLayoutManager(this)
//        historyAdapter.data = historyList
//        historyAdapter.notifyDataSetChanged()
//    }
//    suspend fun ShowHistoryInfo(){
//        return suspendCoroutine {
//            continuation ->
//            val responseHistory = getRetrofit().create(RetrofitInterface::class.java)
//            responseHistory.ShowHistory(
//                groupId
//            ).enqueue(object : Callback<ResponseShowHistory>{
//                override fun onResponse(
//                    call: Call<ResponseShowHistory>,
//                    response: Response<ResponseShowHistory>
//                ) {
//                    if (response.isSuccessful) {
//                        val resp = response.body()// 성공했을 경우 response body불러오기
//                        historyList = resp!!.result
//                        initRecycler()
//                    }
//                    else{
//
//                    }
//                }
//
//                override fun onFailure(call: Call<ResponseShowHistory>, t: Throwable) {
//                    Log.d("SIGNUP/FAILURE", t.message.toString()) // 실패한 이유 메세지 출력
//
//                }
//
//            })
//        }
//    }
//
//
//}