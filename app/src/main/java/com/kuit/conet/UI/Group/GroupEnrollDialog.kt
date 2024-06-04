package com.kuit.conet.UI.Group

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.kuit.conet.Network.RetrofitClient
import com.kuit.conet.UI.application.CoNetApplication
import com.kuit.conet.Utils.LIFECYCLE
import com.kuit.conet.Utils.NetworkUtil.getErrorResponse
import com.kuit.conet.Utils.NETWORK
import com.kuit.conet.databinding.DialogGroupEnrollBinding
import com.kuit.conet.data.dto.request.team.RequestTeamJoin
import com.kuit.conet.data.dto.response.team.ResponseTeamJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

class GroupEnrollDialog : DialogFragment() {

    private var _binding: DialogGroupEnrollBinding? = null
    private val binding: DialogGroupEnrollBinding
        get() = requireNotNull(_binding) { "GroupEnrollDialog's binding is null" }
    private var _listener: GroupPlusListener? = null
    private val listener: GroupPlusListener
        get() = requireNotNull(_listener) { "GroupEnrollDialog's GroupPlusListener is null" }

    interface GroupPlusListener {
        fun onUpdateGroupList()
    }

    fun setGroupAdapterListener(listener: GroupPlusListener) {
        this._listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        Log.d(LIFECYCLE, "GroupEnrollDialog - onCreateView() called")
        _binding = DialogGroupEnrollBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(LIFECYCLE, "GroupEnrollDialog - onViewCreated() called")

        binding.ivDialogGroupEnrollClose.setOnClickListener {
            dismiss()
        }

        binding.btnDialogGroupEnroll.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val bearerAccessToken =
                    CoNetApplication.getInstance().getDataStore().bearerAccessToken.first()
                enrollGroup(bearerAccessToken)
            }

        }

        binding.tfDialogGroupEnrollInputCode.doAfterTextChanged {
            val inviteCode = binding.tfDialogGroupEnrollInputCode.text.toString()

            if (validateInviteCode(inviteCode)) {    // 올바른 형식을 입력한 경우
                binding.ivDialogGroupEnrollError.visibility = View.INVISIBLE
                binding.tvDialogGroupEnrollError.visibility = View.INVISIBLE
                binding.tvDialogGroupEnrollInputCodeHint.visibility = View.GONE
                binding.btnDialogGroupEnroll.isEnabled = true
                return@doAfterTextChanged
            }

            if (inviteCode.isEmpty()) {    // 아무것도 입력하지 않은 경우
                binding.ivDialogGroupEnrollError.visibility = View.INVISIBLE
                binding.tvDialogGroupEnrollError.visibility = View.INVISIBLE
                binding.tvDialogGroupEnrollInputCodeHint.visibility = View.VISIBLE
                binding.btnDialogGroupEnroll.isEnabled = false
                return@doAfterTextChanged
            }

            binding.ivDialogGroupEnrollError.visibility = View.VISIBLE
            binding.tvDialogGroupEnrollError.visibility = View.VISIBLE
            binding.tvDialogGroupEnrollError.text = "올바른 초대코드를 입력해주세요."
            binding.tvDialogGroupEnrollInputCodeHint.visibility = View.GONE
            binding.btnDialogGroupEnroll.isEnabled = false
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
        Log.d(LIFECYCLE, "GroupEnrollDialog - onDestroyView() called")
    }

    private fun validateInviteCode(inviteCode: String): Boolean {
        return regex.matches(inviteCode)
    }

    private fun enrollGroup(accessToken: String) {
        RetrofitClient.teamInstance.enrollGroup(
            authorization = accessToken,
            request = RequestTeamJoin(
                binding.tfDialogGroupEnrollInputCode.text.toString()
            )
        ).enqueue(object : retrofit2.Callback<ResponseTeamJoin> {
            override fun onResponse(
                call: Call<ResponseTeamJoin>,
                response: Response<ResponseTeamJoin>
            ) {
                if (response.isSuccessful) {
                    Log.d(NETWORK, "GroupEnrollDialog - Retrofit enrollGroup()실행결과 - 성공")
                    listener.onUpdateGroupList()
                    dismiss()
                } else {
                    Log.d(NETWORK, "GroupEnrollDialog - Retrofit enrollGroup()실행결과 - 안좋음")

                    val errorText =
                        getErrorResponse(response.errorBody())?.message ?: "오류 불러오기를 실패하였습니다."
                    binding.ivDialogGroupEnrollError.visibility = View.VISIBLE
                    binding.tvDialogGroupEnrollError.visibility = View.VISIBLE
                    binding.tvDialogGroupEnrollError.text = errorText
                }
            }

            override fun onFailure(call: Call<ResponseTeamJoin>, t: Throwable) {
                Log.d(NETWORK, "GroupEnrollDialog - enrollGroup()실행결과 - 실패\nbecause : $t")
            }

        })
    }

    companion object {
        const val TAG: String = "GroupEnrollDialog"
        val regex: Regex = Regex("^[a-zA-Z0-9]{8}$")    // 영어(소문자, 대문자), 숫자 7자만 가능, 그외 불가
    }
}