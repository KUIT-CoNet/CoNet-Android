package com.kuit.conet.UI.Group

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kuit.conet.Network.ResponseDeleteBookmark
import com.kuit.conet.Network.ResponseEnrollBookmark
import com.kuit.conet.Network.ResultGetGroup
import com.kuit.conet.Network.RetrofitClient
import com.kuit.conet.R
import com.kuit.conet.UI.GroupMain.GroupMainActivity
import com.kuit.conet.Utils.TAG
import com.kuit.conet.databinding.ItemGroupBinding
import com.kuit.conet.getAccessToken
import retrofit2.Call
import retrofit2.Response

class GroupAdapter(val context: Context, var itemList: ArrayList<ResultGetGroup>)
    : RecyclerView.Adapter<GroupAdapter.ViewHolder>() {

    /*private var listener: AdapterCountListener? = null

    interface AdapterCountListener {
        fun onAdditionalGroupCount(count: Int)
    }

    fun setGroupAdapterListener(listener: AdapterCountListener) {
        this.listener = listener
    }*/

    //    외부 클래스(WebtoonAdapter)를 참조하기 위해서 내부 클래스로 만듬 (Kotlin - 클래스 내에 일반 클래스를 만들면 중첩 클래스가 된다.)
    inner class ViewHolder(val binding: ItemGroupBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: ResultGetGroup){
            binding.itemGroupTitleTv.text = item.groupName
            Glide.with(context)
                .load(item.groupUrl)
                .centerCrop()
                .placeholder(R.color.gray200) // 이미지 로딩 시작하기 전 표시할 이미지
                .error(R.color.gray200) // 로딩 에러 발생 시 표시할 이미지
                .fallback(R.color.gray200) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
                .into(binding.itemGroupIv)

            if(item.favoriteTag){
                binding.itemGroupStarIv.visibility = View.VISIBLE
                binding.itemGroupStarUnIv.visibility = View.GONE
            } else{
                binding.itemGroupStarIv.visibility = View.GONE
                binding.itemGroupStarUnIv.visibility = View.VISIBLE
            }

            if(item.newTag){
                binding.newTagTv.visibility = View.VISIBLE
            } else {
                binding.newTagTv.visibility = View.INVISIBLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupAdapter.ViewHolder {
        val binding: ItemGroupBinding = ItemGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.itemGroupStarIv.bringToFront()
        binding.itemGroupStarUnIv.bringToFront()
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupAdapter.ViewHolder, position: Int) {
        holder.bind(itemList[position])
        val binding = holder.binding

        binding.itemCl.setOnClickListener {
            val mIntent = Intent(context, GroupMainActivity::class.java)
            mIntent.putExtra("GroupName",itemList[position].groupName)
            mIntent.putExtra("GroupImg",itemList[position].groupUrl)
            mIntent.putExtra("GroupMemberCount",itemList[position].groupMemberCount)
            mIntent.putExtra("GroupId",itemList[position].groupId)
            mIntent.putExtra("GroupFavorite", itemList[position].favoriteTag)
            startActivity(context, mIntent, null)
        }

        binding.itemGroupStarIv.setOnClickListener {
            binding.itemGroupStarIv.visibility = View.GONE
            binding.itemGroupStarUnIv.visibility = View.VISIBLE
            itemList[position].favoriteTag = false
            RetrofitClient.instance.deleteBookmark("Bearer " + getAccessToken(context), itemList[position].groupId).enqueue(object: retrofit2.Callback<ResponseDeleteBookmark>{
                override fun onResponse(
                    call: Call<ResponseDeleteBookmark>,
                    response: Response<ResponseDeleteBookmark>
                ) {
                    if(response.isSuccessful){
                        Log.d(TAG, "GroupAdapter - Retrofit deleteBookmark() 실행결과 - 성공\n" +
                                "result : ${response.body()!!.result}")
                    } else {
                        Log.d(TAG, "GroupAdapter - Retrofit deleteBookmark() 실행결과 - 안좋음")
                    }
                }
                override fun onFailure(call: Call<ResponseDeleteBookmark>, t: Throwable) {
                    Log.d(TAG, "GroupAdapter - Retrofit deleteBookmark() 실행결과 - 실패")
                }
            })
        }
        binding.itemGroupStarUnIv.setOnClickListener {
            binding.itemGroupStarIv.visibility = View.VISIBLE
            binding.itemGroupStarUnIv.visibility = View.GONE
            itemList[position].favoriteTag = true
            RetrofitClient.instance.enrollBookmark("Bearer " + getAccessToken(context), itemList[position].groupId).enqueue(object: retrofit2.Callback<ResponseEnrollBookmark>{
                override fun onResponse(
                    call: Call<ResponseEnrollBookmark>,
                    response: Response<ResponseEnrollBookmark>
                ) {
                    if(response.isSuccessful){
                        Log.d(TAG, "GroupAdapter - Retrofit enrollBookmark() 실행결과 - 성공\n" +
                                "result : ${response.body()!!.result}")
                    } else {
                        Log.d(TAG, "GroupAdapter - Retrofit enrollBookmark() 실행결과 - 안좋음")
                    }
                }

                override fun onFailure(call: Call<ResponseEnrollBookmark>, t: Throwable) {
                    Log.d(TAG, "GroupAdapter - Retrofit enrollBookmark() 실행결과 - 실패")
                }

            })
        }
    }

    override fun getItemCount(): Int{
        return itemList.size
    }
}