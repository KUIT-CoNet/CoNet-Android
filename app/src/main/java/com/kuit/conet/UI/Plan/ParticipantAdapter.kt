package com.kuit.conet.UI.Plan

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
import com.kuit.conet.databinding.ItemParticipantBinding

class ParticipantAdapter(val context: Context, var membersList: ArrayList<Members>, private val option:Int)
    : RecyclerView.Adapter<ParticipantAdapter.ViewHolder>(), MembersDialog.BottomSheetListener {

    lateinit var supportFragmentManager: FragmentManager
    var groupId = 1

    //    외부 클래스(WebtoonAdapter)를 참조하기 위해서 내부 클래스로 만듬 (Kotlin - 클래스 내에 일반 클래스를 만들면 중첩 클래스가 된다.)
    inner class ViewHolder(val binding: ItemParticipantBinding) : RecyclerView.ViewHolder(binding.root){
        @SuppressLint("ResourceAsColor")
        fun bind(item: Members){
            binding.participantNameTv.text = item.name
            when(option){
//                else(0) : 상세페이지, 1: 상세페이지(수정), 2: dialog, 3: history 등록하기
                1 -> {
                    binding.checkIv.visibility = View.GONE

                    if(item.name == "추가하기"){
                        binding.participantImageIv.setImageResource(R.drawable.icon_participant_plus)
                        binding.participantNameTv.setTextColor(ContextCompat.getColor(context, R.color.textDisabled))
                        binding.minusIv.visibility = View.INVISIBLE
                    } else {
                        Glide.with(context)
                            .load(item.image) // 불러올 이미지 url
                            .centerCrop()
                            .apply(RequestOptions.circleCropTransform())
                            .placeholder(R.drawable.profile_gray) // 이미지 로딩 시작하기 전 표시할 이미지
                            .error(R.drawable.profile_gray) // 로딩 에러 발생 시 표시할 이미지
                            .fallback(R.drawable.profile_gray) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
                            .into(binding.participantImageIv) // 이미지를 넣을 뷰
                        binding.participantNameTv.setTextColor(ContextCompat.getColor(context, R.color.texthigh))
                        binding.minusIv.visibility = View.VISIBLE
                    }
                }
                /*2 -> {
                    if(item in membersList){
                        binding.checkIv.setImageResource(R.drawable.icon_check_circle)
                    }
                    binding.minusIv.visibility = View.GONE
                    binding.checkIv.visibility = View.VISIBLE
                }*/
                3 -> {
                    Glide.with(context)
                        .load(item.image) // 불러올 이미지 url
                        .centerCrop()
                        .apply(RequestOptions.circleCropTransform())
                        .placeholder(R.drawable.profile_gray) // 이미지 로딩 시작하기 전 표시할 이미지
                        .error(R.drawable.profile_gray) // 로딩 에러 발생 시 표시할 이미지
                        .fallback(R.drawable.profile_gray) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
                        .into(binding.participantImageIv) // 이미지를 넣을 뷰
                    binding.participantNameTv.setTextColor(ContextCompat.getColor(context, R.color.textDisabled))
                    binding.minusIv.visibility = View.GONE
                    binding.checkIv.visibility = View.GONE
                }
                else -> {
                    Glide.with(context)
                        .load(item.image) // 불러올 이미지 url
                        .centerCrop()
                        .apply(RequestOptions.circleCropTransform())
                        .placeholder(R.drawable.profile_gray) // 이미지 로딩 시작하기 전 표시할 이미지
                        .error(R.drawable.profile_gray) // 로딩 에러 발생 시 표시할 이미지
                        .fallback(R.drawable.profile_gray) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
                        .into(binding.participantImageIv) // 이미지를 넣을 뷰

//                    binding.participantNameTv.setTextColor(R.color.texthigh)
                    binding.participantNameTv.setTextColor(ContextCompat.getColor(context, R.color.texthigh))
                    binding.minusIv.visibility = View.GONE
                    binding.checkIv.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemParticipantBinding = ItemParticipantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(membersList[position])

        holder.binding.minusIv.setOnClickListener {
            membersList.remove(membersList[position])
//            notifyItemRemoved(position) -> 이걸로 하면 오류 생김;;;
            notifyDataSetChanged()
        }

        holder.binding.participantCl.setOnClickListener {
            if(holder.binding.participantNameTv.text == "추가하기"){
                val membersDialog = MembersDialog(context, membersList, groupId)
                membersDialog.setBottomSheetListener(this)
                membersDialog.show(supportFragmentManager, membersDialog.tag)
                Log.d("내용", "ParticipantAdapter members\n" +
                        "members : ${membersList}")
                membersList.remove(membersList[position])
                Log.d("내용", "ParticipantAdapter members 변경\n" +
                        "members : ${membersList}")
            }
        }
    }

    override fun getItemCount(): Int = membersList.size

    override fun onAdditionalInfoSubmitted(info: ArrayList<Members>) {
        membersList = info
        Log.d("내용", "ParticipantAdapter members\n" +
                "members : ${membersList}")
//        membersList.add(Members(0, "추가하기", null))
        notifyDataSetChanged()
    }
}