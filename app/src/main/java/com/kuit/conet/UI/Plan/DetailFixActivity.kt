package com.kuit.conet.UI.Plan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.kuit.conet.Network.ResponseGetPlanDetail
import com.kuit.conet.Network.ResultGetPlanDetail
import com.kuit.conet.Network.RetrofitClient
import com.kuit.conet.R
import com.kuit.conet.Utils.TAG
import com.kuit.conet.databinding.ActivityDetailFixBinding
import retrofit2.Call
import retrofit2.Response
import kotlin.properties.Delegates

class DetailFixActivity : AppCompatActivity(), View.OnClickListener, EditTrashDialog.OnDialogClickListener {

    private val binding: ActivityDetailFixBinding by lazy { ActivityDetailFixBinding.inflate(layoutInflater) }

    private var planId: Int? = null
    private lateinit var data: ResultGetPlanDetail

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "DetailFixActivity - onCreate() 실행")
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        planId = intent.getIntExtra("PlanId", 0)

        if(planId == 0){
            Log.d(TAG, "DetailPastActiity - onCreat()\n" +
                    "잘못된 planId를 넘겨주었습니다 코드를 잘 살펴보세요!")
            finish()
        }

        binding.menuBtn.setOnClickListener(this)
        binding.backIv.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
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
        Log.d(TAG, "DetailFixActivity - onResume() 실행")
        planId?.let {
            Log.d(TAG, "DetailPastActivity - onResume() planId가 정상적으로 받아와졌어요\n" +
                    "planId : $planId")
            RetrofitClient.instance.getPlanDetail(it).enqueue(object: retrofit2.Callback<ResponseGetPlanDetail>{
                override fun onResponse(
                    call: Call<ResponseGetPlanDetail>,
                    response: Response<ResponseGetPlanDetail>
                ) {
                    if(response.isSuccessful){
                        data = response.body()!!.result
                        Log.d(TAG, "DetailPastActivity - Retrofit getPlanDetail() 실행결과 - 성공\n" +
                                "data : $data")

                        binding.nameTf.setText(response.body()!!.result.planName)
                        binding.dateTf.setText(response.body()!!.result.date)
                        binding.timeTf.setText(response.body()!!.result.time)

                        val participantList = response.body()!!.result.members
                        val participantAdapter = ParticipantAdapter(this@DetailFixActivity, participantList, 0)
                        binding.participantsRv.adapter = participantAdapter
                        binding.participantsRv.layoutManager = GridLayoutManager(this@DetailFixActivity, 2)
                    } else{
                        Log.d(TAG, "DetailPastActivity - Retrofit getPlanDetail() 실행결과 - 안좋음")
                    }
                }

                override fun onFailure(call: Call<ResponseGetPlanDetail>, t: Throwable) {
                    Log.d(TAG, "DetailPastActivity - Retrofit getPlanDetail() 실행결과 - 실패\n" +
                            "t : $t")
                }
            }) }
    }

    override fun onEditButtonClick() {
        val mIntent = Intent(this, DetailEditFixActivity::class.java)
//        TODO 해당 내용 전달해 주기!
        mIntent.putExtra("data", data)
        startActivity(mIntent)
    }

    override fun onTrashButtonClick() {
//        TODO 해당 약속을 삭제 -> Dialog 띄우기 -> 만약 삭제를 한다면 DetailFixActivity에 전달
    }
}