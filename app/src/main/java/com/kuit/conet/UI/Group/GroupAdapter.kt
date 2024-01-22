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
import com.kuit.conet.Utils.NETWORK
import com.kuit.conet.databinding.ItemGroupBinding
import com.kuit.conet.getAccessToken
import retrofit2.Call
import retrofit2.Response

class GroupAdapter(
    private val context: Context,
    private var itemList: List<ResultGetGroup>,
) : RecyclerView.Adapter<GroupAdapter.ViewHolder>() {

    /* // star 변경에 따른 업데이트를 위한 Listener
    private var listener: AdapterCountListener? = null

    interface AdapterCountListener {
        fun onAdditionalGroupCount(count: Int)
    }

    fun setGroupAdapterListener(listener: AdapterCountListener) {
        this.listener = listener
    }*/

    class ViewHolder(
        private val binding: ItemGroupBinding,
        private val context: Context,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ResultGetGroup) {

            binding.tvGroupItemTitle.text = item.groupName

            Glide.with(context).load(item.groupUrl).centerCrop()
                .placeholder(R.color.gray200) // 이미지 로딩 시작하기 전 표시할 이미지
                .error(R.color.gray200) // 로딩 에러 발생 시 표시할 이미지
                .fallback(R.color.gray200) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
                .into(binding.ivGroupItem)

            binding.ivGroupItemStar.bringToFront()
            binding.ivGroupItemStarUn.bringToFront()
            if (item.favoriteTag) {
                binding.ivGroupItemStar.visibility = View.VISIBLE
                binding.ivGroupItemStarUn.visibility = View.GONE
            } else {
                binding.ivGroupItemStar.visibility = View.GONE
                binding.ivGroupItemStarUn.visibility = View.VISIBLE
            }

            binding.clGroupItem.setOnClickListener {
                val mIntent = Intent(context, GroupMainActivity::class.java)
                val data = GroupData(
                    item.groupId,
                    item.groupName,
                    item.groupUrl,
                    item.groupMemberCount,
                    item.favoriteTag
                )
                mIntent.putExtra(INTENT_GROUP, data)
                startActivity(context, mIntent, null)
            }

            binding.ivGroupItemStar.setOnClickListener {
                binding.ivGroupItemStar.visibility = View.GONE
                binding.ivGroupItemStarUn.visibility = View.VISIBLE
                item.favoriteTag = false

                RetrofitClient.instance.deleteBookmark(
                    "Bearer " + getAccessToken(context), item.groupId
                ).enqueue(object : retrofit2.Callback<ResponseDeleteBookmark> {
                    override fun onResponse(
                        call: Call<ResponseDeleteBookmark>,
                        response: Response<ResponseDeleteBookmark>
                    ) {
                        if (response.isSuccessful) {
                            Log.d(
                                NETWORK,
                                "GroupAdapter - Retrofit deleteBookmark() 실행결과 - 성공\n" + "result : ${response.body()!!.result}"
                            )
                        } else {
                            Log.d(NETWORK, "GroupAdapter - Retrofit deleteBookmark() 실행결과 - 안좋음")
                        }
                    }

                    override fun onFailure(call: Call<ResponseDeleteBookmark>, t: Throwable) {
                        Log.d(NETWORK, "GroupAdapter - Retrofit deleteBookmark() 실행결과 - 실패")
                    }
                })
            }

            binding.ivGroupItemStarUn.setOnClickListener {
                binding.ivGroupItemStar.visibility = View.VISIBLE
                binding.ivGroupItemStarUn.visibility = View.GONE
                item.favoriteTag = true

                RetrofitClient.instance.enrollBookmark(
                    "Bearer " + getAccessToken(context), item.groupId
                ).enqueue(object : retrofit2.Callback<ResponseEnrollBookmark> {
                    override fun onResponse(
                        call: Call<ResponseEnrollBookmark>,
                        response: Response<ResponseEnrollBookmark>
                    ) {
                        if (response.isSuccessful) {
                            Log.d(
                                NETWORK,
                                "GroupAdapter - Retrofit enrollBookmark() 실행결과 - 성공\n" + "result : ${response.body()!!.result}"
                            )
                        } else {
                            Log.d(NETWORK, "GroupAdapter - Retrofit enrollBookmark() 실행결과 - 안좋음")
                        }
                    }

                    override fun onFailure(call: Call<ResponseEnrollBookmark>, t: Throwable) {
                        Log.d(NETWORK, "GroupAdapter - Retrofit enrollBookmark() 실행결과 - 실패")
                    }

                })
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemGroupBinding =
            ItemGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    companion object {
        const val INTENT_GROUP = "GROUP"
    }
}