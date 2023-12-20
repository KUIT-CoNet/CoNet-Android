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
import com.kuit.conet.Utils.TAG
import com.kuit.conet.databinding.DialogGroupEnrollBinding
import com.kuit.conet.getAccessToken
import retrofit2.Call
import retrofit2.Response

class GroupEnrollDialog: DialogFragment() {

    private lateinit var binding: DialogGroupEnrollBinding

    private var listener: GroupPlusListener? = null

    interface GroupPlusListener {
        fun onUpdateGroupList()
    }

    fun setGroupAdapterListener(listener: GroupPlusListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DialogGroupEnrollBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.closeIbtn.setOnClickListener {
            dismiss()
        }

        binding.enrollBtn.setOnClickListener {
            Log.d(TAG, "참여하기 버튼 클릭함!")
            RetrofitClient.instance.enrollGroup("Bearer ${getAccessToken(requireContext())}", binding.inputCodeTf.text.toString()).enqueue(object: retrofit2.Callback<ResponseEnrollGroup>{
                override fun onResponse(
                    call: Call<ResponseEnrollGroup>,
                    response: Response<ResponseEnrollGroup>
                ) {
                    if(response.isSuccessful) {
                        Log.d(TAG, "GroupEnrollDialog - Retrofit enrollGroup()실행결과 - 성공")
                        Log.d(TAG, response.toString())
                        listener?.onUpdateGroupList()
                        dismiss()
                    } else{
                        Log.d(TAG, "GroupEnrollDialog - Retrofit enrollGroup()실행결과 - 성공x")
                        val errorText = getErrorResponse(response.errorBody())!!.message
                        Log.d(TAG, "response.errorbody : ${errorText}")
                        binding.errorIv.visibility = View.VISIBLE
                        binding.errorTv.visibility = View.VISIBLE
                        binding.errorTv.text = errorText
                    }
                }

                override fun onFailure(call: Call<ResponseEnrollGroup>, t: Throwable) {
                    Log.d(TAG, "GroupEnrollDialog - Retrofit enrollGroup()실행결과 - 실패")
                    Log.d(TAG, t.toString())

                }

            })
        }

        binding.inputCodeTf.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                when(binding.inputCodeTf.text!!.count()){
                    0 -> {
                        binding.errorIv.visibility = View.INVISIBLE
                        binding.errorTv.visibility = View.INVISIBLE
                        binding.inputCodeTv.visibility = View.VISIBLE
                        binding.enrollBtn.isEnabled = false
                    }
                    in 1..7 ->{
                        binding.errorIv.visibility = View.INVISIBLE
                        binding.errorTv.visibility = View.INVISIBLE
                        binding.inputCodeTv.visibility = View.GONE
                        binding.enrollBtn.isEnabled = false
                    }
                    8 ->{
                        binding.errorIv.visibility = View.INVISIBLE
                        binding.errorTv.visibility = View.INVISIBLE
                        binding.inputCodeTv.visibility = View.GONE
                        binding.enrollBtn.isEnabled = true
                    }
                    else -> {
                        binding.errorIv.visibility = View.VISIBLE
                        binding.errorTv.visibility = View.VISIBLE
                        binding.errorTv.text = "올바른 초대코드를 입력해주세요."
                        binding.inputCodeTv.visibility = View.GONE
                        binding.enrollBtn.isEnabled = false
                    }
                }
            }
        })

        binding.inputCodeTf.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus){
                if(binding.inputCodeTf.error == null){
                    if(binding.inputCodeTf.text!!.isEmpty()){
                        binding.inputCodeTv.visibility = View.VISIBLE
                    } else{
                        binding.inputCodeTv.visibility = View.GONE
                    }
                }
            }
        }
    }
}