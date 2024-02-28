package com.kuit.conet.UI.Plan.list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import com.kuit.conet.Network.*
import com.kuit.conet.UI.Group.GroupVPAdapter
//import com.kuit.conet.UI.Home.RecyclerView.ConfirmRecyclerAdapter
import com.kuit.conet.databinding.FragmentConfirmlistBinding
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ConfirmList(
    private val groupId: Int,
) : Fragment() { // option이 1이면 확정 약속, 2이면 지난 약속

    private var _binding: FragmentConfirmlistBinding? = null
    private val binding: FragmentConfirmlistBinding
        get() = requireNotNull(_binding) { "ConfirmList's binding is null" }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfirmlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vpPlanList.adapter = PlanVPAdapter(this, groupId)
        TabLayoutMediator(binding.tlPlanCategory, binding.vpPlanList) { tab, position ->
            val categoryList = arrayListOf("다가오는", "지난")
            tab.text = categoryList[position]
        }.attach()

        binding.llPlanListCheckMy.setOnClickListener {

        }

    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }


}