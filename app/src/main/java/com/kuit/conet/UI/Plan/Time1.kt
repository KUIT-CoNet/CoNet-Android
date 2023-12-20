//package com.kuit.conet.UI.Plan
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import com.kuit.conet.R
//import com.kuit.conet.databinding.FragmentTime1Binding
//
//class Time1 : Fragment() {
//    lateinit var binding : FragmentTime1Binding
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        binding = FragmentTime1Binding.inflate(inflater, container, false)
//
//        binding.ivNext.setOnClickListener {
//            parentFragmentManager.beginTransaction().replace(R.id.fl_plan_time, Time2()).commitAllowingStateLoss()
//        }
//
//        return binding.root
//    }
//}