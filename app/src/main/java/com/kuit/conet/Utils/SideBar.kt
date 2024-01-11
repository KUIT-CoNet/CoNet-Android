package com.kuit.conet.Utils

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kuit.conet.R
import com.kuit.conet.UI.Group.GroupPlusActivity
import com.kuit.conet.UI.GroupMain.DeleteGroupDialog
import com.kuit.conet.UI.GroupMain.GroupInviteCodeDialog
import com.kuit.conet.UI.GroupMain.ExitGroupDialog
import com.kuit.conet.UI.GroupMain.GroupMainActivity
import com.kuit.conet.UI.History.HistoryMainActivity
import com.kuit.conet.databinding.FragmentSidebarBinding

class SideBar(title : String?, memberCount : Int, groupId : Int, groupMainActivity: GroupMainActivity, image : String?) : Fragment(){
    lateinit var binding : FragmentSidebarBinding
    //private lateinit var itemClickListener : OnItemClickListener
    private var itemClickListener: OnItemClickListener? = null
    val title = title
    val memberCount = memberCount
    val groupId = groupId
    val groupMainActivity = groupMainActivity
    val image = image
    interface OnItemClickListener{
        fun onItemClick(option : Int) // 1 : 대기중인 약속, 2: 확정된 약속, 3:지난 약속 , 4: 히스토리
    }
    fun setOnItemClickListener(onItemClickListener : OnItemClickListener){
        itemClickListener = onItemClickListener
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSidebarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvSideGroupName.text = title
        binding.tvSidePeopleCount.text = memberCount.toString() + "명"

        binding.ivCloseBtn.setOnClickListener {
            val fragment = parentFragmentManager.findFragmentById(R.id.fl_sidebar)
            if (fragment != null) {
                parentFragmentManager.beginTransaction()
                    .remove(fragment)
                    .commit()
            }
        }

        binding.groupMainSidebar.setOnClickListener {
            Log.d("groupMainSidebar","클릭")
            val fragment = parentFragmentManager.findFragmentById(R.id.fl_sidebar)
            if (fragment != null) {
                parentFragmentManager.beginTransaction()
                    .remove(fragment)
                    .commit()
            }
        }

        binding.cvOncallPromise.setOnClickListener {
            itemClickListener?.onItemClick(1)
            destoryFragment()
        }
        binding.cvSettlePromise.setOnClickListener {
            itemClickListener?.onItemClick(2)
            destoryFragment()
        }
        binding.cvLatePromise.setOnClickListener {
            itemClickListener?.onItemClick(3)
            destoryFragment()
        }
        binding.tvOutGroup.setOnClickListener {
            val exitGroupDialog = ExitGroupDialog(groupMainActivity, groupId)
            destoryFragment()
            exitGroupDialog.show(parentFragmentManager,"ExitGroupDialog")
        }
        binding.tvDeleteGroup.setOnClickListener {
            val deleteGroupDialog = DeleteGroupDialog(groupMainActivity, groupId)
            destoryFragment()
            deleteGroupDialog.show(parentFragmentManager, "DeleteGroupDialog")
        }
        binding.ivEditNameBtn.setOnClickListener { // 모임 정보 수정
            destoryFragment()
            groupMainActivity.finish()
            val intent = Intent(requireContext(), GroupPlusActivity::class.java)
            intent.putExtra("option",1)
            intent.putExtra("groupId",groupId)
            intent.putExtra("groupName",title)
            intent.putExtra("groupImage",image)
            startActivity(intent)
        }
        binding.tvDeleteGroup.setOnClickListener {
            val deleteGroupDialog = DeleteGroupDialog(groupMainActivity, groupId)
            destoryFragment()
            deleteGroupDialog.show(parentFragmentManager, "DeleteGroupDialog")
        }
        binding.clHistorymenu.setOnClickListener { // 히스토리 메뉴
            destoryFragment()
            val intent = Intent(requireContext(), HistoryMainActivity::class.java)
            intent.putExtra("groupId",groupId)
            startActivity(intent)
        }
        binding.cvInviteCode.setOnClickListener {
            val dlg = GroupInviteCodeDialog(groupId)
            dlg.isCancelable = false
            dlg.show(parentFragmentManager, "GROUP ENROLL")
            destoryFragment()
        }
    }

    fun destoryFragment(){
        val fragment = parentFragmentManager.findFragmentById(R.id.fl_sidebar)
        if (fragment != null) {
            parentFragmentManager.beginTransaction()
                .remove(fragment)
                .commit()
        }
    }

}