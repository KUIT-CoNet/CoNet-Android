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
import com.kuit.conet.Network.RetrofitClient
import com.kuit.conet.R
import com.kuit.conet.UI.GroupMain.GroupMainActivity
import com.kuit.conet.Utils.NETWORK
import com.kuit.conet.databinding.ItemGroupBinding
import com.kuit.conet.Utils.getAccessToken
import com.kuit.conet.data.dto.request.member.RequestPostBookmark
import com.kuit.conet.data.dto.response.member.ResponsePostBookmark
import com.kuit.conet.domain.entity.group.GroupSimple
import retrofit2.Call
import retrofit2.Response

class GroupAdapter(
    private val context: Context,
    private var itemList: List<GroupSimple>,
) : RecyclerView.Adapter<GroupAdapter.ViewHolder>() {

    class ViewHolder(
        val binding: ItemGroupBinding,
        private val context: Context,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GroupSimple) {

            binding.tvGroupItemTitle.text = item.name

            Glide.with(context)
                .load(item.imageUrl)
                .centerCrop()
                .placeholder(R.color.gray200) // 이미지 로딩 시작하기 전 표시할 이미지
                .error(R.color.gray200) // 로딩 에러 발생 시 표시할 이미지
                .fallback(R.color.gray200) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
                .into(binding.ivGroupItem)

            binding.ivGroupItemStar.bringToFront()
            binding.ivGroupItemStarUn.bringToFront()
            if (item.isFavorite) {
                binding.ivGroupItemStar.visibility = View.VISIBLE
                binding.ivGroupItemStarUn.visibility = View.GONE
            } else {
                binding.ivGroupItemStar.visibility = View.GONE
                binding.ivGroupItemStarUn.visibility = View.VISIBLE
            }

            binding.clGroupItem.setOnClickListener {
                val intent = Intent(context, GroupMainActivity::class.java)
                intent.putExtra(INTENT_GROUP_ID, item.id)
                startActivity(context, intent, null)        // TODO : startActivity에 대해서 알아보기!
            }

            binding.ivGroupItemStar.setOnClickListener {    // 북마크 삭제
                binding.ivGroupItemStar.visibility = View.GONE
                binding.ivGroupItemStarUn.visibility = View.VISIBLE
                item.isFavorite = false

                RetrofitClient.memberInstance.postBookmark(
                    authorization = "Bearer ${getAccessToken(context)}",
                    request = RequestPostBookmark(
                        teamId = item.id
                    )
                ).enqueue(object : retrofit2.Callback<ResponsePostBookmark> {
                    override fun onResponse(
                        call: Call<ResponsePostBookmark>,
                        response: Response<ResponsePostBookmark>
                    ) {
                        if (response.isSuccessful) {
                            val result = response.body()?.result ?: "실행결과 불러오기 실패"
                            Log.d(
                                NETWORK,
                                "GroupAdapter - Retrofit postBookmark() 북마크 삭제 실행결과 - 성공\nresult : $result"
                            )
                        } else {
                            Log.d(
                                NETWORK,
                                "GroupAdapter - Retrofit postBookmark() 북마크 삭제 실행결과 - 안좋음"
                            )
                        }
                    }

                    override fun onFailure(call: Call<ResponsePostBookmark>, t: Throwable) {
                        Log.d(NETWORK, "GroupAdapter - Retrofit postBookmark() 북마크 삭제 실행결과 - 실패")
                    }
                })
            }
            binding.ivGroupItemStarUn.setOnClickListener {  // 북마크 등록
                binding.ivGroupItemStar.visibility = View.VISIBLE
                binding.ivGroupItemStarUn.visibility = View.GONE
                item.isFavorite = true

                RetrofitClient.memberInstance.postBookmark(
                    authorization = "Bearer ${getAccessToken(context)}",
                    request = RequestPostBookmark(
                        teamId = item.id
                    )
                ).enqueue(object : retrofit2.Callback<ResponsePostBookmark> {
                    override fun onResponse(
                        call: Call<ResponsePostBookmark>,
                        response: Response<ResponsePostBookmark>
                    ) {
                        if (response.isSuccessful) {
                            val result = response.body()?.result ?: "실행결과 불러오기 실패"
                            Log.d(
                                NETWORK,
                                "GroupAdapter - Retrofit postBookmark() 북마크 등록 실행결과 - 성공\nresult : $result"
                            )
                        } else {
                            Log.d(
                                NETWORK,
                                "GroupAdapter - Retrofit postBookmark() 북마크 등록 실행결과 - 안좋음"
                            )
                        }
                    }

                    override fun onFailure(call: Call<ResponsePostBookmark>, t: Throwable) {
                        Log.d(NETWORK, "GroupAdapter - Retrofit postBookmark() 북마크 등록 실행결과 - 실패")
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

    override fun getItemCount(): Int = itemList.size

    companion object {
        const val INTENT_GROUP_ID = "group id"
    }
}