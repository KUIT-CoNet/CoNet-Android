/*
package com.kuit.conet.UI.Plan.delete

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.kuit.conet.Network.Members
import com.kuit.conet.Network.RetrofitClient
import com.kuit.conet.R
import com.kuit.conet.UI.Plan.detail.ParticipantAdapter
import com.kuit.conet.UI.Plan.dialog.EditTrashDialog
import com.kuit.conet.Utils.NETWORK
import com.kuit.conet.Utils.TAG
import com.kuit.conet.databinding.ActivityDetailPastBinding
import com.kuit.conet.Utils.getRefreshToken
import com.kuit.conet.data.dto.response.plan.ResponseGetPlanDetail
import com.kuit.conet.domain.entity.plan.PlanDetail
import retrofit2.Call
import retrofit2.Response

class DetailPastActivity
    : AppCompatActivity(), View.OnClickListener, EditTrashDialog.OnDialogClickListener {

    private lateinit var binding: ActivityDetailPastBinding
    private val planId: Int by lazy { intent.getIntExtra("PlanId", 0) }
    private lateinit var data: PlanDetail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (planId == 0) {
            Log.d(
                TAG, "DetailPastActiity - onCreat()\n" +
                        "잘못된 planId를 넘겨주었습니다 코드를 잘 살펴보세요!"
            )
            finish()
        }

        binding.menuBtn.setOnClickListener(this)
        binding.backIv.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.menu_btn -> {
                val bottomSheet = EditTrashDialog()
                bottomSheet.setOnDialogClickListener(this)
                bottomSheet.show(supportFragmentManager, bottomSheet.tag)
            }

            R.id.back_iv -> {
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        RetrofitClient.planInstance.getPlanDetail(
            authorization = "Bearer ${getRefreshToken(this)}",
            planId = planId.toLong(),
        ).enqueue(object : retrofit2.Callback<ResponseGetPlanDetail> {
            override fun onResponse(
                call: Call<ResponseGetPlanDetail>,
                response: Response<ResponseGetPlanDetail>
            ) {
                if (response.isSuccessful) {
                    Log.d(NETWORK, "DetailPastActivity - getPlanDetail() 실행결과 - 성공")
                    val resp =
                        requireNotNull(response.body()) { "DetailPastActivity - getPlanDetail() 실행결과 불러오기 실패" }
                    data = resp.result.asPlanDetail()

                    binding.nameTf.setText(data.planName)
                    binding.dateTf.setText(data.date)
                    binding.timeTf.setText(data.time)

                    val participantAdapter = ParticipantAdapter(
                        this@DetailPastActivity,
                        data.members.map { it.asMembers() } as ArrayList<Members>,
                        0)
                    binding.participantsRv.adapter = participantAdapter
                } else {
                    Log.d(NETWORK, "DetailPastActivity - Retrofit getPlanDetail() 실행결과 - 안좋음")
                }
            }

            override fun onFailure(call: Call<ResponseGetPlanDetail>, t: Throwable) {
                Log.d(
                    NETWORK, "DetailPastActivity - Retrofit getPlanDetail() 실행결과 - 실패\nbecause : $t"
                )
            }
        })

        
    }

    override fun onEditButtonClick() {
        val intent = Intent(this, DetailEditPastActivity::class.java)
        intent.putExtra(INTENT_KEY, data)
        startActivity(intent)
    }

    override fun onTrashButtonClick() {
//        TODO 해당 약속을 삭제 -> Dialog 띄우기 -> 만약 삭제를 한다면 DetailFixActivity에 전달
    }

    companion object {
        const val INTENT_KEY = "PlanDetail"
    }
}
*/
