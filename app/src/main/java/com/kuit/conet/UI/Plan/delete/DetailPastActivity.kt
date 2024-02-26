package com.kuit.conet.UI.Plan.delete

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.kuit.conet.Network.ResponseGetPlanDetail
import com.kuit.conet.Network.ResultGetPlanDetail
import com.kuit.conet.Network.RetrofitClient
import com.kuit.conet.R
import com.kuit.conet.UI.History.RegistHistoryActivity
import com.kuit.conet.UI.Plan.detail.ParticipantAdapter
import com.kuit.conet.UI.Plan.dialog.EditTrashDialog
import com.kuit.conet.Utils.TAG
import com.kuit.conet.databinding.ActivityDetailPastBinding
import retrofit2.Call
import retrofit2.Response

class DetailPastActivity : AppCompatActivity(), View.OnClickListener, EditTrashDialog.OnDialogClickListener {

    private val binding: ActivityDetailPastBinding by lazy { ActivityDetailPastBinding.inflate(layoutInflater) }
    private var planId: Int? = null
    private lateinit var data: ResultGetPlanDetail

    override fun onCreate(savedInstanceState: Bundle?) {
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
//        binding.enrollHistoryBtnIv.setOnClickListener(this)
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
            /*R.id.enroll_history_btn_iv -> {
                val mIntnet = Intent(this, RegistHistoryActivity::class.java)
                mIntnet.putExtra("data", data)
                startActivity(mIntnet)
            }*/
        }
    }

    override fun onResume() {
        super.onResume()
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

//                    TODO 확이필요!

                    binding.nameTf.setText(response.body()!!.result.planName)
//                    binding.nameTf.setTextColor(R.color.texthigh)
                    binding.dateTf.setText(response.body()!!.result.date)
                    binding.timeTf.setText(response.body()!!.result.time)
                    /*if(response.body()!!.result.isRegisteredToHistory){
                        Glide.with(this@DetailPastActivity)
                            .load(response.body()!!.result.historyImageUrl) // 불러올 이미지 url
                            .centerCrop()
                            .error(R.drawable.profile_purple) // 로딩 에러 발생 시 표시할 이미지
                            .into(binding.pictureIv1) // 이미지를 넣을 뷰

                        if(response.body()!!.result.historyDescription != null){
                            binding.contentHintTv.visibility = View.GONE
                            binding.contentTf.setText(response.body()!!.result.historyDescription)
                        } else{
                            binding.contentHintTv.visibility = View.VISIBLE
                        }
                        binding.historyCl.visibility = View.VISIBLE
                        binding.noHistoryCl.visibility = View.GONE
                    } else{
                        binding.historyCl.visibility = View.GONE
                        binding.noHistoryCl.visibility = View.VISIBLE
                    }*/

                    val participantList = response.body()!!.result.members
                    val participantAdapter = ParticipantAdapter(this@DetailPastActivity, participantList, 0)

                    binding.participantsRv.adapter = participantAdapter
                    binding.participantsRv.layoutManager = GridLayoutManager(this@DetailPastActivity, 2)
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

    /*private fun checkHistory(isRegisteredHistory: Boolean){
        if(isRegisteredHistory){
            binding.historyCl.visibility = View.VISIBLE
            binding.noHistoryCl.visibility = View.GONE
        } else{
            binding.historyCl.visibility = View.GONE
            binding.noHistoryCl.visibility = View.VISIBLE
        }
    }*/

    override fun onEditButtonClick() {
        val mIntent = Intent(this, DetailEditPastActivity::class.java)
//        TODO 해당 내용 전달해 주기!
        mIntent.putExtra("data", data)
        startActivity(mIntent)
    }

    override fun onTrashButtonClick() {
//        TODO 해당 약속을 삭제 -> Dialog 띄우기 -> 만약 삭제를 한다면 DetailFixActivity에 전달
    }
}
