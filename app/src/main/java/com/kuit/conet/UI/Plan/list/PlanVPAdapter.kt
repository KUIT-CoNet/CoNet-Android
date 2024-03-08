package com.kuit.conet.UI.Plan.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kuit.conet.UI.Group.GroupAllFragment
import com.kuit.conet.UI.Group.GroupFavoriteFragment

class PlanVPAdapter(
    fragment: Fragment,
    private val groupId: Int,
    ) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        val bundle = Bundle()
        bundle.putInt(BUNDLE_GROUP_ID, groupId)
        bundle.putInt(BUNDLE_OPTION, position)

        return when (position) {
            1 -> {      // 지난 약속
                val fragment = PlanListFragment()
                fragment.arguments = bundle
                fragment
            }

            else -> {   // 다가오는 약속
                val fragment = PlanListFragment()
                fragment.arguments = bundle
                fragment
            }
        }
    }

    companion object {
        const val BUNDLE_GROUP_ID = "GROUPID"
        const val BUNDLE_OPTION = "OPTION"
    }
}