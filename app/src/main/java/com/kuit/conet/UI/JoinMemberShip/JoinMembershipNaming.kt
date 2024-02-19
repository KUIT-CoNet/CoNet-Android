package com.kuit.conet.UI.JoinMemberShip

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.kuit.conet.*
import com.kuit.conet.UI.ConetMainActivity
import androidx.fragment.app.Fragment
import com.kuit.conet.Network.*
import com.kuit.conet.databinding.FragmentNameInputBinding
import retrofit2.Call
import retrofit2.Response
import java.util.regex.Pattern


class JoinMembershipNaming : Fragment() {

    lateinit var binding: FragmentNameInputBinding
    val pattern: Pattern = Pattern.compile("[!@#$%^&*(),. ?\":{}|<>]") // 특수 문자 체크
    var edit = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNameInputBinding.inflate(inflater, container, false)
        // 이름 검사(실시간으로)
        binding.etNameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("texting", "입력전")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("texting", "입력중")
            }

            override fun afterTextChanged(p0: Editable?) {
                Log.d("texting", "입력끝")

                val matcher = pattern.matcher(p0.toString())
                if (binding.ivNameInputTextCancel.visibility == View.GONE && p0!!.isNotEmpty()) {
                    binding.ivNameInputTextCancel.visibility = View.VISIBLE
                }

                if (matcher.find() || p0!!.length >= 20) {
                    Log.d("texting", "오류")
                    binding.ivNameInputInfo1.setImageResource(R.drawable.ic_error_red)
                    binding.tvNameInputInfo1.setTextColor(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.error
                        )
                    )
                    binding.ivNameInputTextCancel.visibility = View.GONE
                    binding.ivNameInputErrorEmpty.visibility = View.VISIBLE
                    binding.cvNameInputDoneBtn.setBackgroundResource(R.drawable.button_design_gray)
                    binding.llNameInputUnderline.setBackgroundResource(R.color.error)
                    edit = false
                } else if (p0.isNotEmpty()) {
                    binding.ivNameInputInfo1.setImageResource(R.drawable.ic_error_purple)
                    binding.tvNameInputInfo1.setTextColor(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.black
                        )
                    )
                    binding.ivNameInputTextCancel.visibility = View.VISIBLE
                    binding.ivNameInputErrorEmpty.visibility = View.GONE
                    binding.cvNameInputDoneBtn.setBackgroundResource(R.drawable.button_design_purple)
                    binding.llNameInputUnderline.setBackgroundResource(R.color.gray200)
                    edit = true
                } else {
                    binding.ivNameInputInfo1.setImageResource(R.drawable.ic_error_purple)
                    binding.tvNameInputInfo1.setTextColor(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.black
                        )
                    )
                    binding.ivNameInputTextCancel.visibility = View.GONE
                    binding.ivNameInputErrorEmpty.visibility = View.GONE
                    binding.cvNameInputDoneBtn.setBackgroundResource(R.drawable.button_design_gray)
                    binding.llNameInputUnderline.setBackgroundResource(R.color.gray200)
                    edit = false
                }

            }

        })

        binding.ivNameInputTextCancel.setOnClickListener {
            binding.etNameInput.text = null
        }

        binding.cvNameInputDoneBtn.setOnClickListener { // 완료 시 이름 저장
            if (edit) {
                saveUsername(requireContext(), binding.etNameInput.text.toString())
                sendUserInfo( //서버로 정보 보내기
                    getAccessToken(requireContext()),
                    getUsername(requireContext()),
                    //getIsoption(requireContext())
                )
                val intent = Intent(requireContext(), ConetMainActivity::class.java)
                startActivity(intent)
                (activity as JoinMembershipActivity).finish()
            }

        }
        return binding.root
    }

    private fun sendUserInfo(accessToken: String, name: String) { // 유저 정보 보내는 메소드
        val signUpService = RetrofitClient.instance
        Log.d("accestoken2", "Bearer $accessToken")
        signUpService.registed(
            "Bearer $accessToken",
            sendInfo(
                name,
            )
        ).enqueue(object :
            retrofit2.Callback<registedResponse> { // 서버와 비동기적으로 데이터 주고 받을 수 있는 방법 enqueue 사용
            override fun onResponse( // 통신에 성공했을 경우
                call: Call<registedResponse>,
                response: Response<registedResponse>
            ) {
                if (response.isSuccessful) {
                    val resp = response.body()// 성공했을 경우 response body 불러오기
                    Log.d("SIGNUP/SUCCESS", resp.toString())
                }
            }

            override fun onFailure(call: Call<registedResponse>, t: Throwable) {
                Log.d("SIGNUP/FAILURE", t.message.toString()) // 실패한 이유 메세지 출력
            }
        })
    }
}