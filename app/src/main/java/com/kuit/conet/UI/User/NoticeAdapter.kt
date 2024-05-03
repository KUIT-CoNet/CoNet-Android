package com.kuit.conet.UI.User

import android.content.ClipData.Item
import android.content.Context
import android.opengl.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kuit.conet.Network.Members
import com.kuit.conet.Network.NoticeInfo
import com.kuit.conet.R
import com.kuit.conet.UI.Group.GroupFragment
import com.kuit.conet.UI.Group.GroupFragment.Companion.binding
import com.kuit.conet.UI.Plan.list.ConfirmList
import com.kuit.conet.databinding.ItemNoticeBinding

class NoticeAdapter(
    private val context: Context,
    private var noticeList: ArrayList<NoticeInfo>
) : RecyclerView.Adapter<NoticeAdapter.ViewHolder>() {
    private lateinit var itemClickListener: OnItemClickListener
    private lateinit var isClickedList: ArrayList<Boolean>

    interface OnItemClickListener {
        fun onItemClick(noticeInfo: NoticeInfo) {
        }
    }

    inner class ViewHolder(val binding: ItemNoticeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NoticeInfo) {
            initIsClickedList()
            binding.tvItemNoticeTitle.text = item.title
            binding.tvItemNoticeContent.text = item.content
            binding.tvItemNoticeDate.text = item.date
            //setItem(isClicked) //화면이 만들어질 때 화면 세팅

            binding.ivItemNoticeDown.setOnClickListener {
                itemClickListener.onItemClick(item)
                isClickedList[itemCount] = !isClickedList[itemCount]
                Log.d("chrin", "bind: itemCount : $itemCount")
                setItem(isClickedList[itemCount])
            }
        }

        private fun setItem(isClicked: Boolean) {
            if (!isClicked) {
                binding.ivItemNoticeDown.setImageResource(R.drawable.ic_angle_down)
                binding.tvItemNoticeContent.visibility = View.GONE
            } else {
                binding.ivItemNoticeDown.setImageResource(R.drawable.ic_angle_top)
                binding.tvItemNoticeContent.visibility = View.VISIBLE
            }
        }
    }

    fun initIsClickedList() {
        for (i in 0..<noticeList.size) isClickedList.add(element = false)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        itemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: ItemNoticeBinding =
            ItemNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        initIsClickedList()
        holder.bind(noticeList[position])
    }

    override fun getItemCount(): Int = noticeList.size
}