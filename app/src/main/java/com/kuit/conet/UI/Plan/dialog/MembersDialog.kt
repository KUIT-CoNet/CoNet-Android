package com.kuit.conet.UI.Plan.dialog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kuit.conet.Network.Members
import com.kuit.conet.Network.ResponseGetGroupMembers
import com.kuit.conet.Network.RetrofitClient
import com.kuit.conet.R
import com.kuit.conet.Utils.TAG
import com.kuit.conet.databinding.DialogBottomSheetMembersBinding
import com.kuit.conet.Utils.getRefreshToken
import retrofit2.Call
import retrofit2.Response

class MembersDialog(
    private val context: Context,
    private val participantList: ArrayList<Members>,
    private val groupId: Int
) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogBottomSheetMembersBinding
    private lateinit var allParticipantAdapter: AllParticipantAdapter
    private var listener: BottomSheetListener? = null

    interface BottomSheetListener {
        fun onAdditionalInfoSubmitted(info: ArrayList<Members>)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DialogBottomSheetMembersBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val refreshToken = getRefreshToken(requireContext())

        RetrofitClient.instance.getGroupMembers(
            "Bearer $refreshToken",
            groupId
        ).enqueue(object : retrofit2.Callback<ResponseGetGroupMembers> {
                override fun onResponse(
                    call: Call<ResponseGetGroupMembers>,
                    response: Response<ResponseGetGroupMembers>
                ) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "MembersDialog - Retrofit getGroupMembers() 실행 결과 - 성공")

                        val allParticipantList = response.body()!!.result
                        Log.d(
                            TAG, "내용 : $allParticipantList\n" +
                                    "타입 : ${allParticipantList.javaClass}"
                        )
                        Log.d(
                            "내용", "MembersDialog all members\n" +
                                    "allmembers : $allParticipantList"
                        )

                        allParticipantAdapter =
                            AllParticipantAdapter(context, allParticipantList, participantList)
                        binding.membersRv.adapter = allParticipantAdapter
                        binding.membersRv.layoutManager = GridLayoutManager(context, 2)
                    } else {
                        Log.d(TAG, "MembersDialog - Retrofit getGroupMembers() 실행 결과 - 안좋음")
                    }
                }

                override fun onFailure(call: Call<ResponseGetGroupMembers>, t: Throwable) {
                    Log.d(TAG, "MembersDialog - Retrofit getGroupMembers() 실행 결과 - 실패")
                    Log.d(TAG, "실패 원인 : $t")
                }

            })

        binding.plusBtn.setOnClickListener {
//            TODO 서버에 해당 내용 전달하고 이전 화면에도 적용하기
            val giveList = allParticipantAdapter.updateEnrollList()
            giveList.add(Members(0, "추가하기", null))
            Log.d(
                "내용", "MembersDialog members 변경\n" +
                        "members : $giveList"
            )
            listener?.onAdditionalInfoSubmitted(giveList)
            dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle( // Background -> Transparent.
            STYLE_NORMAL,
            R.style.TransparentBottomSheetDialogFragment
        )
    }

    fun setBottomSheetListener(listener: BottomSheetListener) {
        this.listener = listener
    }
}
