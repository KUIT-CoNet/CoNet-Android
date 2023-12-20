package com.kuit.conet.UI.Group

import com.kuit.conet.R
import com.kuit.conet.databinding.FragmentGroupBinding
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.tabs.TabLayoutMediator
import com.kuit.conet.Utils.TAG

class GroupFragment : Fragment(), View.OnClickListener, GroupEnrollDialog.GroupPlusListener{

    companion object{
        lateinit var binding: FragmentGroupBinding
    }

    private var isFabOpen = false
    private val rotateClockWiseAnim: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_clock_wise) }
    private val rotateAntiClockWiseAnim: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_anti_clock_wise) }

    lateinit var groupVPAdapter : GroupVPAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mainFab.setOnClickListener(this)
        binding.groupEnrollFab.setOnClickListener(this)
        binding.groupPlusFab.setOnClickListener(this)

        val groupVPAdapter = GroupVPAdapter(this@GroupFragment)
        binding.groupListVp.adapter = groupVPAdapter
        TabLayoutMediator(binding.categoryTl , binding.groupListVp){ tab, position ->
            val categoryList = arrayListOf("전체", "즐겨찾기")
            tab.text = categoryList[position]
        }.attach()

    }

    override fun onStart() { // 추가 하기 액티비티 갔다가 다시 돌아왔을 때 호출
        super.onStart()
        Log.d(TAG, "GroupFragment - onStart() 실행")
        /*RetrofitClient.instance.getGroup("Bearer "+getAccessToken(requireContext())).enqueue(object: retrofit2.Callback<ResponseGetGroup>{
            override fun onResponse(call: Call<ResponseGetGroup>, response: Response<ResponseGetGroup>) {
                if(response.isSuccessful){
                    Log.d(TAG, "GroupFragment - getTeam()실행 결과 - 성공")
                    Log.d(TAG, "response : $response")
                    Log.d(TAG, "response.body : ${response.body()}")

                    groupList = response.body()!!.result
                    binding.groupCountTv.text = response.body()!!.result.count().toString()

                    //        TabLayout에 대한 내용
                    val webtoonAdapter = GroupVPAdapter(this@GroupFragment)
                    binding.groupListVp.adapter = webtoonAdapter
                    TabLayoutMediator(binding.categoryTl , binding.groupListVp){ tab, position ->
                        val categoryList = arrayListOf("전체", "즐겨찾기")
                        tab.text = categoryList[position]
                    }.attach()
                } else{
                    Log.d(TAG, "GroupFragment - getTeam()실행 결과 - 안좋음")
                }
            }
            override fun onFailure(call: Call<ResponseGetGroup>, t: Throwable) {
                Log.d(TAG, "GroupFragment - getTeam()실행 결과 - 실패")
            }
        })*/

//        val groupVPAdapter = GroupVPAdapter(this)
        groupVPAdapter = GroupVPAdapter(this)
        binding.groupListVp.adapter = groupVPAdapter
        TabLayoutMediator(binding.categoryTl , binding.groupListVp){ tab, position ->
            val categoryList = arrayListOf("전체", "즐겨찾기")
            tab.text = categoryList[position]
        }.attach()
    }

    private fun toggleFab() {
        if (isFabOpen) {
            closeFab()
        } else {
            openFab()
        }
        isFabOpen = !isFabOpen
    }

    private fun closeFab(){
        ObjectAnimator.ofFloat(binding.groupEnrollFab, "translationY", 0f).apply { start() }
        ObjectAnimator.ofFloat(binding.groupPlusFab, "translationY", 0f).apply { start() }
        ObjectAnimator.ofFloat(binding.groupEnrollFabTv, "translationY", 0f).apply { start() }
        ObjectAnimator.ofFloat(binding.groupPlusFabTv, "translationY", 0f).apply { start() }
        binding.mainFab.startAnimation(rotateAntiClockWiseAnim)
        binding.groupEnrollFab.isClickable = false
        binding.groupPlusFab.isClickable = false
        binding.groupEnrollFabTv.visibility = View.INVISIBLE
        binding.groupPlusFabTv.visibility = View.INVISIBLE
        binding.fabBackgroundV.visibility = View.GONE
    }

    private fun openFab(){
        ObjectAnimator.ofFloat(binding.groupEnrollFab, "translationY", -200f).apply { start() }
        ObjectAnimator.ofFloat(binding.groupPlusFab, "translationY", -400f).apply { start() }
        ObjectAnimator.ofFloat(binding.groupEnrollFabTv, "translationY", -200f).apply { start() }
        ObjectAnimator.ofFloat(binding.groupPlusFabTv, "translationY", -400f).apply { start() }
        /*ObjectAnimator.ofFloat(binding.groupEnrollFab, "translationY", -(80f.dpToFloat(requireContext()))).apply { start() }
        ObjectAnimator.ofFloat(binding.groupPlusFab, "translationY", -(136f.dpToFloat(requireContext()))).apply { start() }
        ObjectAnimator.ofFloat(binding.groupEnrollFabTv, "translationY", -(80f.dpToFloat(requireContext()))).apply { start() }
        ObjectAnimator.ofFloat(binding.groupPlusFabTv, "translationY", -(136f.dpToFloat(requireContext()))).apply { start() }*/
        binding.mainFab.startAnimation(rotateClockWiseAnim)
        binding.groupEnrollFab.isClickable = true
        binding.groupPlusFab.isClickable = true
        binding.groupEnrollFabTv.visibility = View.VISIBLE
        binding.groupPlusFabTv.visibility = View.VISIBLE
        binding.fabBackgroundV.visibility = View.VISIBLE

    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.main_fab -> {
                toggleFab()
            }
            R.id.group_enroll_fab -> {
                val dlg = GroupEnrollDialog()
                dlg.setGroupAdapterListener(this)
                dlg.isCancelable = false
                dlg.show(parentFragmentManager, "GROUP ENROLL")
                toggleFab()
            }
            R.id.group_plus_fab -> {
                val intent = Intent(requireContext(), GroupPlusActivity::class.java)
                intent.putExtra("option", 0)
                startActivity(intent)
                toggleFab()
            }
            R.id.fab_background_v -> {
//                TODO 다른 동작 못하게 막아야 하는데 ...
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "GroupFragment - onResume() 실행")
    }

    override fun onUpdateGroupList() {
        Log.d(TAG, "실행되니?")
//        binding.groupListVp.invalidate()
//        refreshFragment(this, childFragmentManager)
        refreshFragment(this, parentFragmentManager)
    }

    fun refreshFragment(fragment: Fragment, fragmentManager: FragmentManager) {
        Log.d(TAG, "실행되니?")
        var ft: FragmentTransaction = fragmentManager.beginTransaction()
        ft.detach(fragment).attach(fragment).commit()
    }

}