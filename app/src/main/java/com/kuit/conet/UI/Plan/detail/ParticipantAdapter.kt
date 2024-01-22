package com.kuit.conet.UI.Plan.detail

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kuit.conet.Network.Members
import com.kuit.conet.R
import com.kuit.conet.UI.Plan.dialog.MembersDialog
import com.kuit.conet.Utils.TAG
import com.kuit.conet.databinding.ItemParticipantBinding

class ParticipantAdapter(
    private val context: Context,
    var membersList: ArrayList<Members>,
    private val option: Int,
) : RecyclerView.Adapter<ParticipantAdapter.ViewHolder>(), MembersDialog.BottomSheetListener {

    lateinit var supportFragmentManager: FragmentManager
    var groupId = 1

    class ViewHolder(
        val binding: ItemParticipantBinding,
        private val context: Context,
//        private val membersList: ArrayList<Members>,
        private val option: Int,
        ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ResourceAsColor")
        fun bind(item: Members) {
            binding.tvParticipantName.text = item.name
            when (option) {
//                else(0) : 상세페이지, 1: 상세페이지(수정), 2: dialog, 3: history 등록하기
                1 -> {
                    binding.ivCheck.visibility = View.GONE

                    if (item.name == "추가하기") {
                        binding.ivParticipantImage.setImageResource(R.drawable.icon_participant_plus)
                        binding.tvParticipantName.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.textDisabled
                            )
                        )
                        binding.ivMinus.visibility = View.INVISIBLE
                    } else {
                        Glide.with(context)
                            .load(item.image) // 불러올 이미지 url
                            .centerCrop()
                            .apply(RequestOptions.circleCropTransform())
                            .placeholder(R.drawable.icon_profile_gray) // 이미지 로딩 시작하기 전 표시할 이미지
                            .error(R.drawable.icon_profile_gray) // 로딩 에러 발생 시 표시할 이미지
                            .fallback(R.drawable.icon_profile_gray) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
                            .into(binding.ivParticipantImage) // 이미지를 넣을 뷰
                        binding.tvParticipantName.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.texthigh
                            )
                        )
                        binding.ivMinus.visibility = View.VISIBLE
                    }
                }
                /*2 -> {
                    if(item in membersList){
                        binding.ivCheck.setImageResource(R.drawable.icon_check_circle_purple)
                    }
                    binding.ivMinus.visibility = View.GONE
                    binding.ivCheck.visibility = View.VISIBLE
                }*/
                3 -> {
                    Glide.with(context)
                        .load(item.image) // 불러올 이미지 url
                        .centerCrop()
                        .apply(RequestOptions.circleCropTransform())
                        .placeholder(R.drawable.icon_profile_gray) // 이미지 로딩 시작하기 전 표시할 이미지
                        .error(R.drawable.icon_profile_gray) // 로딩 에러 발생 시 표시할 이미지
                        .fallback(R.drawable.icon_profile_gray) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
                        .into(binding.ivParticipantImage) // 이미지를 넣을 뷰
                    binding.tvParticipantName.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.textDisabled
                        )
                    )
                    binding.ivMinus.visibility = View.GONE
                    binding.ivCheck.visibility = View.GONE
                }

                else -> {
                    Glide.with(context)
                        .load(item.image) // 불러올 이미지 url
                        .centerCrop()
                        .apply(RequestOptions.circleCropTransform())
                        .placeholder(R.drawable.icon_profile_gray) // 이미지 로딩 시작하기 전 표시할 이미지
                        .error(R.drawable.icon_profile_gray) // 로딩 에러 발생 시 표시할 이미지
                        .fallback(R.drawable.icon_profile_gray) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
                        .into(binding.ivParticipantImage) // 이미지를 넣을 뷰

                    binding.tvParticipantName.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.texthigh
                        )
                    )
                    binding.ivMinus.visibility = View.GONE
                    binding.ivCheck.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemParticipantBinding =
            ItemParticipantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, context, option)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(membersList[position])

        holder.binding.ivMinus.setOnClickListener {
            membersList.remove(membersList[position])
            notifyDataSetChanged()
        }

        holder.binding.clParticipant.setOnClickListener {
            if (holder.binding.tvParticipantName.text == "추가하기") {
                val membersDialog = MembersDialog(context, membersList, groupId)
                membersDialog.setBottomSheetListener(this)
                membersDialog.show(supportFragmentManager, membersDialog.tag)
                Log.d(
                    TAG, "ParticipantAdapter members\n" +
                            "members : $membersList"
                )
                membersList.remove(membersList[position])
                Log.d(
                    TAG, "ParticipantAdapter members 변경\n" +
                            "members : $membersList"
                )
            }
        }
    }

    override fun getItemCount(): Int = membersList.size

    override fun onAdditionalInfoSubmitted(info: ArrayList<Members>) {
        membersList = info
        Log.d(
            TAG, "ParticipantAdapter members\n" +
                    "members : $membersList"
        )
//        membersList.add(Members(0, "추가하기", null))
        notifyDataSetChanged()
    }
}