package com.kuit.conet.UI.Plan.dialog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kuit.conet.Network.RetrofitClient
import com.kuit.conet.R
import com.kuit.conet.Utils.LIFECYCLE
import com.kuit.conet.Utils.NETWORK
import com.kuit.conet.databinding.DialogBottomSheetMembersBinding
import com.kuit.conet.data.dto.response.plan.ResponseGetPlanParticipants
import com.kuit.conet.domain.entity.member.Member
import retrofit2.Call
import retrofit2.Response

class MembersDialog(
    private val context: Context,
    private val planId: Long
) : BottomSheetDialogFragment() {

    private var _binding: DialogBotomSheetMembersBinding? = null
    private val binding: DialogBottomSheetMembersBinding
        get() = requireNotNull(_binding) { "MembersDialog's binding is null" }
    private var _listener: BottomSheetListener? = null
    private val listener: BottomSheetListener
        get() = requireNotNull(_listener) { "MembersDialog's listener is null" }

    private lateinit var allParticipantAdapter: AllParticipantAdapter


    interface BottomSheetListener {
        fun onAdditionalInfoSubmitted(info: List<Member>)
    }

    fun setBottomSheetListener(listener: BottomSheetListener) {
        this._listener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LIFECYCLE, "MembersDialog - onCreate() called")
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
        _binding = DialogBottomSheetMembersBinding.inflate(layoutInflater, container, false)
        Log.d(LIFECYCLE, "MembersDialog - onCreateView() called")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(LIFECYCLE, "MembersDialog - onViewCreated() called")

        RetrofitClient.planInstance.getPlanParticipants(
            planId = planId,
        ).enqueue(object : retrofit2.Callback<ResponseGetPlanParticipants> {
            override fun onResponse(
                call: Call<ResponseGetPlanParticipants>,
                response: Response<ResponseGetPlanParticipants>
            ) {
                if (response.isSuccessful) {
                    Log.d(NETWORK, "MembersDialog - Retrofit getPlanParticipant() 실행 결과 - 성공")

                    val resp =
                        requireNotNull(response.body()) { "MembersDialog - getPlanParticipant() 실행 결과 불러 오기 실패" }

                    allParticipantAdapter =
                        AllParticipantAdapter(
                            context,
                            resp.result.map { it.asMember() })//resp.result.map { it.asMember() }, participantList)
                    binding.membersRv.adapter = allParticipantAdapter
                } else {
                    Log.d(NETWORK, "MembersDialog - Retrofit getGroupMembers() 실행 결과 - 안좋음")
                }
            }

            override fun onFailure(call: Call<ResponseGetPlanParticipants>, t: Throwable) {
                Log.d(
                    NETWORK,
                    "MembersDialog - Retrofit getGroupMembers() 실행 결과 - 실패\nbecause : $t"
                )
            }
        })

        binding.plusBtn.setOnClickListener {
            val giveList = allParticipantAdapter.updateEnrollList()
            listener.onAdditionalInfoSubmitted(giveList)
            dismiss()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
        _listener = null
        Log.d(LIFECYCLE, "MembersDialog - onDestroyView() called")
    }

    companion object {
        const val TAG = "MembersDialog"
    }
}
