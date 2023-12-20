package com.kuit.conet.UI.Group

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kuit.conet.Data.GroupList

class GroupVPAdapter(fragment:Fragment): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {

        return when(position) {
            1 -> GroupFavoriteFragment()
            else -> GroupAllFragment()
        }
    }
}