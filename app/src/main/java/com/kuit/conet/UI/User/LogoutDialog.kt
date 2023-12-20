package com.kuit.conet.UI.User

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.kuit.conet.R
import com.kuit.conet.UI.Login.LoginActivity
import com.kuit.conet.databinding.DialogLogoutBinding
import com.kuit.conet.saveUserAccessToken
import com.kuit.conet.saveUserRefreshToken

class LogoutDialog : DialogFragment() {
    lateinit var binding : DialogLogoutBinding

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
        binding = DialogLogoutBinding.inflate(inflater, container, false)

        binding.logoutCancel.setOnClickListener {
            dismiss()
        }

        binding.logoutDone.setOnClickListener {
            // 리프레쉬 엑세스 토큰 삭제
            saveUserRefreshToken(requireContext(),"")
            saveUserAccessToken(requireContext(),"")
            // 로그인 엑티비티로 이동(이거외에 실행되고 있는 다른 모든 액티비티 종료)
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            dismiss()
        }

        return binding.root
    }
}