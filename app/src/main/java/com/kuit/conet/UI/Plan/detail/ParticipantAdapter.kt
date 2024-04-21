package com.kuit.conet.UI.Plan.detail

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
import com.kuit.conet.R
import com.kuit.conet.UI.Plan.dialog.MembersDialog
import com.kuit.conet.Utils.TAG
import com.kuit.conet.databinding.ItemParticipantBinding
import com.kuit.conet.domain.entity.member.Member

class ParticipantAdapter(
    private val context: Context,
    private var membersList: MutableList<Member>,
    private val option: Int,
) : RecyclerView.Adapter<ParticipantAdapter.ViewHolder>() {

    lateinit var supportFragmentManager: FragmentManager
    var planId: Long = 1

    inner class ViewHolder(
        val binding: ItemParticipantBinding,
//        private val context: Context,
//        private val option: Int,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Member) {
            binding.tvParticipantName.text = item.name
            when (option) {
//              ParticipantAdapter -> else(0) : 상세페이지, 1: 상세페이지(수정) : DetailFixActivity, DetailPastActivity, DetailEditFixActivity, DetailEditPastActivity
//              AllParticipantAdapter -> MembersDialog
                1 -> {
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
                            .load(item.imageUrl) // 불러올 이미지 url
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

                else -> {
                    Glide.with(context)
                        .load(item.imageUrl) // 불러올 이미지 url
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
                    binding.ivMinus.visibility = View.INVISIBLE
                    binding.ivCheck.visibility = View.INVISIBLE
                }
            }

            /*binding.ivMinus.setOnClickListener {      // adapterPosition 사용해서 삭제시 index 문제 발생 항상 Position이 -1이 나옴
                notifyItemRemoved(adapterPosition)
                Log.d(TAG, "ParticipantAdapter members removed in position: $adapterPosition\nmembers : $membersList")
                membersList.remove(membersList[adapterPosition])
                Log.d(TAG, "ParticipantAdapter members\nmembers : $membersList")
            }*/

            if (item.name == "추가하기") {
                binding.clParticipant.setOnClickListener {
                    val membersDialog = MembersDialog(context, planId)
                    membersDialog.setBottomSheetListener(object :
                        MembersDialog.BottomSheetListener {
                        override fun onAdditionalInfoSubmitted(info: List<Member>) {
                            membersList.clear()
                            membersList.add(Member(0, "추가하기", ""))
                            membersList.addAll(info)

                            Log.d(TAG, "ParticipantAdapter members\nmembers : $membersList")
                            notifyDataSetChanged()
                        }
                    })
                    membersDialog.show(supportFragmentManager, MembersDialog.TAG)

                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemParticipantBinding =
            ItemParticipantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)//, context, option)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(membersList[position])

        holder.binding.ivMinus.setOnClickListener {
            notifyItemRemoved(position)
            Log.d(TAG, "ParticipantAdapter members in position : $position\nmembers : $membersList")
            membersList.remove(membersList[position])
            Log.d(TAG, "ParticipantAdapter members\nmembers : $membersList")
//            notifyDataSetChanged()
        }

        /*holder.binding.clParticipant.setOnClickListener {
            if (holder.binding.tvParticipantName.text == "추가하기") {
                val membersDialog = MembersDialog(context, planId)
                membersDialog.setBottomSheetListener(object :
                    MembersDialog.BottomSheetListener {
                    override fun onAdditionalInfoSubmitted(info: List<Member>) {
                        membersList.clear()
                        membersList.add(Member(0, "추가하기", ""))
                        membersList.addAll(info)

                        Log.d(TAG, "ParticipantAdapter members\nmembers : $membersList")
                        notifyDataSetChanged()
                    }
                })
                membersDialog.show(supportFragmentManager, MembersDialog.TAG)
            }
        }*/
    }

    override fun getItemCount(): Int = membersList.size

    fun getMembersList(): List<Member> {
        return membersList.filter { it.name != "추가하기" }
    }
}