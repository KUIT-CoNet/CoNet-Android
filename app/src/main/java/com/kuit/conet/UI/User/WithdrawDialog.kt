package com.kuit.conet.UI.User

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.kuit.conet.Network.DeleteUser
import com.kuit.conet.Network.RetrofitInterface
import com.kuit.conet.Network.ShowUser
import com.kuit.conet.Network.getRetrofit
import com.kuit.conet.databinding.DialogWithdrawBinding
import com.kuit.conet.getRefreshToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class WithdrawDialog : Fragment() {
    lateinit var binding: DialogWithdrawBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogWithdrawBinding.inflate(inflater, container, false)

        binding.withdrawCancel.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .remove(this)
                .commit()
        }

        binding.withdrawDone.setOnClickListener {
            DeleteUser()
            val intent = Intent(requireContext(), WithdrawDoneActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            parentFragmentManager.beginTransaction()
                .remove(this)
                .commit()
        }

        return binding.root
    }

    fun DeleteUser() {

        val deleteUser = getRetrofit().create(RetrofitInterface::class.java)
        val refreshToken = getRefreshToken(requireContext())
        deleteUser.deleteUser(
            "Bearer $refreshToken"
        ).enqueue(object :
            retrofit2.Callback<DeleteUser> {
            override fun onResponse(call: Call<DeleteUser>, response: Response<DeleteUser>) {
                if (response.isSuccessful) {
                    val resp = response.body()// 성공했을 경우 response body불러오기
                    Log.d("SIGNUP/SUCCESS", resp.toString())
                    Log.d("성공!", "success")
                } else {

                }
            }

            override fun onFailure(call: Call<DeleteUser>, t: Throwable) {
                Log.d("SIGNUP/FAILURE", t.message.toString()) // 실패한 이유 메세지 출력

            }

        })
    }

}