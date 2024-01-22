package com.kuit.conet.UI.Plan.detail

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.kuit.conet.Network.ResponseDeletePlan
import com.kuit.conet.Network.ResponseGetPlanDetail
import com.kuit.conet.Network.ResultGetPlanDetail
import com.kuit.conet.Network.RetrofitClient
import com.kuit.conet.R
import com.kuit.conet.UI.Plan.dialog.EditTrashDialog
import com.kuit.conet.Utils.LIFECYCLE
import com.kuit.conet.Utils.NETWORK
import com.kuit.conet.Utils.TAG
import com.kuit.conet.databinding.ActivityDetailFixBinding
import retrofit2.Call
import retrofit2.Response

class DetailFixActivity
    : AppCompatActivity(), View.OnClickListener, EditTrashDialog.OnDialogClickListener {

    private lateinit var binding: ActivityDetailFixBinding

    private var planId: Int = 0
    private lateinit var data: ResultGetPlanDetail

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(LIFECYCLE, "DetailFixActivity - onCreate() 실행")
        super.onCreate(savedInstanceState)
        binding = ActivityDetailFixBinding.inflate(layoutInflater)
        setContentView(binding.root)

        planId = intent.getIntExtra("PlanId", 0)

        if (planId == 0) {
            Log.d(TAG, "DetailPastActiity - onCreate()\nplanId가 넘어오지 않았습니다.")
            finish()
        }

        binding.ivDetailFixMenu.setOnClickListener(this)
        binding.ivDetailFixBack.setOnClickListener(this)
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
        Log.d(LIFECYCLE, "DetailFixActivity - onResume() 실행")

        RetrofitClient.instance.getPlanDetail(planId)
            .enqueue(object : retrofit2.Callback<ResponseGetPlanDetail> {
                override fun onResponse(
                    call: Call<ResponseGetPlanDetail>,
                    response: Response<ResponseGetPlanDetail>
                ) {
                    if (response.isSuccessful) {
                        data = response.body()!!.result
                        Log.d(
                            NETWORK, "DetailPastActivity - Retrofit getPlanDetail() 실행결과 - 성공\n" +
                                    "data : $data"
                        )

                        binding.tfDetailFixPlanName.setText(data.planName)
                        binding.tfDetailFixPlanDate.setText(data.date)
                        binding.tfDetailFixPlanTime.setText(data.time)

                        val participantList = response.body()!!.result.members
                        binding.rvDetailFixPlanParticipants.adapter =
                            ParticipantAdapter(this@DetailFixActivity, participantList, 0)
                        binding.rvDetailFixPlanParticipants.layoutManager =
                            GridLayoutManager(this@DetailFixActivity, 2)
                    } else {
                        Log.d(NETWORK, "DetailPastActivity - Retrofit getPlanDetail() 실행결과 - 안좋음")
                    }
                }

                override fun onFailure(call: Call<ResponseGetPlanDetail>, t: Throwable) {
                    Log.d(
                        NETWORK, "DetailPastActivity - Retrofit getPlanDetail() 실행결과 - 실패\n" +
                                "t : $t"
                    )
                }
            })
    }

    override fun onEditButtonClick() {
        val mIntent = Intent(this, DetailEditFixActivity::class.java)
        mIntent.putExtra("data", data)
        startActivity(mIntent)
    }

    override fun onTrashButtonClick() {
//        TODO 약속 삭제에 대한 dialog 만들기(DeletePlanDialog을 활용하기)
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            .setMessage("해당 약속을 삭제하시겠습니까?")
            .setTitle("약속 삭제")
            .setPositiveButton("확인") { dialog, which ->
                RetrofitClient.instance.deletePlan(planId)
                    .enqueue(object : retrofit2.Callback<ResponseDeletePlan> {
                        override fun onResponse(
                            call: Call<ResponseDeletePlan>,
                            response: Response<ResponseDeletePlan>
                        ) {
                            if (response.isSuccessful) {
                                Log.d(
                                    NETWORK, "DetailFixActivity - deletePlan() 실행결과 성공\n" +
                                            "result : ${response.body()}"
                                )
                            } else {
                                Log.d(NETWORK, "DetailFixActivity - deletePlan() 실행결과 안좋음")
                            }
                        }

                        override fun onFailure(call: Call<ResponseDeletePlan>, t: Throwable) {
                            Log.d(
                                NETWORK, "DetailFixActivity - deletePlan() 실행결과 실패\n" +
                                        "t : $t"
                            )
                        }

                    })
            }
            .setNegativeButton("취소") { dialog, which ->
                finish()
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}