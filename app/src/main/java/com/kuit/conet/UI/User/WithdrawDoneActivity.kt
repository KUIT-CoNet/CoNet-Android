package com.kuit.conet.UI.User

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kuit.conet.UI.Login.LoginActivity
import com.kuit.conet.databinding.ActivityWithdrawDoneBinding

class WithdrawDoneActivity : AppCompatActivity()  {
    lateinit var binding : ActivityWithdrawDoneBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWithdrawDoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cvWithdrawDoneBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}