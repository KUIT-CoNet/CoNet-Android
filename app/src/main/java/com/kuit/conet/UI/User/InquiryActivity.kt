package com.kuit.conet.UI.User

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.GridLayoutManager
import com.kuit.conet.Network.Notice
import com.kuit.conet.Network.NoticeInfo
import com.kuit.conet.Network.ResponseGetGroup
import com.kuit.conet.Network.RetrofitClient
import com.kuit.conet.UI.Group.GroupAdapter
import com.kuit.conet.UI.Group.GroupFragment
import com.kuit.conet.Utils.NETWORK
import com.kuit.conet.databinding.ActivityInquiryBinding
import com.kuit.conet.getAccessToken
import retrofit2.Call
import retrofit2.Response

class InquiryActivity : AppCompatActivity() {
    lateinit var binding: ActivityInquiryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityInquiryBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.ivInquiryBackBtn.setOnClickListener {
            finish()
        }
    }
}
