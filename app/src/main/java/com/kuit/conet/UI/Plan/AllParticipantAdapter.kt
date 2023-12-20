package com.kuit.conet.UI.Plan

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kuit.conet.Network.Members
import com.kuit.conet.R
import com.kuit.conet.Utils.TAG
import com.kuit.conet.databinding.ItemParticipantBinding

class AllParticipantAdapter(val context: Context, var allList: ArrayList<Members>, var enrollList: ArrayList<Members>)
    : RecyclerView.Adapter<AllParticipantAdapter.ViewHolder>() {

    //    외부 클래스(WebtoonAdapter)를 참조하기 위해서 내부 클래스로 만듬 (Kotlin - 클래스 내에 일반 클래스를 만들면 중첩 클래스가 된다.)
    inner class ViewHolder(val binding: ItemParticipantBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: Members){
            binding.participantNameTv.text = item.name
            Glide.with(context)
                .load(item.image) // 불러올 이미지 url
                .centerCrop()
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.profile_gray) // 이미지 로딩 시작하기 전 표시할 이미지
                .error(R.drawable.profile_gray) // 로딩 에러 발생 시 표시할 이미지
                .fallback(R.drawable.profile_gray) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
                .into(binding.participantImageIv) // 이미지를 넣을 뷰

            if(enrollList.find{ it.name == item.name } != null){
                binding.checkIv.setImageResource(R.drawable.icon_check_circle)
            } else{
                binding.checkIv.setImageResource(R.drawable.icon_check_circle_un)
            }
            binding.minusIv.visibility = View.GONE
            binding.checkIv.visibility = View.VISIBLE

            binding.allCv.setBackgroundResource(R.color.grayWhite)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllParticipantAdapter.ViewHolder {
        val binding: ItemParticipantBinding = ItemParticipantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(allList[position])

        holder.binding.checkIv.setOnClickListener{
            val item = enrollList.find { it.name == allList[position].name }
            if(item != null){
                holder.binding.checkIv.setImageResource(R.drawable.icon_check_circle_un)
                enrollList.remove(item)
                Log.d("내용", "AllParticipantAdapter members 변경\n" +
                        "members : ${enrollList}")
            } else{
                holder.binding.checkIv.setImageResource(R.drawable.icon_check_circle)
                enrollList.add(allList[position])
                Log.d("내용", "AllParticipantAdapter members 변경\n" +
                        "members : ${enrollList}")
            }
        }
    }

    override fun getItemCount(): Int = allList.size

    fun updateEnrollList(): ArrayList<Members>{
        Log.d(TAG, "AllParticipantAdapter - updateEnrollList() 실행\n" +
                "enrollList : $enrollList")
        return enrollList
    }
}