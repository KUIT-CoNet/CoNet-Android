//package com.kuit.conet.UI.Plan
//
//import android.annotation.SuppressLint
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.Fragment
//import com.kuit.conet.R
//import com.kuit.conet.databinding.FragmentInputTime3Binding
//
//class InputTime3 : Fragment() {
//    lateinit var binding : FragmentInputTime3Binding
//    var disable = false
//
//    lateinit var day1 : ArrayList<View>
//    lateinit var isCheckDay1 : ArrayList<Boolean>
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        binding = FragmentInputTime3Binding.inflate(inflater, container, false)
//
//        day1 = arrayListOf(
//            binding.day100, binding.day101, binding.day102, binding.day103, binding.day104,
//            binding.day105, binding.day106, binding.day107, binding.day108, binding.day109,
//            binding.day110, binding.day111, binding.day112, binding.day113, binding.day114,
//            binding.day115, binding.day116, binding.day117, binding.day118, binding.day119,
//            binding.day120, binding.day121, binding.day122, binding.day123
//        )
//        isCheckDay1  = arrayListOf(
//            false, false, false, false, false, false, false, false, false, false,
//            false, false, false, false, false, false, false, false, false, false,
//            false, false, false, false
//        )
//
//        binding.ivPrev.setOnClickListener {
//            parentFragmentManager.beginTransaction().replace(R.id.fl_plan_time_input, InputTime2()).commitAllowingStateLoss()
//        }
//
//        binding.mcvClock.setOnClickListener {
//            disable =! disable
//            updateDisable(disable)
//        }
//
//        for (i in 0..23 step(1)) { day1[i].setOnClickListener { updateCheck1(i) } }
//
//        return binding.root
//    }
//
//    @SuppressLint("ResourceAsColor")
//    fun updateDisable(disable : Boolean){
//        //가능한 시간 없음
//        if(disable){
//            binding.mcvClock.setBackgroundResource(R.drawable.view_clock_check)
//            binding.tv.setTextColor(ContextCompat.getColor(requireActivity(),
//                R.color.purpleMain
//            ))
//
//            for (i in 0..23 step(1)) {
//                day1[i].setBackgroundResource(R.drawable.view_border_disable)
//            }
//
//        }
//        //가능한 시간 없음 해제
//        else{
//            binding.mcvClock.setBackgroundResource(R.drawable.view_clock_clear)
//            binding.tv.setTextColor(ContextCompat.getColor(requireActivity(),
//                R.color.gray200
//            ))
//
//            for (i in 0..23 step(1)) {
//                day1[i].setBackgroundResource(R.drawable.view_border)
//            }
//        }
//    }
//
//    private fun updateCheck1(i: Int) {
//        if (!isCheckDay1[i]) {
//            day1[i].setBackgroundResource(R.drawable.view_border_check)
//            isCheckDay1[i] != isCheckDay1[i]
//        }
//        else {
//            day1[i].setBackgroundResource(R.drawable.view_border)
//            isCheckDay1[i] != isCheckDay1[i]
//        }
//    }
//}