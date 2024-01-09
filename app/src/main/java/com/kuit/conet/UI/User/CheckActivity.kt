package com.kuit.conet.UI.User

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kuit.conet.databinding.ActivityInquiryBinding

class CheckActivity : AppCompatActivity() {
    lateinit var binding: ActivityInquiryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityInquiryBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.checkBtnBackIv.setOnClickListener {
            finish()
        }
    }
}