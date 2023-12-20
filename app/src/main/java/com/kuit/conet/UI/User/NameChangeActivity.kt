package com.kuit.conet.UI.User

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.kuit.conet.*
import com.kuit.conet.Network.*
import com.kuit.conet.databinding.ActivityNameChangeBinding
import retrofit2.Call
import retrofit2.Response
import java.util.regex.Pattern

class NameChangeActivity : AppCompatActivity() {
    lateinit var binding : ActivityNameChangeBinding
    val pattern = Pattern.compile("[!@#$%^&*(),. ?\":{}|<>]") // 특수 문자 체크
    var edit = true

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityNameChangeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.nChangeEt.setText(intent.getStringExtra("name"))
        //binding.nChangeEt.text = getUsername(this)
        binding.nChangeBtnBackIv.setOnClickListener {
            finish()
        }

        // 이름 검사(실시간으로)
        binding.nChangeEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("texting","입력전")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("texting","입력중")
            }

            override fun afterTextChanged(p0: Editable?) {
                Log.d("texting","입력끝")

                val matcher = pattern.matcher(p0.toString())
                //Log.d("texting", "${matcher.find()}")
                //Log.d("text length","${p0!!.length}")
                if(binding.btnTextCancle.visibility == View.GONE && p0!!.length > 0){
                    binding.btnTextCancle.visibility = View.VISIBLE
                }


                if(matcher.find() || p0!!.length >= 20 || p0!!.length <= 0){
                    Log.d("texting","오류")
                    binding.nChangeInfo1Iv.setImageResource(R.drawable.error_red)
                    binding.nChangeInfo1Tv.setTextColor(getColor(R.color.error))
                    binding.btnTextCancle.visibility = View.GONE
                    binding.simbolErrCancle.visibility = View.VISIBLE
                    binding.nChangeBtnCv.setBackgroundResource(R.drawable.button_design_gray)
                    binding.tvUnderlineLl.setBackgroundResource(R.color.error)
                    edit = false
                }
                else if(p0.length > 0){
                    binding.nChangeInfo1Iv.setImageResource(R.drawable.error_purple)
                    binding.nChangeInfo1Tv.setTextColor(getColor(R.color.gray800))
                    binding.btnTextCancle.visibility = View.VISIBLE
                    binding.simbolErrCancle.visibility = View.GONE
                    binding.nChangeBtnCv.setBackgroundResource(R.drawable.button_design_purple)
                    binding.tvUnderlineLl.setBackgroundResource(R.color.gray200)
                    edit = true
                }
                else{
                    binding.nChangeInfo1Iv.setImageResource(R.drawable.error_purple)
                    binding.nChangeInfo1Tv.setTextColor(getColor(R.color.gray800))
                    binding.btnTextCancle.visibility = View.GONE
                    binding.simbolErrCancle.visibility = View.GONE
                    binding.nChangeBtnCv.setBackgroundResource(R.drawable.button_design_gray)
                    binding.tvUnderlineLl.setBackgroundResource(R.color.gray200)
                    edit = false
                }

            }

        })

        binding.btnTextCancle.setOnClickListener {
            binding.nChangeEt.text = null
        }

        binding.nChangeBtnCv.setOnClickListener { // 완료 시 이름 저장


            if(edit){
                saveUsername(this, binding.nChangeEt.text.toString())
                //서버로 정보 보내기
                editUserName(getUsername(this))
                //메인 화면으로 이동

                val intent = Intent(this, InfoActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }



    fun editUserName(name : String){
        val refreshToken = getRefreshToken(this)
        val editName = getRetrofit().create(RetrofitInterface::class.java)
        editName.editName(
            "Bearer $refreshToken",
            name
        ).enqueue(object : retrofit2.Callback<EditUserName>{
            override fun onResponse(call: Call<EditUserName>, response: Response<EditUserName>) {
                if(response.isSuccessful){
                    val resp = response.body()
                    Log.d("SIGNUP/SUCCESS", resp.toString())
                }
            }

            override fun onFailure(call: Call<EditUserName>, t: Throwable) {
                Log.d("SIGNUP/FAILURE", t.message.toString()) // 실패한 이유 메세지 출력
            }

        })
    }
}