package com.kuit.conet.UI.GroupMain

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.kuit.conet.Network.ResponseGroupCode
import com.kuit.conet.Network.RetrofitClient
import com.kuit.conet.Utils.TAG
import com.kuit.conet.databinding.DialogEnrollCodeBinding
import com.kuit.conet.getAccessToken
import retrofit2.Call
import retrofit2.Response


class GroupInviteCodeDialog(val groupId: Int): DialogFragment() {

    private lateinit var binding: DialogEnrollCodeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DialogEnrollCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.closeIbtn.setOnClickListener {
            dismiss()
        }

        binding.enrollBtn.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                val shareText =
                    "[Conet안내]\n모임 초대코드가 발송되었어요!\n" + binding.enrollCodeTv.text + "\n" + binding.expirationPeriodTv.text
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)

            dismiss()
        }

        RetrofitClient.instance.getGroupCode(getAccessToken(requireContext()), groupId)
            .enqueue(object : retrofit2.Callback<ResponseGroupCode> {
                override fun onResponse(
                    call: Call<ResponseGroupCode>,
                    response: Response<ResponseGroupCode>
                ) {
                    Log.d(TAG, "response :  ${response}")
                    if (response.isSuccessful) {
                        Log.d(TAG, "GroupInviteCodeDialog - Retrofit getGroupCode()실행결과 - 성공")
                        Log.d(TAG, "response.body: ${response.body()}")
                        binding.enrollCodeTv.text = response.body()!!.result.inviteCode
                        binding.expirationPeriodTv.text =
                            "초대 코드 유효기간 : ${response.body()!!.result.codeDeadLine}"
                        binding.enrollBtn.isEnabled = true
                    } else {
                        Log.d(TAG, "GroupInviteCodeDialog - Retrofit getGroupCode()실행결과 - 안좋음")
                        Log.d(TAG, "response.errorbody: ${response.errorBody()}")
                    }
                }

                override fun onFailure(call: Call<ResponseGroupCode>, t: Throwable) {
                    Log.d(TAG, "GroupInviteCodeDialog - Retrofit getGroupCode()실행결과 - 실패")
                    Log.d(TAG, "error : $t")
                }

            })
    }
}