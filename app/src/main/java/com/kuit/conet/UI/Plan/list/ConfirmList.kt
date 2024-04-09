package com.kuit.conet.UI.Plan.list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.kuit.conet.R
import com.kuit.conet.Utils.LIFECYCLE
import com.kuit.conet.databinding.FragmentConfirmlistBinding

class ConfirmList(
    private val groupId: Int,
) : Fragment() { // option이 1이면 확정 약속, 2이면 지난 약속

    private var _binding: FragmentConfirmlistBinding? = null
    private val binding: FragmentConfirmlistBinding
        get() = requireNotNull(_binding) { "ConfirmList's binding is null" }

    private var isMyPlan = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        Log.d(LIFECYCLE, "ConfirmList - onCreateView() called")
        _binding = FragmentConfirmlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(LIFECYCLE, "ConfirmList - onViewCreated() called")
        binding.vpPlanList.adapter = PlanVPAdapter(this, groupId)
        TabLayoutMediator(binding.tlPlanCategory, binding.vpPlanList) { tab, position ->
            val categoryList = arrayListOf("다가오는", "지난")
            tab.text = categoryList[position]
        }.attach()

        binding.llPlanListCheckMy.setOnClickListener {
            isMyPlan = !isMyPlan
            setMyPlan(isMyPlan)
            listener.onItemClick(isMyPlan)
        }

        binding.tlPlanCategory.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {   // Tab이 변경되었을 때
                isMyPlan = false
                setMyPlan(isMyPlan)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) { // Tab이 취소되었을 때

            }

            override fun onTabReselected(tab: TabLayout.Tab?) { // Tab이 다시 선택되었을 때

            }
        })

    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
        Log.d(LIFECYCLE, "ConfirmList - onDestroyView() called")
    }

    private fun setMyPlan(isMyPlan: Boolean) {
        if (isMyPlan) {
            binding.ivPlanListCheckMy.setImageResource(R.drawable.icon_check_circle_purple)
        } else {
            binding.ivPlanListCheckMy.setImageResource(R.drawable.icon_check_circle_gray)
        }
    }

    private var _listener: OnItemClickListener? = null
    private val listener: OnItemClickListener
        get() = requireNotNull(_listener) { "ConfirmList's listener is null" }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this._listener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(isMyPlan: Boolean)
    }

}