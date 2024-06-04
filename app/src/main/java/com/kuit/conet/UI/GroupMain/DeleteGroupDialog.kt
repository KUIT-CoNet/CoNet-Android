package com.kuit.conet.UI.GroupMain

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.kuit.conet.Network.RetrofitClient
import com.kuit.conet.R
import com.kuit.conet.UI.application.CoNetApplication
import com.kuit.conet.Utils.LIFECYCLE
import com.kuit.conet.Utils.NETWORK
import com.kuit.conet.databinding.DialogDeleteGroupBinding
import com.kuit.conet.data.dto.response.team.ResponseDeleteGroup
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeleteGroupDialog(
    private val groupMainActivity: GroupMainActivity,
    private val groupId: Long
) : DialogFragment() {

    private var _binding: DialogDeleteGroupBinding? = null
    private val binding: DialogDeleteGroupBinding
        get() = requireNotNull(_binding) { "DeleteGroupDialog's binding is null" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LIFECYCLE, "DeleteGroupDialog - onCreate() called")
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
        Log.d(LIFECYCLE, "DeleteGroupDialog - onCreateView() called")
        _binding = DialogDeleteGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(LIFECYCLE, "DeleteGroupDialog - onViewCreated() called")

        binding.tvDeleteDialogDelete.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val bearerAccessToken =
                    CoNetApplication.getInstance().getDataStore().bearerAccessToken.first()

                deleteGroup(bearerAccessToken, groupId)
            }
        }

        binding.tvDeleteDialogCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
        Log.d(LIFECYCLE, "DeleteGroupDialog - onDestroyView() called")
    }

    private fun deleteGroup(accessToken: String, groupId: Long) {
        RetrofitClient.teamInstance.deleteGroup(
            authorization = accessToken,
            teamId = groupId
        ).enqueue(object : Callback<ResponseDeleteGroup> {
            override fun onResponse(
                call: Call<ResponseDeleteGroup>,
                response: Response<ResponseDeleteGroup>
            ) {
                if (response.isSuccessful) {
                    Log.d(NETWORK, "DeleteGroupDialog - deleteGroup() 실행 결과 - 성공")
                    dismiss()
                    groupMainActivity.finish()
                } else {
                    Log.d(NETWORK, "DeleteGroupDialog - deleteGroup() 실행 결과 - 안좋음")
                }
            }

            override fun onFailure(call: Call<ResponseDeleteGroup>, t: Throwable) {
                Log.d(NETWORK, "DeleteGroupDialog - deleteGroup() 실행 결과 - 실패\nbecause : $t")
            }
        })
    }

    companion object {
        const val DIALOG_TAG = "DeleteGroupDialog"
    }

}