package com.kuit.conet.UI.Plan.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.kuit.conet.R
import com.kuit.conet.UI.GroupMain.GroupMainActivity
import com.kuit.conet.UI.Plan.list.ConfirmList
import com.kuit.conet.Utils.LIFECYCLE
import com.kuit.conet.Utils.OncallList
import com.kuit.conet.databinding.ActivityPlanListBinding

class PlanListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlanListBinding
    private val fragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlanListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d(LIFECYCLE, "PlanListActivity - onCreate() called")

        val groupId = intent.getIntExtra(GroupMainActivity.INTENT_GROUP_ID, 0)
        val option = intent.getIntExtra(GroupMainActivity.INTENT_SIDE_OPTION, 0)

        when (option) {
            1 -> {  // 1 -> 대기중인 약속
                binding.tvSidebarDetailTitle.text = "대기중인 약속"

                val oncallList = OncallList(groupId, 2)
                fragmentManager.beginTransaction()
                    .replace(R.id.fl_plan_list, oncallList)
                    .commit()
            }

            else -> {   // 2 -> 약속
                binding.tvSidebarDetailTitle.text = "약속"

                val confirmList = ConfirmList(groupId)
                fragmentManager.beginTransaction()
                    .replace(R.id.fl_plan_list, confirmList)
                    .commit()
            }
        }

        binding.ivClose.setOnClickListener {
            finish()
        }
    }
}
