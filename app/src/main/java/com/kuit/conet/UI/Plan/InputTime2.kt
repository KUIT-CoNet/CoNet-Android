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
//import com.kuit.conet.databinding.FragmentInputTime2Binding
//import com.kuit.conet.databinding.FragmentTime2Binding
//
//class InputTime2 : Fragment() {
//    lateinit var binding : FragmentInputTime2Binding
//    var disable = false
//
//    lateinit var day1 : ArrayList<View>
//    lateinit var day2 : ArrayList<View>
//    lateinit var day3 : ArrayList<View>
//
//    lateinit var isCheckDay1 : ArrayList<Boolean>
//    lateinit var isCheckDay2 : ArrayList<Boolean>
//    lateinit var isCheckDay3 : ArrayList<Boolean>
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        binding = FragmentInputTime2Binding.inflate(inflater, container, false)
//
//        day1 = arrayListOf(
//            binding.day100, binding.day101, binding.day102, binding.day103, binding.day104,
//            binding.day105, binding.day106, binding.day107, binding.day108, binding.day109,
//            binding.day110, binding.day111, binding.day112, binding.day113, binding.day114,
//            binding.day115, binding.day116, binding.day117, binding.day118, binding.day119,
//            binding.day120, binding.day121, binding.day122, binding.day123
//        )
//        day2  = arrayListOf(
//            binding.day200, binding.day201, binding.day202, binding.day203, binding.day204,
//            binding.day205, binding.day206, binding.day207, binding.day208, binding.day209,
//            binding.day210, binding.day211, binding.day212, binding.day213, binding.day214,
//            binding.day215, binding.day216, binding.day217, binding.day218, binding.day219,
//            binding.day220, binding.day221, binding.day222, binding.day223
//        )
//        day3 = arrayListOf(
//            binding.day300, binding.day301, binding.day302, binding.day303, binding.day304,
//            binding.day305, binding.day306, binding.day307, binding.day308, binding.day309,
//            binding.day310, binding.day311, binding.day312, binding.day313, binding.day314,
//            binding.day315, binding.day316, binding.day317, binding.day318, binding.day319,
//            binding.day320, binding.day321, binding.day322, binding.day323
//        )
//        isCheckDay1  = arrayListOf(
//            false, false, false, false, false, false, false, false, false, false,
//            false, false, false, false, false, false, false, false, false, false,
//            false, false, false, false
//        )
//        isCheckDay2  = arrayListOf(
//            false, false, false, false, false, false, false, false, false, false,
//            false, false, false, false, false, false, false, false, false, false,
//            false, false, false, false
//        )
//        isCheckDay3  = arrayListOf(
//            false, false, false, false, false, false, false, false, false, false,
//            false, false, false, false, false, false, false, false, false, false,
//            false, false, false, false
//        )
//
//        binding.ivPrev.setOnClickListener {
//            parentFragmentManager.beginTransaction().replace(R.id.fl_plan_time_input, InputTime1()).commitAllowingStateLoss()
//        }
//
//        binding.ivNext.setOnClickListener {
//            parentFragmentManager.beginTransaction().replace(R.id.fl_plan_time_input, InputTime3()).commitAllowingStateLoss()
//        }
//
//        binding.mcvClock.setOnClickListener {
//            disable =! disable
//            updateDisable(disable)
//        }
//
//        for (i in 0..23 step(1)) {
//            day1[i].setOnClickListener { updateCheck1(i) }
//            day2[i].setOnClickListener { updateCheck2(i) }
//            day3[i].setOnClickListener { updateCheck3(i) }
//        }
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
//                day2[i].setBackgroundResource(R.drawable.view_border_disable)
//                day3[i].setBackgroundResource(R.drawable.view_border_disable)
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
//                day2[i].setBackgroundResource(R.drawable.view_border)
//                day3[i].setBackgroundResource(R.drawable.view_border)
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
//
//    private fun updateCheck2(i: Int) {
//        if (!isCheckDay2[i]) {
//            day2[i].setBackgroundResource(R.drawable.view_border_check)
//            isCheckDay2[i] != isCheckDay2[i]
//        }
//        else {
//            day2[i].setBackgroundResource(R.drawable.view_border)
//            isCheckDay2[i] != isCheckDay2[i]
//        }
//    }
//
//    private fun updateCheck3(i: Int) {
//        if (!isCheckDay3[i]) {
//            day3[i].setBackgroundResource(R.drawable.view_border_check)
//            isCheckDay3[i] != isCheckDay3[i]
//        }
//        else {
//            day3[i].setBackgroundResource(R.drawable.view_border)
//            isCheckDay3[i] != isCheckDay3[i]
//        }
//    }
//}