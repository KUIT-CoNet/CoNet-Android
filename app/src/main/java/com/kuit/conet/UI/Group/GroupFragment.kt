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
import com.kuit.conet.Utils.LIFECYCLE
import com.kuit.conet.Utils.TAG

class GroupFragment : Fragment(), View.OnClickListener, GroupEnrollDialog.GroupPlusListener {

    private var _binding: FragmentGroupBinding? = null
    private val binding: FragmentGroupBinding
        get() = requireNotNull(_binding) { "GroupFragment's binding is null" }
    private var isFabOpen = false
    private val rotateClockWiseAnim: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.rotate_clock_wise
        )
    }
    private val rotateAntiClockWiseAnim: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.rotate_anti_clock_wise
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(LIFECYCLE, "GroupFragment - onCreateView() 실행")
        _binding = FragmentGroupBinding.inflate(inflater, container, false)
        GroupFragment.binding = binding // 임시 코드 -> 리펙토링 : companion objetect에 존재하는 것 제거 필요!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(LIFECYCLE, "GroupFragment - onViewCreated() 실행")

        binding.fabGroupMain.setOnClickListener(this)
        binding.fabGroupEnroll.setOnClickListener(this)
        binding.fabGroupPlus.setOnClickListener(this)
        binding.vFabBackground.setOnClickListener(this)

        binding.vpGroupList.adapter = GroupVPAdapter(this)
        TabLayoutMediator(binding.tlGroupCategory, binding.vpGroupList) { tab, position ->
            val categoryList = arrayListOf("전체", "즐겨찾기")
            tab.text = categoryList[position]
        }.attach()

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.fab_group_main -> {
                toggleFab()
            }

            R.id.fab_group_enroll -> {
                val dlg = GroupEnrollDialog()
                dlg.setGroupAdapterListener(this)
                dlg.isCancelable = false
                dlg.show(parentFragmentManager, GroupEnrollDialog.TAG)
                toggleFab()
            }

            R.id.fab_group_plus -> {
                val intent = Intent(requireContext(), GroupPlusActivity::class.java)
                intent.putExtra("option", 0)
                startActivity(intent)
                toggleFab()
            }

            R.id.v_fab_background -> {} //화면에 대한 비활성화를 위해 존재
        }
    }

    private fun toggleFab() {
        if (isFabOpen) {
            closeFab()
        } else {
            openFab()
        }
        isFabOpen = !isFabOpen
    }

    private fun closeFab() {
        ObjectAnimator.ofFloat(binding.fabGroupEnroll, "translationY", 0f).apply { start() }
        ObjectAnimator.ofFloat(binding.fabGroupPlus, "translationY", 0f).apply { start() }
        ObjectAnimator.ofFloat(binding.tvGroupEnrollFabContent, "translationY", 0f).apply { start() }
        ObjectAnimator.ofFloat(binding.tvGroupPlusFabContent, "translationY", 0f).apply { start() }

        binding.fabGroupMain.startAnimation(rotateAntiClockWiseAnim)
        binding.fabGroupEnroll.isClickable = false
        binding.fabGroupPlus.isClickable = false
        binding.tvGroupEnrollFabContent.visibility = View.INVISIBLE
        binding.tvGroupPlusFabContent.visibility = View.INVISIBLE
        binding.vFabBackground.visibility = View.GONE
    }

    private fun openFab() {
        ObjectAnimator.ofFloat(binding.fabGroupEnroll, "translationY", -200f).apply { start() }
        ObjectAnimator.ofFloat(binding.fabGroupPlus, "translationY", -400f).apply { start() }
        ObjectAnimator.ofFloat(binding.tvGroupEnrollFabContent, "translationY", -200f).apply { start() }
        ObjectAnimator.ofFloat(binding.tvGroupPlusFabContent, "translationY", -400f).apply { start() }

        binding.fabGroupMain.startAnimation(rotateClockWiseAnim)
        binding.fabGroupEnroll.isClickable = true
        binding.fabGroupPlus.isClickable = true
        binding.tvGroupEnrollFabContent.visibility = View.VISIBLE
        binding.tvGroupPlusFabContent.visibility = View.VISIBLE
        binding.vFabBackground.visibility = View.VISIBLE
    }

    override fun onUpdateGroupList() {
        Log.d(TAG, "GroupFragment - onUpdateGroupList() called")
//        binding.groupListVp.invalidate()
//        refreshFragment(this, childFragmentManager)
        refreshFragment(this, parentFragmentManager)
    }

    private fun refreshFragment(fragment: Fragment, fragmentManager: FragmentManager) {
        Log.d(TAG, "GroupFragment - refreshFragment() called")
        var ft: FragmentTransaction = fragmentManager.beginTransaction()
        ft.detach(fragment).attach(fragment).commit()
    }

    companion object {
        lateinit var binding: FragmentGroupBinding
    }

}