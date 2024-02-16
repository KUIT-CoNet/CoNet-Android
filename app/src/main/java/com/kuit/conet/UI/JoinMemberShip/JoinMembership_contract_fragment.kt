package com.kuit.conet.UI.JoinMemberShip

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kuit.conet.R
import com.kuit.conet.databinding.FragmentTermsConditionsBinding
import com.kuit.conet.saveIsoption


class JoinMembership_contract_fragment : Fragment() {
    // TODO: Rename and change types of parameters
    lateinit var binding : FragmentTermsConditionsBinding
    private var checkbox = arrayOf(0,0,0) // 왼쪽부터 개인정보 수집[필수], 이용약관[필수], 푸시 알람 수신 동의[선택] 여부 0과 1로 나타냄(0은 미선택, 1은 선택)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTermsConditionsBinding.inflate(inflater, container, false)
        //전체 동의 버튼 체크 시
        binding.ivTermsCheckAll.setOnClickListener {
            if(checkbox[0] == 0 || checkbox[1] == 0 || checkbox[2] == 0){
                checkbox[0] = 1
                checkbox[1] = 1
                checkbox[2] = 1
                binding.ivTermsCheckAll.setImageResource(R.drawable.checkbox1)
                binding.ivTermsPersonalInfo.setImageResource(R.drawable.checkbox1)
                binding.ivTermsOfUse.setImageResource(R.drawable.checkbox1)
                //binding.termsCheck3Iv.setImageResource(R.drawable.checkbox1)

            }
            else{
                checkbox[0] = 0
                checkbox[1] = 0
                checkbox[2] = 0
                binding.ivTermsCheckAll.setImageResource(R.drawable.checkbox2)
                binding.ivTermsPersonalInfo.setImageResource(R.drawable.checkbox2)
                binding.ivTermsOfUse.setImageResource(R.drawable.checkbox2)
                //binding.termsCheck3Iv.setImageResource(R.drawable.checkbox2)

            }
            btnisEnabled()
        }
        //개인 정보 동의 체크 시
        binding.ivTermsPersonalInfo.setOnClickListener {
            if(checkbox[0] == 0){
                checkbox[0] = 1
                binding.ivTermsPersonalInfo.setImageResource(R.drawable.checkbox1)

            }
            else{
                checkbox[0] = 0
                binding.ivTermsPersonalInfo.setImageResource(R.drawable.checkbox2)
            }
            btnisEnabled()
        }
        //이용약관 동의 체크 시
        binding.ivTermsOfUse.setOnClickListener {
            if(checkbox[1] == 0){
                checkbox[1] = 1
                binding.ivTermsOfUse.setImageResource(R.drawable.checkbox1)
            }
            else{
                checkbox[1] = 0
                binding.ivTermsOfUse.setImageResource(R.drawable.checkbox2)
            }
            btnisEnabled()
        }
        // 푸쉬 알람 설정 체크 시
//        binding.termsCheck3Iv.setOnClickListener {
//            if(checkbox[2] == 0){
//                checkbox[2] = 1
//                binding.termsCheck3Iv.setImageResource(R.drawable.checkbox1)
//            }
//            else{
//                checkbox[2] = 0
//                binding.termsCheck3Iv.setImageResource(R.drawable.checkbox2)
//            }
//            btnisEnabled()
//        }

        binding.cvTermsNextBtn.setOnClickListener {
            Log.d("button event","버튼 클릭")
            if(checkbox[0] == 1 && checkbox[1] == 1){
                (activity as JoinMembershipActivity).changeProgressBar(2) // 프로그래스바 변경

                saveIsoption(requireContext(), checkbox[2])
                parentFragmentManager.beginTransaction().replace(R.id.jm_fl, JoinMembership_naming()).commit()
            }

        }


        return binding.root
    }
    fun btnisEnabled(){
        if(checkbox[0] == 1 && checkbox[1] == 1 && checkbox[2] == 1){
            binding.ivTermsCheckAll.setImageResource(R.drawable.checkbox1)
        }
        else{
            binding.ivTermsCheckAll.setImageResource(R.drawable.checkbox2)
        }
        if(checkbox[0] == 1 && checkbox[1] == 1){
            binding.cvTermsNextBtn.setBackgroundResource(R.drawable.button_design_purple)
        }
        else{
            binding.cvTermsNextBtn.setBackgroundResource(R.drawable.button_design_gray)
        }
    }
}