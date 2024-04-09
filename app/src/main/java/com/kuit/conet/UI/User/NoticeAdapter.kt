package com.kuit.conet.UI.User

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kuit.conet.Network.Members
import com.kuit.conet.Network.NoticeInfo
import com.kuit.conet.databinding.ItemNoticeBinding

class NoticeAdapter(
    private val context: Context,
    private var allList: ArrayList<NoticeInfo>,
    private var noticeList: ArrayList<NoticeInfo>
) : RecyclerView.Adapter<NoticeAdapter.ViewHolder>() {
    class ViewHolder (
        private val binding : ItemNoticeBinding,
        private val context: Context,
        private var noticeList: ArrayList<NoticeInfo>,
    ) :  RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NoticeInfo) {
            binding.tvItemNoticeTitle.text = item.title
            binding.tvItemNoticeContent.text = item.content
            binding.tvItemNoticeDate.text = item.date
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): NoticeAdapter.ViewHolder {
        val binding: ItemNoticeBinding = ItemNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, context, noticeList)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(allList[position])
    }

    override fun getItemCount(): Int  = allList.size
}