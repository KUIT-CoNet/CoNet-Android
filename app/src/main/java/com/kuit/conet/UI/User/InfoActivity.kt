package com.kuit.conet.UI.User

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.kuit.conet.*
import com.kuit.conet.Network.EditUserImage
import com.kuit.conet.Network.RetrofitInterface
import com.kuit.conet.Network.getRetrofit
import com.kuit.conet.Utils.getRefreshToken
import com.kuit.conet.Utils.getUserEmail
import com.kuit.conet.Utils.getUserImg
import com.kuit.conet.Utils.getUsername
import com.kuit.conet.Utils.saveUserImgUrl
import com.kuit.conet.databinding.ActivityInfoBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File

class InfoActivity : AppCompatActivity() {
    lateinit var binding : ActivityInfoBinding
    private val pickImageFromGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageUri = data?.data
            saveUserImgUrl(this, imageUri.toString())
            if (imageUri != null) {
                val file = uriToFile(imageUri)
                if (file != null) {
                    Glide.with(this)
                        .load(file) // 로컬 파일로 변환한 이미지를 로드
                        .placeholder(R.drawable.profile_purple)
                        .error(R.drawable.profile_purple)
                        .fallback(R.drawable.profile_purple)
                        .circleCrop()
                        .into(binding.ivInfoPhoto)
                    sendImage(file)
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityInfoBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val name = getUsername(this)
        val imageUrl = getUserImg(this)
        val email = getUserEmail(this)
        binding.tvInfoUsername.text = name
        Glide.with(this)
            .load(imageUrl) // 불러올 이미지 url
            .placeholder(R.drawable.profile_purple) // 이미지 로딩 시작하기 전 표시할 이미지
            .error(R.drawable.profile_purple) // 로딩 에러 발생 시 표시할 이미지
            .fallback(R.drawable.profile_purple) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
            .circleCrop() // 동그랗게 자르기 동그란 맘속에 피어난 How is the life...
            .into(binding.ivInfoPhoto) // 이미지를 넣을 뷰

        binding.tvInfoAccount.text = email

        binding.ivInfoBackBtn.setOnClickListener {
            finish()
        }

        binding.cvInfoMy.setOnClickListener {
            val intent = Intent(this, NameChangeActivity::class.java)
            intent.putExtra("name",name)
            startActivity(intent)
            finish()
        }

        binding.tvInfoWithdrawal.setOnClickListener {
            val withdrawDialog = WithdrawDialog()

            supportFragmentManager.beginTransaction()
                .replace(R.id.fl_info, withdrawDialog)
                .commit()
        }

        binding.ivInfoPhoto.setOnClickListener {
            openGallery()
        }


    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageFromGallery.launch(galleryIntent)
    }

    fun sendImage(imageFile: File){
        val responseImage = getRetrofit().create(RetrofitInterface::class.java)
        val refreshToken = getRefreshToken(this)

        val authHeader = "Bearer $refreshToken"
        val requestFile = imageFile.asRequestBody("image/png".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

        responseImage.editImage(authHeader, filePart).enqueue(object : retrofit2.Callback<EditUserImage> {
            override fun onResponse(call: Call<EditUserImage>, response: Response<EditUserImage>) {
                if (response.isSuccessful) {
                    val resp = response.body() // 성공했을 경우 response body불러오기
                    Log.d("UserInfo", resp.toString())
                    Log.d("UserInfo!", "success")
                } else {
                    // 실패 처리
                    Log.d("UserInfo", "failed")
                }
            }

            override fun onFailure(call: Call<EditUserImage>, t: Throwable) {
                Log.d("SIGNUP/FAILURE", t.message.toString()) // 실패한 이유 메세지 출력
            }
        })
    }

    private fun uriToFile(uri: Uri): File? {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, filePathColumn, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex(filePathColumn[0])
                val filePath = it.getString(columnIndex)
                return File(filePath)
            }
        }
        return null
    }
}