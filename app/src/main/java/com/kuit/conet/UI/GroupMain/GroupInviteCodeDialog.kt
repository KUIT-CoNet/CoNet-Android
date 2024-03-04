package com.kuit.conet.UI.GroupMain

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.kuit.conet.Network.RetrofitClient
import com.kuit.conet.R
import com.kuit.conet.Utils.LIFECYCLE
import com.kuit.conet.Utils.NETWORK
import com.kuit.conet.databinding.DialogEnrollCodeBinding
import com.kuit.conet.data.dto.request.team.RequestGetInviteCode
import com.kuit.conet.data.dto.response.team.ResponseGetInviteCode
import retrofit2.Call
import retrofit2.Response


class GroupInviteCodeDialog() : DialogFragment() {

    private var _binding: DialogEnrollCodeBinding? = null
    private val binding: DialogEnrollCodeBinding
        get() = requireNotNull(_binding) { "GroupInviteCodeDialog's binding is null" }
    private val groupId: Long by lazy { arguments?.getLong(SideBar.BUNDLE_GROUP_ID) ?: 0 }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LIFECYCLE, "GroupInviteCodeDialog - onCreate() called")
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
        super.onCreateView(inflater, container, savedInstanceState)
        Log.d(LIFECYCLE, "GroupInviteCodeDialog - onCreateView() called")
        _binding = DialogEnrollCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(LIFECYCLE, "GroupInviteCodeDialog - onViewCreated() called")

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

        RetrofitClient.teamInstance.getInviteCode(
            request = RequestGetInviteCode(
                groupId
            )
        ).enqueue(object : retrofit2.Callback<ResponseGetInviteCode> {
            override fun onResponse(
                call: Call<ResponseGetInviteCode>,
                response: Response<ResponseGetInviteCode>
            ) {
                if (response.isSuccessful) {
                    Log.d(NETWORK, "GroupInviteCodeDialog - Retrofit getGroupCode()실행결과 - 성공")

                    val resp =
                        requireNotNull(response.body()) { "GroupInviteCodeDialog - getInviteCode 결과 불러오기 실패" }

                    binding.enrollCodeTv.text = resp.result.inviteCode
                    binding.expirationPeriodTv.text = " ${resp.result.codeDeadLine}"
                    binding.enrollBtn.isEnabled = true

                } else {
                    Log.d(NETWORK, "GroupInviteCodeDialog - Retrofit getGroupCode()실행결과 - 안좋음")
                }
            }

            override fun onFailure(call: Call<ResponseGetInviteCode>, t: Throwable) {
                Log.d(
                    NETWORK,
                    "GroupInviteCodeDialog - Retrofit getGroupCode()실행결과 - 실패\nbecause: $t"
                )
            }
        })
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
        Log.d(LIFECYCLE, "GroupInviteCodeDialog - onDestroyView() called")
    }

    companion object {
        const val DIALOG_TAG = "GROUP ENROLL"
    }
}