package com.kuit.conet.UI.Plan.detail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.kuit.conet.Network.RetrofitClient
import com.kuit.conet.R
import com.kuit.conet.UI.Plan.dialog.DeletingPlanDialog
import com.kuit.conet.UI.Plan.dialog.EditTrashDialog
import com.kuit.conet.Utils.LIFECYCLE
import com.kuit.conet.Utils.NETWORK
import com.kuit.conet.Utils.TAG
import com.kuit.conet.databinding.ActivityDetailFixBinding
import com.kuit.conet.Utils.getRefreshToken
import com.kuit.conet.data.dto.response.plan.ResponseDeletePlan
import com.kuit.conet.data.dto.response.plan.ResponseGetPlanDetail
import com.kuit.conet.domain.entity.plan.PlanDetail
import retrofit2.Call
import retrofit2.Response

class DetailFixActivity
    : AppCompatActivity(), View.OnClickListener, EditTrashDialog.OnDialogClickListener,
    DeletingPlanDialog.DialogClickListener {

    private lateinit var binding: ActivityDetailFixBinding

    private var planId: Long = 0
    private lateinit var data: PlanDetail

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(LIFECYCLE, "DetailFixActivity - onCreate() 실행")
        super.onCreate(savedInstanceState)
        binding = ActivityDetailFixBinding.inflate(layoutInflater)
        setContentView(binding.root)

        planId = intent.getIntExtra("PlanId", 0).toLong()

        if (planId == 0L) {
            Log.d(TAG, "DetailPastActiity - onCreate()\nplanId가 넘어오지 않았습니다.")
            finish()
        }

        binding.ivDetailFixMenu.setOnClickListener(this)
        binding.ivDetailFixBack.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_detail_fix_menu -> {
                val bottomSheet = EditTrashDialog()
                bottomSheet.setOnDialogClickListener(this)
                bottomSheet.show(supportFragmentManager, EditTrashDialog.TAG)
            }

            R.id.iv_detail_fix_back -> {
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(LIFECYCLE, "DetailFixActivity - onResume() 실행")

        RetrofitClient.planInstance.getPlanDetail(
            authorization = "Bearer ${getRefreshToken(this)}",
            planId = planId
        ).enqueue(object : retrofit2.Callback<ResponseGetPlanDetail> {
            override fun onResponse(
                call: Call<ResponseGetPlanDetail>,
                response: Response<ResponseGetPlanDetail>
            ) {
                if (response.isSuccessful) {
                    Log.d(NETWORK, "DetailFixActivity - getPlanDetail() 실행 결과 - 성공")

                    val resp =
                        requireNotNull(response.body()) { "DetailFixActivity - getPlanDetail() 실행 결과 불러오기 실패" }

                    data = resp.result.asPlanDetail()

                    binding.tfDetailFixPlanName.setText(resp.result.planName)
                    binding.tfDetailFixPlanDate.setText(resp.result.date)
                    binding.tfDetailFixPlanTime.setText(resp.result.time)

                    val participantList = resp.result.members
                    val participantAdapter = ParticipantAdapter(
                        context = this@DetailFixActivity,
                        membersList = participantList.map { it.asMember() }.toMutableList(),
                        option = 0
                    )
                    participantAdapter.planId = planId
                    binding.rvDetailFixPlanParticipants.adapter = participantAdapter

                } else {
                    Log.d(NETWORK, "DetailPastActivity - Retrofit getPlanDetail() 실행 결과 - 안좋음")
                }
            }

            override fun onFailure(call: Call<ResponseGetPlanDetail>, t: Throwable) {
                Log.d(
                    NETWORK,
                    "DetailPastActivity - Retrofit getPlanDetail() 실행 결과 - 실패\nbecause : $t"
                )
            }
        })
    }

    override fun onEditButtonClick() {      // EditTrashDialog - 수정하기 버튼 클릭
        val intent = Intent(this, DetailEditFixActivity::class.java)
        intent.putExtra(INTENT_TAG, data)
        startActivity(intent)
    }

    override fun onTrashButtonClick() {     // EditTrashDialog - 삭제하기 버튼 클릭
        val deletingPlanDialog = DeletingPlanDialog()
        deletingPlanDialog.setListener(this)
        deletingPlanDialog.show(supportFragmentManager, DeletingPlanDialog.TAG)
    }

    override fun onDeleteButtonClick() {    // DeletingPlanDialog - 삭제하기 버튼 클릭
        RetrofitClient.planInstance.deletePlan(
            authorization = "Bearer ${getRefreshToken(this)}",
            planId = planId
        ).enqueue(object : retrofit2.Callback<ResponseDeletePlan> {
            override fun onResponse(
                call: Call<ResponseDeletePlan>,
                response: Response<ResponseDeletePlan>
            ) {
                if (response.isSuccessful) {
                    Log.d(
                        NETWORK, "DetailFixActivity - deletePlan() 실행결과 성공\n" +
                                "result : ${response.body()}"
                    )
                    finish()
                } else {
                    Log.d(NETWORK, "DetailFixActivity - deletePlan() 실행결과 안좋음")
                }
            }

            override fun onFailure(call: Call<ResponseDeletePlan>, t: Throwable) {
                Log.d(
                    NETWORK, "DetailFixActivity - deletePlan() 실행결과 실패\nbecause : $t"
                )
            }
        })
    }

    companion object {
        const val INTENT_TAG = "DetailFixActivity"
    }
}