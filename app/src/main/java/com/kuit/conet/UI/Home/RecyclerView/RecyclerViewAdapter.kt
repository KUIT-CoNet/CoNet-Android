package com.kuit.conet.UI.Home.RecyclerView

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.kuit.conet.UI.Plan.detail.DetailFixActivity
import com.kuit.conet.UI.Plan.PlanTimeActivity
import com.kuit.conet.Utils.TAG
import com.kuit.conet.databinding.ItemCofirmlistBinding
import com.kuit.conet.databinding.ItemTodolistBinding
import com.kuit.conet.databinding.ItemTodolistallBinding
import com.kuit.conet.domain.entity.plan.DecidedPlan
import com.kuit.conet.domain.entity.plan.UndecidedPlan

// (Home, Sidebar) 대기중인 약속 Adapter
class AllTodoRecyclerAdapter(
    private val data: List<UndecidedPlan>,
    private val option: Int,
    private val context: Context
) : RecyclerView.Adapter<AllTodoRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemTodolistallBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, option, context)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    class ViewHolder(
        val binding: ItemTodolistallBinding,
        private val option: Int,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UndecidedPlan) {
            binding.tvPromiseStartdate.text = item.startDate
            binding.tvPromiseEnddate.text = item.endDate
            binding.tvPromisscontent.text = item.planName
            binding.tvPromisscategory.text = item.groupName

            val density: Float = context.resources.displayMetrics.density
            when (option) {
                1 -> { // 메인화면에 띄울때
                    binding.clOncalllist.elevation = 8F * density
                }

                else -> { // 사이드바 메뉴에 띄울 때
                    binding.clOncalllist.elevation = 0F * density
                }
            }

            binding.clOncalllist.setOnClickListener {
                val mIntent = Intent(context, PlanTimeActivity::class.java)
                mIntent.putExtra("planId", item.planId.toInt())
                mIntent.putExtra("planName", item.planName)
                Log.d(TAG, "onBindViewHolder: ${item.planId} + ${item.planName}")
                startActivity(context, mIntent, null)
            }
        }
    }
}

// (Home, GroupMain) 확정된 약속 - 특정 날짜의 약속
class TodoRecyclerAdapter(
    private val context: Context,
    private val itemList: List<DecidedPlan>,
) : RecyclerView.Adapter<TodoRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemTodolistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, context)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    class ViewHolder(
        private val binding: ItemTodolistBinding,
        private val context: Context,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DecidedPlan) {
            binding.tvPromisstime.text = item.time
            binding.tvPromisscontent.text = item.planName
            binding.tvPromisscategory.text = item.groupName

            binding.itemCl.setOnClickListener {
                val intent = Intent(context, DetailFixActivity::class.java)
                intent.putExtra(INTNET_PLAN_ID, item.planId.toInt())
                startActivity(context, intent, null)
            }
        }
    }

    companion object {
        const val INTNET_PLAN_ID = "planId"
    }
}

// (Sidebar) (확정된) 약속 - 다가오는, 지난
class ConfirmRecyclerAdapter(
    private val context: Context,
    private val option: Int,            // 0,else : (확정된) 약속 - 다가오는, 1 : (확정된) 약속 - 지난
    private var data: List<DecidedPlan>
) : RecyclerView.Adapter<ConfirmRecyclerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemCofirmlistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, context, option)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    class ViewHolder(
        val binding: ItemCofirmlistBinding,
        val context: Context,
        val option: Int,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DecidedPlan) {
            binding.tvDate.text = item.date
            binding.tvTime.text = item.time
            binding.tvPromisename.text = item.planName
            binding.tvRemaindate.text = "${item.dday}일 남았습니다."

            binding.planCv.setOnClickListener {
                when (option) {
                    1 -> {      // 지난 약속
//                        val intent = Intent(context, DetailPastActivity::class.java)
                        val intent = Intent(context, DetailFixActivity::class.java)
                        intent.putExtra("PlanId", item.planId.toInt())
                        startActivity(context, intent, null)
                    }

                    else -> {   // 다가오는 약속
                        val intent = Intent(context, DetailFixActivity::class.java)
                        intent.putExtra("PlanId", item.planId.toInt())
                        startActivity(context, intent, null)
                    }
                }
            }
        }
    }

    fun updateData(data: List<DecidedPlan>) {
        this.data = data
        notifyDataSetChanged()
    }
}