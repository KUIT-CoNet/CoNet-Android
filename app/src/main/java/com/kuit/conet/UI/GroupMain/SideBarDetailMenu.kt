package com.kuit.conet.UI.GroupMain

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kuit.conet.R
import com.kuit.conet.Utils.ConfirmList
import com.kuit.conet.Utils.OncallList
import com.kuit.conet.databinding.FragmentSidebarDetailmenuBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SideBarDetailMenu(option : Int, groupId : Int) : Fragment(){
    lateinit var binding : FragmentSidebarDetailmenuBinding
    val option = option
    val groupId = groupId
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSidebarDetailmenuBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (option) {
            1 -> {
                binding.tvOncalltitle.text = "대기중인 약속"

                val oncallList = OncallList(groupId, 2)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fl_oncallpromise, oncallList)
                    .commit()
            }
            2 -> {
                binding.tvOncalltitle.text = "확정된 약속"

                val confirmList = ConfirmList(groupId, 1)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fl_oncallpromise, confirmList)
                    .commit()
            }
            else -> {
                binding.tvOncalltitle.text = "지난 약속"

                val confirmList = ConfirmList(groupId, 2)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fl_oncallpromise, confirmList)
                    .commit()
            }
        }

        binding.btnClose.setOnClickListener {
            val fragment = parentFragmentManager.findFragmentById(R.id.fl_sidebar_menu)
            if (fragment != null) {
                parentFragmentManager.beginTransaction()
                    .remove(fragment)
                    .commit()
            }
        }
    }
}