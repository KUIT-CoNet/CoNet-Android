package com.kuit.conet.UI.GroupMain

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.DialogFragment
import com.kuit.conet.Network.EditUserName
import com.kuit.conet.Network.RetrofitInterface
import com.kuit.conet.Network.getRetrofit
import com.kuit.conet.R
import com.kuit.conet.databinding.DialogExitGroupBinding
import com.kuit.conet.getRefreshToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.suspendCoroutine

class ExitGroupDialog(groupMainActivity: GroupMainActivity, groupId : Int) : DialogFragment() {
    lateinit var binding : DialogExitGroupBinding
    val groupMainActivity = groupMainActivity
    val groupId = groupId
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle( // Background -> Transparent.
            STYLE_NORMAL,
            R.style.TransparentBottomSheetDialogFragment
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogExitGroupBinding.inflate(inflater, container, false)

        binding.exitDone.setOnClickListener { // 나가기 메뉴 눌렀을 때
            ExitGroup()
            groupMainActivity.finish()
            dismiss()
        }

        binding.exitCancel.setOnClickListener { // 취소 눌렀을 때
            // 리프레쉬 엑세스 토큰 삭제
           dismiss()
        }

        return binding.root
    }

   fun ExitGroup(){
        val exitgroup = getRetrofit().create(RetrofitInterface :: class.java)
        val refreshToken = getRefreshToken(requireContext())
        exitgroup.LeaveGroup(
            "Bearer $refreshToken",
            groupId
        ).enqueue(object : Callback<EditUserName>{
            override fun onResponse(
                call: Call<EditUserName>,
                response: Response<EditUserName>
            ) {
                val resp = response.body()// 성공했을 경우 response body불러오기
                Log.d("SIGNUP/SUCCESS", resp.toString())
                Log.d("성공!","success")
            }

            override fun onFailure(call: Call<EditUserName>, t: Throwable) {

            }

        })
   }

    companion object {
        const val TAG = "ExitGroupDialog"
    }

}