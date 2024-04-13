package com.kuit.conet.UI.User

import android.content.Context
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kuit.conet.Network.Members
import com.kuit.conet.Network.NoticeInfo
import com.kuit.conet.databinding.ItemNoticeBinding

class NoticeAdapter(
    private val context: Context,
    private var noticeList: ArrayList<NoticeInfo>
) : RecyclerView.Adapter<NoticeAdapter.ViewHolder>() {
    class ViewHolder (val binding : ItemNoticeBinding) :  RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NoticeInfo) {
            binding.tvItemNoticeTitle.text = item.title
            binding.tvItemNoticeContent.text = item.content
            binding.tvItemNoticeDate.text = item.date
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: ItemNoticeBinding = ItemNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(noticeList[position])
    }

    override fun getItemCount(): Int  = noticeList.size
}