package com.kuit.conet.UI.User

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.kuit.conet.R
import com.kuit.conet.UI.Login.LoginActivity
import com.kuit.conet.UI.application.CoNetApplication
import com.kuit.conet.databinding.DialogLogoutBinding
import kotlinx.coroutines.launch

class LogoutDialog : DialogFragment() {

    private var _binding: DialogLogoutBinding? = null
    private val binding
        get() = requireNotNull(_binding) { "LogoutDialog's binding is null" }

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
        _binding = DialogLogoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvLogoutCancelBtn.setOnClickListener {
            dismiss()
        }

        binding.tvLogoutLogoutBtn.setOnClickListener {
            // 리프레쉬 엑세스 토큰 삭제
            viewLifecycleOwner.lifecycleScope.launch {
                CoNetApplication.getInstance().getDataStore().also {
                    it.setAccesToken("")
                    it.setRefreshToken("")
                }

                // 로그인 엑티비티로 이동(이거외에 실행되고 있는 다른 모든 액티비티 종료)
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                dismiss()
            }

//            saveUserRefreshToken(requireContext(), "")
//            saveUserAccessToken(requireContext(), "")

            // 로그인 엑티비티로 이동(이거외에 실행되고 있는 다른 모든 액티비티 종료)
            /*val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            dismiss()*/
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}