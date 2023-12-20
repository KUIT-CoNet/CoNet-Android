package com.kuit.conet.UI.Plan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kuit.conet.R
import com.kuit.conet.UI.User.WithdrawDialog
import com.kuit.conet.databinding.DialogPlanMenuBinding

class PlanMenuDialog : Fragment() {
    lateinit var binding : DialogPlanMenuBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogPlanMenuBinding.inflate(inflater, container, false)

        val plan_name = arguments?.getString("planName")
        val plan_date = arguments?.getString("planDate")
        val planId = requireArguments().getInt("planId")
        val groupId = requireArguments().getInt("groupId")

        binding.dialogPlanName.text = plan_name.toString()
        binding.dialogPlanDate.text = plan_date.toString()

        binding.clEdit.setOnClickListener {
            val intent = Intent(requireContext(), EditPlanActivity::class.java)
            intent.putExtra("planName", binding.dialogPlanName.text.toString())
            intent.putExtra("planDate", binding.dialogPlanDate.text.toString())
            intent.putExtra("planId", planId)
            intent.putExtra("groupId", groupId)
            Log.d("Pmenu>planId!!", planId.toString())
            startActivity(intent)
            parentFragmentManager.beginTransaction().remove(this).commit()
        }

        binding.clDelete.setOnClickListener {
            val deletePlanDialog = DeletePlanDialog()
            val bundle = Bundle()
            bundle.putInt("planId", planId)
            bundle.putInt("grouopId", groupId)
            deletePlanDialog.arguments = bundle
            parentFragmentManager.beginTransaction()
                .replace(R.id.fl_plan, deletePlanDialog)
                .commit()
            parentFragmentManager.beginTransaction().remove(this).commit()
        }

        binding.dialogPlanMenu.setOnClickListener {
            val fragment = parentFragmentManager.findFragmentById(R.id.fl_plan)
            if (fragment != null) {
                parentFragmentManager.beginTransaction()
                    .remove(fragment)
                    .commit()
            }
        }

        return binding.root
    }
}