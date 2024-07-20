package com.kuit.conet.UI.User

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.kuit.conet.Network.Notice
import com.kuit.conet.Network.NoticeInfo
import com.kuit.conet.Network.RetrofitClient
import com.kuit.conet.UI.application.CoNetApplication
import com.kuit.conet.Utils.NETWORK
import com.kuit.conet.databinding.ActivityNoticeBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

class NoticeActivity : AppCompatActivity() {
    lateinit var binding: ActivityNoticeBinding
    private var noticeList: ArrayList<NoticeInfo> = arrayListOf()
    private var noticeAdapter: NoticeAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityNoticeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        lifecycleScope.launch {
            getNotice()
        }

        binding.ivNoticeBackBtn.setOnClickListener {
            finish()
        }
    }

    private fun initNoticeList() {
        if (noticeList.size == 1) {
            binding.tvNoticeNoContent.visibility = View.VISIBLE
            binding.rvNotice.visibility = View.GONE
        } else {
            binding.tvNoticeNoContent.visibility = View.GONE
            binding.rvNotice.visibility = View.VISIBLE
            noticeAdapter = NoticeAdapter(this, noticeList)
            binding.rvNotice.adapter = noticeAdapter
            binding.rvNotice.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

            noticeAdapter!!.setOnItemClickListener(object : NoticeAdapter.OnItemClickListener {
                override fun onItemClick(noticeInfo: NoticeInfo) {

                }
            })
        }
    }

    private suspend fun getNotice() {
        val bearerAccessToken =
            CoNetApplication.getInstance().getDataStore().bearerAccessToken.first()

        RetrofitClient.instance.getNotice(
            authorization = bearerAccessToken,
        ).enqueue(object : retrofit2.Callback<Notice> {
            override fun onResponse(
                call: Call<Notice>,
                response: Response<Notice>
            ) {
                if (response.isSuccessful) {
                    Log.d(
                        NETWORK, "NoticeActivity - Retrofit getNotice()실행 결과 - 성공\n" +
                                "response : $response\n" +
                                "response.body :  ${response.body()}"
                    )

                    response.body()?.let { listOf(it.result) }?.let {
                        noticeList.addAll(
                            it
                        )
                    }

                    initNoticeList()

                } else {
                    Log.d(NETWORK, "NoticeActivity - Retrofit getNotice()실행 결과 - 안좋음\n")
                }
            }

            override fun onFailure(call: Call<Notice>, t: Throwable) {
                Log.d(NETWORK, "NoticeActivity - Retrofit getNotice()실행 결과 - 실패\n")

            }
        })
    }
}