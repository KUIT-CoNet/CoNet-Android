package com.kuit.conet.UI.GroupMain.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kuit.conet.Network.RetrofitClient
import com.kuit.conet.R
import com.kuit.conet.UI.GroupMain.GroupMainActivity
import com.kuit.conet.UI.application.CoNetApplication
import com.kuit.conet.Utils.LIFECYCLE
import com.kuit.conet.Utils.NETWORK
import com.kuit.conet.data.dto.response.team.ResponseGetGroupMembers
import com.kuit.conet.databinding.DialogAllMembersBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllMembersDialog : BottomSheetDialogFragment() {

    private var _binding: DialogAllMembersBinding? = null
    private val binding: DialogAllMembersBinding
        get() = requireNotNull(_binding) { "AllMembersDialog's binding is null" }

    private var groupId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LIFECYCLE, "AllMembersDialog - onCreate() called")
        arguments?.let {
            // TODO : 이전 화면에서 GroupId 받아오기
            groupId = it.getLong(GroupMainActivity.BUNDLE_GROUP_ID)
        }

        setStyle( // Background -> Transparent.
            STYLE_NORMAL,
            R.style.TransparentBottomSheetDialogFragment
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(LIFECYCLE, "AllMembersDialog - onCreateView() called")
        _binding = DialogAllMembersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(LIFECYCLE, "AllMembersDialog - onViewCreated() called")

        viewLifecycleOwner.lifecycleScope.launch {
            val bearerAccessToken =
                CoNetApplication.getInstance().getDataStore().bearerAccessToken.first()
            getGroupMembers(bearerAccessToken, groupId)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
        Log.d(LIFECYCLE, "AllMembersDialog - onDestroyView() called")
    }

    private fun getGroupMembers(accessToken: String, groupId: Long) {
        RetrofitClient.teamInstance.getGroupMembers(
            authorization = accessToken,
            groupId = groupId,
        ).enqueue(object : Callback<ResponseGetGroupMembers> {
            override fun onResponse(
                call: Call<ResponseGetGroupMembers>,
                response: Response<ResponseGetGroupMembers>
            ) {
                if (response.isSuccessful) {
                    Log.d(NETWORK, "AllMembersDialog - Retrofit getGroupMembers() 실행결과 - 성공")

                    val resp =
                        requireNotNull(response.body()) { "AllMembersDialog - getGroupMembers() - onResponse() - response.body() is null" }

                    val memberList = resp.result
                        .map { it.asMember() }

                    binding.rvMembersList.adapter = AllMembersAdapater(
                        context = requireContext(),
                        memberList = memberList // resp.result.map { it.asMember() },
                    )

                } else {
                    Log.d(NETWORK, "AllMembersDialog - Retrofit getGroupMembers() 실행결과 - 안좋음")
                }
            }

            override fun onFailure(call: Call<ResponseGetGroupMembers>, t: Throwable) {
                Log.d(
                    NETWORK,
                    "AllMembersDialog - Retrofit getGroupMembers() 실행결과 - 실패\nbecause : $t"
                )
            }
        })
    }

    companion object {
        const val TAG = "AllMembersDialog"
    }
}