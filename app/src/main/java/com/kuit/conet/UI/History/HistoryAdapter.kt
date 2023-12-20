package com.kuit.conet.UI.History

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kuit.conet.Network.HistoryInfo
import com.kuit.conet.R
import com.kuit.conet.databinding.ItemGroupBinding
import com.kuit.conet.databinding.ItemHistoryBinding

class HistoryAdapter(historyMainActivity: HistoryMainActivity) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    var data = ArrayList<HistoryInfo>()
    val historyMainActivity =historyMainActivity
    private lateinit var itemClickListener: OnItemClickListener

    interface OnItemClickListener{
        fun onItemClick(historyInfo: HistoryInfo)
    }

    inner class ViewHolder(val binding : ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(historyInfo: HistoryInfo){
            binding.tvHistoryDate.text = historyInfo.planDate
            binding.tvHistoryName.text = historyInfo.planName
            binding.tvHistoryNum.text = historyInfo.planMemberNum.toString() + "명"
            if(historyInfo.historyImgUrl == null || historyInfo.historyImgUrl == ""){
                binding.cvHistoryImage.visibility = GONE
            }
            else{
                binding.ivHistoryImage.visibility = View.VISIBLE
                Glide.with(historyMainActivity)
                    .load(historyInfo.historyImgUrl) // 불러올 이미지 url
                    .centerCrop()
                    .placeholder(R.drawable.profile_purple) // 이미지 로딩 시작하기 전 표시할 이미지
                    .error(R.drawable.profile_purple) // 로딩 에러 발생 시 표시할 이미지
                    .fallback(R.drawable.profile_purple) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
                    .into(binding.ivHistoryImage) // 이미지를 넣을 뷰
            }
            if(historyInfo.historyDescription == null || historyInfo.historyDescription == "null"){
                binding.tvHistoryContent.text = "내용없음"
            }
            else{
                binding.tvHistoryContent.text = historyInfo.historyDescription
            }

            binding.itemHistoryCl.setOnClickListener {
                itemClickListener.onItemClick(historyInfo)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }
}
