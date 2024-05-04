package com.kuit.conet.UI.GroupMain.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kuit.conet.R
import com.kuit.conet.databinding.ItemMemberBinding
import com.kuit.conet.domain.entity.member.Member

class AllMembersAdapater(
    private val context: Context,
    private val memberList: List<Member>
) : RecyclerView.Adapter<AllMembersAdapater.ViewHolder>() {

    class ViewHolder(
        private val binding: ItemMemberBinding,
        private val context: Context,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: Member,
        ) {
            binding.tvMemberName.text = item.name

            Glide.with(context)
                .load(item.imageUrl) // 불러올 이미지 url
                .centerCrop()
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.icon_profile_gray) // 이미지 로딩 시작하기 전 표시할 이미지
                .error(R.drawable.icon_profile_gray) // 로딩 에러 발생 시 표시할 이미지
                .fallback(R.drawable.icon_profile_gray) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
                .into(binding.ivMemberImage) // 이미지를 넣을 뷰
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemMemberBinding =
            ItemMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, context)
    }

    override fun getItemCount(): Int = memberList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(memberList[position])
    }
}