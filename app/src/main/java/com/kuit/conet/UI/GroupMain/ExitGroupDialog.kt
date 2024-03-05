package com.kuit.conet.UI.GroupMain

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
import com.kuit.conet.databinding.DialogExitGroupBinding
import com.kuit.conet.Utils.getRefreshToken
import com.kuit.conet.data.dto.request.team.RequestLeaveGroup
import com.kuit.conet.data.dto.response.team.ResponseLeaveGroup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExitGroupDialog(
    private val groupMainActivity: GroupMainActivity,
    private val groupId: Int,
) : DialogFragment() {

    private var _binding: DialogExitGroupBinding? = null
    private val binding: DialogExitGroupBinding
        get() = requireNotNull(_binding) { "ExitGroupDialog's binding is null" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LIFECYCLE, "ExitGroupDialog - onCreate() called")
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
        Log.d(LIFECYCLE, "ExitGroupDialog - onCreateView() called")
        _binding = DialogExitGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(LIFECYCLE, "ExitGroupDialog - onViewCreated() called")
        binding.exitDone.setOnClickListener {
            RetrofitClient.teamInstance.leaveGroup(
                "Bearer ${getRefreshToken(requireContext())}",
                RequestLeaveGroup(
                    groupId.toLong(),
                ),
            ).enqueue(object : Callback<ResponseLeaveGroup> {
                override fun onResponse(
                    call: Call<ResponseLeaveGroup>,
                    response: Response<ResponseLeaveGroup>
                ) {
                    if (response.isSuccessful) {
                        Log.d(NETWORK, "ExitGroupDialog - LeaveGroup() 실행결과 - 성공")
                        dismiss()
                        groupMainActivity.finish()
                    } else {
                        Log.d(NETWORK, "ExitGroupDialog - LeaveGroup() 실행결과 - 안좋음")
                    }

                }

                override fun onFailure(call: Call<ResponseLeaveGroup>, t: Throwable) {
                    Log.d(NETWORK, "ExitGroupDialog - LeaveGroup() 실행결과 - 실패\nbecause : $t")
                }
            })
        }

        binding.exitCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
        Log.d(LIFECYCLE, "ExitGroupDialog - onDestroyView() called")
    }

    companion object {
        const val DIALOG_TAG = "ExitGroupDialog"
    }

}