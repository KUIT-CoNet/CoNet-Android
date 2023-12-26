package com.kuit.conet.UI.Group

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.kuit.conet.Network.ResponseEnrollGroup
import com.kuit.conet.Network.RetrofitClient
import com.kuit.conet.Utils.NetworkUtil.getErrorResponse
import com.kuit.conet.Utils.NETWORK
import com.kuit.conet.databinding.DialogGroupEnrollBinding
import com.kuit.conet.getAccessToken
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
        _binding = DialogGroupEnrollBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.closeIbtn.setOnClickListener {
            dismiss()
        }

        binding.enrollBtn.setOnClickListener {
            Log.d(NETWORK, "참여하기 버튼 클릭함!")
            RetrofitClient.instance.enrollGroup(
                "Bearer ${getAccessToken(requireContext())}",
                binding.inputCodeTf.text.toString()
            ).enqueue(object : retrofit2.Callback<ResponseEnrollGroup> {
                override fun onResponse(
                    call: Call<ResponseEnrollGroup>,
                    response: Response<ResponseEnrollGroup>
                ) {
                    if (response.isSuccessful) {
                        Log.d(NETWORK, "GroupEnrollDialog - Retrofit enrollGroup()실행결과 - 성공")
                        Log.d(NETWORK, response.toString())
                        listener.onUpdateGroupList()
                        dismiss()
                    } else {
                        Log.d(NETWORK, "GroupEnrollDialog - Retrofit enrollGroup()실행결과 - 성공x")
                        val errorText = getErrorResponse(response.errorBody())!!.message
                        Log.d(NETWORK, "response.errorbody : ${errorText}")
                        binding.errorIv.visibility = View.VISIBLE
                        binding.errorTv.visibility = View.VISIBLE
                        binding.errorTv.text = errorText
                    }
                }

                override fun onFailure(call: Call<ResponseEnrollGroup>, t: Throwable) {
                    Log.d(NETWORK, "GroupEnrollDialog - Retrofit enrollGroup()실행결과 - 실패")
                    Log.d(NETWORK, t.toString())
                }

            })
        }

        binding.inputCodeTf.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {

                val inputCode = binding.inputCodeTf.text.toString()

                if(regex.matches(inputCode)) {      // 올바른 형식을 입력한 경우
                    binding.errorIv.visibility = View.INVISIBLE
                    binding.errorTv.visibility = View.INVISIBLE
                    binding.inputCodeTv.visibility = View.GONE
                    binding.enrollBtn.isEnabled = true
                    return
                }

                if (inputCode.isNullOrEmpty()) {    // 아무것도 입력하지 않은 경우
                    binding.errorIv.visibility = View.INVISIBLE
                    binding.errorTv.visibility = View.INVISIBLE
                    binding.inputCodeTv.visibility = View.VISIBLE
                    binding.enrollBtn.isEnabled = false
                    return
                }

                binding.errorIv.visibility = View.VISIBLE
                binding.errorTv.visibility = View.VISIBLE
                binding.errorTv.text = "올바른 초대코드를 입력해주세요."
                binding.inputCodeTv.visibility = View.GONE
                binding.enrollBtn.isEnabled = false
            }
        })
    }

    companion object {
        const val TAG: String = "GroupEnrollDialog"
        val regex: Regex = Regex("^[a-zA-Z0-9]{8}$")    // 영어(소문자, 대문자), 숫자 7자만 가능, 그외 불가
    }
}