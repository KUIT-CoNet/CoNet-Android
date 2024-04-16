package com.kuit.conet.UI.User

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.kuit.conet.*
import com.kuit.conet.Network.*
import com.kuit.conet.Utils.LIFECYCLE
import com.kuit.conet.Utils.NETWORK
import com.kuit.conet.Utils.getRefreshToken
import com.kuit.conet.Utils.saveUsername
import com.kuit.conet.data.dto.request.member.RequestEditUserName
import com.kuit.conet.data.dto.response.member.ResponseEditUserName
import com.kuit.conet.databinding.ActivityNameChangeBinding
import retrofit2.Call
import retrofit2.Response
import java.util.regex.Pattern

class NameChangeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNameChangeBinding
    private val pattern = Pattern.compile("[!@#$%^&*(),. ?\":{}|<>]") // 특수 문자 체크
    private var edit = true

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityNameChangeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        Log.d(LIFECYCLE, "NameChangeActivity: onCreate called")

        binding.etName.setText(intent.getStringExtra(InfoActivity.INTENT_TAG_NAME))

        binding.ivNameBackBtn.setOnClickListener {
            finish()
        }

        binding.etName.doAfterTextChanged {
            val matcher = pattern.matcher(it.toString())
            //Log.d("texting", "${matcher.find()}")
            //Log.d("text length","${p0!!.length}")
            if (binding.ivNameTextCancel.visibility == View.GONE && it!!.length > 0) {
                binding.ivNameTextCancel.visibility = View.VISIBLE
            }

            if (matcher.find() || it!!.length >= 20 || it!!.length <= 0) {
                Log.d("texting", "오류")
                binding.ivNameInfo1.setImageResource(R.drawable.ic_error_red)
                binding.tvNameInfo1.setTextColor(getColor(R.color.error))
                binding.ivNameTextCancel.visibility = View.GONE
                binding.ivNameErrorEmpty.visibility = View.VISIBLE
                binding.cvNameDoneBtn.setBackgroundResource(R.drawable.button_design_gray)
                binding.llNameUnderline.setBackgroundResource(R.color.error)
                edit = false
            } else if (it.length > 0) {
                binding.ivNameInfo1.setImageResource(R.drawable.ic_error_purple)
                binding.tvNameInfo1.setTextColor(getColor(R.color.gray800))
                binding.ivNameTextCancel.visibility = View.VISIBLE
                binding.ivNameErrorEmpty.visibility = View.GONE
                binding.cvNameDoneBtn.setBackgroundResource(R.drawable.button_design_purple)
                binding.llNameUnderline.setBackgroundResource(R.color.gray200)
                edit = true
            } else {
                binding.ivNameInfo1.setImageResource(R.drawable.ic_error_purple)
                binding.tvNameInfo1.setTextColor(getColor(R.color.gray800))
                binding.ivNameTextCancel.visibility = View.GONE
                binding.ivNameErrorEmpty.visibility = View.GONE
                binding.cvNameDoneBtn.setBackgroundResource(R.drawable.button_design_gray)
                binding.llNameUnderline.setBackgroundResource(R.color.gray200)
                edit = false
            }
        }

        binding.ivNameTextCancel.setOnClickListener {
            binding.etName.text = null
        }

        binding.cvNameDoneBtn.setOnClickListener { // 완료 시 이름 저장
            if (edit) {
                saveUsername(this, binding.etName.text.toString())
                editUserName(binding.etName.text.toString())
            }
        }
    }

    private fun editUserName(name: String) {
        Log.d(NETWORK, "editUserName: 실행")

        RetrofitClient.memberInstance.editUserName(
            authorization = "Bearer ${getRefreshToken(this)}",
            request = RequestEditUserName(
                name = name,
            ),
        ).enqueue(object : retrofit2.Callback<ResponseEditUserName> {
            override fun onResponse(
                call: Call<ResponseEditUserName>,
                response: Response<ResponseEditUserName>
            ) {
                if (response.isSuccessful) {
                    Log.d(NETWORK, "NameChangeActivity - editUsername 실행결과 - 성공")

                    val returnIntent = Intent()
                    returnIntent.putExtra(INTENT_TAG_NAME, name)
                    setResult(Activity.RESULT_OK, returnIntent)

                    finish()

                } else {
                    Log.d(NETWORK, "NameChangeActivity - editUsername 실행결과 - 안좋음")
                }
            }

            override fun onFailure(call: Call<ResponseEditUserName>, t: Throwable) {
                Log.d(
                    NETWORK,
                    "NameChangeActivity - editUsername 실행결과 - 실패\nbecause : ${t.message}"
                )
            }

        })
    }

    companion object {
        const val INTENT_TAG_NAME = "name"
    }
}