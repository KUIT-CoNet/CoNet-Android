package com.kuit.conet.UI.Home.RecyclerView

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.kuit.conet.Network.Oncall
import com.kuit.conet.Network.Plan
import com.kuit.conet.UI.Plan.detail.DetailFixActivity
import com.kuit.conet.UI.Plan.PlanTimeActivity
import com.kuit.conet.databinding.ItemTodolistBinding
import com.kuit.conet.databinding.ItemTodolistallBinding
import com.kuit.conet.domain.entity.plan.DecidedPlan
import com.kuit.conet.domain.entity.plan.UndecidedPlan

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
                startActivity(context, mIntent, null)
            }
        }
    }
}

class TodoRecyclerAdapter(private val context: Context) :
    RecyclerView.Adapter<TodoRecyclerAdapter.ViewHolder>() {
    var itemList = ArrayList<Plan>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemTodolistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])

        holder.binding.itemCl.setOnClickListener {
            val mIntent = Intent(context, DetailFixActivity::class.java)
            mIntent.putExtra("PlanId", itemList[position].planId)
            startActivity(context, mIntent, null)
        }
    }

    inner class ViewHolder(val binding: ItemTodolistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Plan) {
            binding.tvPromisstime.text = item.time
            binding.tvPromisscontent.text = item.planName
            if (item.teamName == null) {
                binding.tvPromisscategory.visibility = View.GONE
            } else {
                binding.tvPromisscategory.text = item.teamName
            }

        }
    }
}

/*class ConfirmRecyclerAdapter(private val context: Context, val option: Int, var datas:List<SidePlanInfo>) : RecyclerView.Adapter<ConfirmRecyclerAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConfirmRecyclerAdapter.ViewHolder {
        val binding = ItemCofirmlistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConfirmRecyclerAdapter.ViewHolder, position: Int) {
        holder.bind(datas[position])

        holder.binding.planCv.setOnClickListener {
            when(option){
                1 -> {
                    val mIntent = Intent(context, DetailFixActivity::class.java)
                    mIntent.putExtra("PlanId", datas[position].planId)
                    startActivity(context, mIntent, null)
                }
                0 -> {
                    Log.d(TAG, "RecyclerViewAdapter - onBindBiewHolder 에서 실행\n" +
                            "planid : ${datas[position].planId}")
                    val mIntent = Intent(context, DetailPastActivity::class.java)
                    mIntent.putExtra("PlanId", datas[position].planId)
                    startActivity(context, mIntent, null)
                }
                else -> {

                }
            }

        }
    }

    override fun getItemCount(): Int = datas.size

    inner class ViewHolder(val binding : ItemCofirmlistBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item : SidePlanInfo){
            binding.tvDate.text = item.date
            binding.tvTime.text = item.time
            if(item.dday != null){
                binding.tvRemaindate.visibility = View.VISIBLE
                binding.tvRemaindate.text = item.dday + "일 남았습니다."
            } else{
                binding.tvRemaindate.visibility = View.GONE
            }
            if(item.isRegisteredToHistory != null){
                if(item.isRegisteredToHistory){
                    binding.ivHistoryMark.visibility = View.VISIBLE
                }
                else{
                    binding.tvRemaindate.visibility = View.GONE
                }
            }
            else{
                // 아무것도 안하쥬~ㅋㅋㅋㅋ
            }
            binding.tvPromisename.text = item.planName
        }
    }

}*/
