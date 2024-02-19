package com.kuit.conet.UI.User

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.kuit.conet.Network.Notice
import com.kuit.conet.Network.NoticeInfo
import com.kuit.conet.Network.RetrofitClient
import com.kuit.conet.Utils.NETWORK
import com.kuit.conet.databinding.ActivityNoticeBinding
import com.kuit.conet.getAccessToken
import retrofit2.Call
import retrofit2.Response

class NoticeActivity : AppCompatActivity() {
    lateinit var binding : ActivityNoticeBinding
    private lateinit var noticeList: List<NoticeInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityNoticeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        getNotice()

        binding.ivNoticeBackBtn.setOnClickListener {
            finish()
        }
    }

    private fun getNotice () {
        RetrofitClient.instance.getNotice("Bearer" + getAccessToken(this))
            .enqueue(object : retrofit2.Callback<Notice> {
                override fun onResponse(
                    call: Call<Notice>,
                    response: Response<Notice>
                ) {
                    if (response.isSuccessful) {
                        Log.d(
                            NETWORK, "InquiryActivity - Retrofit getNotice()실행 결과 - 성공\n" +
                                    "response : $response\n" +
                                    "response.body :  ${response.body()}"
                        )
                        noticeList = listOf(response.body()!!.result)
                        //공지사항 작업하기..

                    } else {
                        Log.d(NETWORK, "InquiryActivity - Retrofit getNotice()실행 결과 - 안좋음\n")
                    }
                }

                override fun onFailure(call: Call<Notice>, t: Throwable) {
                    Log.d(NETWORK, "InquiryActivity - Retrofit getNotice()실행 결과 - 실패\n")

                }
            })
    }
}