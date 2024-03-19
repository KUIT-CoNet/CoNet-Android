package com.kuit.conet.UI.JoinMemberShip

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.kuit.conet.*
import com.kuit.conet.UI.ConetMainActivity
import androidx.fragment.app.Fragment
import com.kuit.conet.Network.*
import com.kuit.conet.Utils.LIFECYCLE
import com.kuit.conet.Utils.NETWORK
import com.kuit.conet.Utils.getAccessToken
import com.kuit.conet.Utils.getUsername
import com.kuit.conet.Utils.saveUsername
import com.kuit.conet.data.dto.request.auth.RequestAgreeToTermsAndConditions
import com.kuit.conet.data.dto.response.auth.ResponseAgreeToTermsAndConditions
import com.kuit.conet.databinding.FragmentNameInputBinding
import retrofit2.Call
import retrofit2.Response
import java.util.regex.Pattern


class JoinMembershipNaming : Fragment() {

    private var _binding: FragmentNameInputBinding? = null
    private val binding: FragmentNameInputBinding
        get() = requireNotNull(_binding) { "JoinMembershipNaming's binding is null" }
    private val pattern: Pattern = Pattern.compile("[!@#$%^&*(),. ?\":{}|<>]") // 특수 문자 체크
    private var edit = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNameInputBinding.inflate(inflater, container, false)
        Log.d(LIFECYCLE, "JoinMembershipNaming - onCreateView() called")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(LIFECYCLE, "JoinMembershipNaming - onViewCreated() called")

        binding.etNameInput.doAfterTextChanged {
            val matcher = pattern.matcher(it.toString())

            if (binding.ivNameInputTextCancel.visibility == View.GONE && it!!.isNotEmpty()) {
                binding.ivNameInputTextCancel.visibility = View.VISIBLE
            }

            if (matcher.find() || it!!.length >= 20) {      // 특수문자가 있거나 이름의 길이가 20이상인 경우
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
            } else if (it.isNotEmpty()) {
                binding.ivNameInputInfo1.setImageResource(R.drawable.ic_error_purple)
                binding.tvNameInputInfo1.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.texthigh
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
                        R.color.texthigh
                    )
                )
                binding.ivNameInputTextCancel.visibility = View.GONE
                binding.ivNameInputErrorEmpty.visibility = View.GONE
                binding.cvNameInputDoneBtn.setBackgroundResource(R.drawable.button_design_gray)
                binding.llNameInputUnderline.setBackgroundResource(R.color.gray200)
                edit = false
            }

        }

        binding.ivNameInputTextCancel.setOnClickListener {
            binding.etNameInput.text = null
        }

        binding.cvNameInputDoneBtn.setOnClickListener {
            if (edit) {
                saveUsername(requireContext(), binding.etNameInput.text.toString())

                sendUserInfo(getAccessToken(requireContext()), getUsername(requireContext()))

                val intent = Intent(requireContext(), ConetMainActivity::class.java)
                startActivity(intent)
                (activity as JoinMembershipActivity).finish()
            }

        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
        Log.d(LIFECYCLE, "JoinMembershipNaming - onDestroyView() called")
    }

    private fun sendUserInfo(accessToken: String, name: String) {
        RetrofitClient.authInstance.agreeToTermsAndConditions(
            authorization = "Bearer $accessToken",
            userInfo = RequestAgreeToTermsAndConditions(
                name = name,
            )
        ).enqueue(object : retrofit2.Callback<ResponseAgreeToTermsAndConditions> {
            override fun onResponse(
                call: Call<ResponseAgreeToTermsAndConditions>,
                response: Response<ResponseAgreeToTermsAndConditions>
            ) {
                if (response.isSuccessful) {
                    Log.d(NETWORK, "JoinMembershipNaming - agreeToTermsAndConditions() 실행결과 - 성공")
                } else {
                    Log.d(NETWORK, "JoinMembershipNaming - agreeToTermsAndConditions() 실행결과 - 안좋음")
                }
            }

            override fun onFailure(call: Call<ResponseAgreeToTermsAndConditions>, t: Throwable) {
                Log.d(
                    NETWORK,
                    "JoinMembershipNaming - agreeToTermsAndConditions() 실행결과 - 실패\nbecause : $t"
                )
            }
        })
    }
}