package com.kuit.conet.UI.User

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kuit.conet.databinding.ActivityNoticeBinding

class NoticeActivity : AppCompatActivity() {
    lateinit var binding : ActivityNoticeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityNoticeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.noticeBtnBackIv.setOnClickListener {
            finish()
        }
    }
}