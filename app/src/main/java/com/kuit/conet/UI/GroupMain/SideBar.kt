package com.kuit.conet.UI.GroupMain

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kuit.conet.R
import com.kuit.conet.UI.Group.GroupPlusActivity
import com.kuit.conet.databinding.FragmentSidebarBinding

class SideBar(
    private val title: String?,
    private val memberCount: Int,
    private val groupId: Int,
    private val groupMainActivity: GroupMainActivity,
    private val image: String?
) : Fragment(), View.OnClickListener {

    private var _binding: FragmentSidebarBinding? = null
    private val binding: FragmentSidebarBinding
        get() = requireNotNull(_binding) { "Sidebar's binding is null" }
    private var _itemClickListener: OnItemClickListener? = null
    private val itemClickListener: OnItemClickListener
        get() = requireNotNull(_itemClickListener) { "Sidebar's itemClickListener is null" }

    interface OnItemClickListener {
        fun onItemClick(option: Int) // 1 : 대기중인 약속, 2: 약속  (이전 - 1 : 대기중인 약속, 2: 확정된 약속, 3:지난 약속 , 4: 히스토리)

        fun exitSidebar()
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        _itemClickListener = onItemClickListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSidebarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvSidebarGroupName.text = title
        binding.tvSidebarGroupCount.text = memberCount.toString() + "명"

        binding.clSidebar.setOnClickListener(this)
        binding.ivSidebarClose.setOnClickListener(this)
        binding.clSidebarBackground.setOnClickListener(this)
        binding.ivSidebarGroupNameEdit.setOnClickListener(this)
        binding.llSidebarInviteCode.setOnClickListener(this)
        binding.llSidebarWaitingPlan.setOnClickListener(this)
        binding.llSidebarPlan.setOnClickListener(this)
        binding.llSidebarGroupDelete.setOnClickListener(this)
        binding.llSidebarGroupOut.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_sidebar_close, R.id.cl_sidebar_background -> {
                itemClickListener.exitSidebar()
                destoryFragment()
            }
            R.id.iv_sidebar_group_name_edit -> {
                destoryFragment()
                groupMainActivity.finish()
                val intent = Intent(requireContext(), GroupPlusActivity::class.java)
                intent.putExtra("option", 1)
                intent.putExtra("groupId", groupId)
                intent.putExtra("groupName", title)
                intent.putExtra("groupImage", image)
                startActivity(intent)
            }
            R.id.ll_sidebar_invite_code -> {
                val dlg = GroupInviteCodeDialog(groupId)
                dlg.isCancelable = false
                dlg.show(parentFragmentManager, GroupInviteCodeDialog.TAG)
                destoryFragment()
            }
            R.id.ll_sidebar_waiting_plan -> {
                itemClickListener.onItemClick(1)
                destoryFragment()
            }
            R.id.ll_sidebar_plan -> {
                itemClickListener.onItemClick(2)
                destoryFragment()
            }
            R.id.ll_sidebar_group_delete -> {
                val deleteGroupDialog = DeleteGroupDialog(groupMainActivity, groupId)
                destoryFragment()
                deleteGroupDialog.show(parentFragmentManager, DeleteGroupDialog.TAG)
            }
            R.id.ll_sidebar_group_out -> {
                val exitGroupDialog = ExitGroupDialog(groupMainActivity, groupId)
                destoryFragment()
                exitGroupDialog.show(parentFragmentManager, ExitGroupDialog.TAG)
            }
            R.id.cl_sidebar -> {}
        }
    }

    override fun onDestroyView() {
        _itemClickListener = null
        _binding = null
        super.onDestroyView()
    }

    private fun destoryFragment() {
        val fragment = parentFragmentManager.findFragmentById(R.id.fl_sidebar)
        if (fragment != null) {
            parentFragmentManager.beginTransaction()
                .remove(fragment)
                .commit()
        }
    }

}