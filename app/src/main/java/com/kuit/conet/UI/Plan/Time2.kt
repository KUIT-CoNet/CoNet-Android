//package com.kuit.conet.UI.Plan
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import com.kuit.conet.R
//import com.kuit.conet.databinding.FragmentTime2Binding
//
//class Time2 : Fragment() {
//    lateinit var binding : FragmentTime2Binding
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        binding = FragmentTime2Binding.inflate(inflater, container, false)
//
//        binding.ivPrev.setOnClickListener {
//            parentFragmentManager.beginTransaction().replace(R.id.fl_plan_time, Time1()).commitAllowingStateLoss()
//        }
//
//        binding.ivNext.setOnClickListener {
//            parentFragmentManager.beginTransaction().replace(R.id.fl_plan_time, Time3()).commitAllowingStateLoss()
//        }
//
//        return binding.root
//    }
//}