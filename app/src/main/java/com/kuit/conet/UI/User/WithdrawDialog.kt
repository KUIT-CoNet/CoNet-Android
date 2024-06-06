package com.kuit.conet.UI.User

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.kuit.conet.Network.DeleteUser
import com.kuit.conet.Network.RetrofitInterface
import com.kuit.conet.Network.getRetrofit
import com.kuit.conet.UI.application.CoNetApplication
import com.kuit.conet.databinding.DialogWithdrawBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

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
            deleteUser()
            val intent = Intent(requireContext(), WithdrawDoneActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            parentFragmentManager.beginTransaction()
                .remove(this)
                .commit()
        }

        return binding.root
    }

    private fun deleteUser() {
        val deleteUser = getRetrofit().create(RetrofitInterface::class.java)
        viewLifecycleOwner.lifecycleScope.launch {
            val bearerAccessToken =
                CoNetApplication.getInstance().getDataStore().bearerAccessToken.first()

            deleteUser.deleteUser(
                authorization = bearerAccessToken
            ).enqueue(object :
                retrofit2.Callback<DeleteUser> {
                override fun onResponse(call: Call<DeleteUser>, response: Response<DeleteUser>) {
                    if (response.isSuccessful) {
                        val resp = response.body()// 성공했을 경우 response body불러오기
                        Log.d("deleteUser/SUCCESS", resp.toString())
                        Log.d("성공!", "success")
                    } else {

                    }
                }

                override fun onFailure(call: Call<DeleteUser>, t: Throwable) {
                    Log.d("deleteUser/FAILURE", t.message.toString()) // 실패한 이유 메세지 출력
                }
            })
        }
    }

}