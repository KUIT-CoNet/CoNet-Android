package com.kuit.conet.UI.JoinMemberShip

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kuit.conet.R
import com.kuit.conet.databinding.ActivityJoinMembershipBinding

class JoinMembershipActivity : AppCompatActivity() {
    lateinit var binding : ActivityJoinMembershipBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityJoinMembershipBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(R.id.jm_fl, JoinMembership_contract_fragment())
            .commitAllowingStateLoss()

        binding.cancleButton.setOnClickListener {
            finish()
        }
    }

    fun changeProgressBar(v : Int){
        if(v == 1){ // 약관동의 받는 프래그먼트에 있을 시
            binding.jmProgressbar.progress = 50
        }
        else{ // 이름 입력 받는 프래그먼트에 있을 시
            binding.jmProgressbar.progress = 100
        }
    }
}