package com.kuit.conet.UI.Plan.dialog

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kuit.conet.R
import com.kuit.conet.Utils.TAG
import com.kuit.conet.databinding.ItemParticipantBinding
import com.kuit.conet.domain.entity.member.Member

class AllParticipantAdapter(
    private val context: Context,
    private val groupMembers: List<Member>
) : RecyclerView.Adapter<AllParticipantAdapter.ViewHolder>() {

    inner class ViewHolder(
        private val binding: ItemParticipantBinding,
//        private val context: Context,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Member) {
            binding.tvParticipantName.text = item.name
            Glide.with(context)
                .load(item.imageUrl) // 불러올 이미지 url
                .centerCrop()
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.icon_profile_gray) // 이미지 로딩 시작하기 전 표시할 이미지
                .error(R.drawable.icon_profile_gray) // 로딩 에러 발생 시 표시할 이미지
                .fallback(R.drawable.icon_profile_gray) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
                .into(binding.ivParticipantImage) // 이미지를 넣을 뷰
            if (item.inPlan) {
                binding.ivCheck.setImageResource(R.drawable.icon_check_circle_purple)
            } else {
                binding.ivCheck.setImageResource(R.drawable.icon_check_circle_gray)
            }

            binding.ivMinus.visibility = View.GONE
            binding.ivCheck.visibility = View.VISIBLE

            binding.ivCheck.setOnClickListener {
                item.inPlan = !item.inPlan
                if (item.inPlan) {
                    binding.ivCheck.setImageResource(R.drawable.icon_check_circle_purple)
                } else {
                    binding.ivCheck.setImageResource(R.drawable.icon_check_circle_gray)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: ItemParticipantBinding =
            ItemParticipantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)//, context, groupMembers)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(groupMembers[position])
    }

    override fun getItemCount(): Int = groupMembers.size

    fun updateEnrollList(): List<Member> {
        return groupMembers.filter { it.inPlan }
    }
}