package com.kuit.conet.UI.User

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kuit.conet.databinding.ActivityCheckBinding

class CheckActivity : AppCompatActivity() {
    lateinit var binding: ActivityCheckBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCheckBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.checkBtnBackIv.setOnClickListener {
            finish()
        }
    }
}