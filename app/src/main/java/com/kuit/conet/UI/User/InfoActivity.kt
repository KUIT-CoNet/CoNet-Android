package com.kuit.conet.UI.User

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.kuit.conet.*
import com.kuit.conet.Network.RetrofitClient
import com.kuit.conet.Utils.LIFECYCLE
import com.kuit.conet.Utils.NETWORK
import com.kuit.conet.Utils.TAG
import com.kuit.conet.Utils.getRefreshToken
import com.kuit.conet.Utils.getUsername
import com.kuit.conet.Utils.intent.intentSerializable
import com.kuit.conet.Utils.multipart.ContentUriRequestBody
import com.kuit.conet.Utils.permission.APIDetector
import com.kuit.conet.data.dto.response.member.ResponseEditUserImg
import com.kuit.conet.databinding.ActivityInfoBinding
import com.kuit.conet.domain.entity.user.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class InfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInfoBinding
    private lateinit var userInfo: User

    private lateinit var galleryApiDetector: APIDetector

    private val galleryActivityResult: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                Log.d(TAG, "okay")

                val uri = result.data?.data ?: return@registerForActivityResult

                Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.profile_purple)
                    .error(R.drawable.profile_purple)
                    .fallback(R.drawable.profile_purple)
                    .circleCrop()
                    .into(binding.ivInfoPhoto)

                //creating a file
                val requestBody = ContentUriRequestBody(this, uri)

                RetrofitClient.memberInstance.editUserImg(
                    authorization = "Bearer ${getRefreshToken(this)}",
                    image = requestBody.toFormData("file")
                ).enqueue(object : Callback<ResponseEditUserImg> {
                    override fun onResponse(
                        call: Call<ResponseEditUserImg>,
                        response: Response<ResponseEditUserImg>
                    ) {
                        if (response.isSuccessful) {
                            Log.d(NETWORK, "InfoActivity - editUserImg 호출 결과 - 성공")
                        } else {
                            Log.d(NETWORK, "InfoActivity - editUserImg 호출 결과 - 안좋음")
                        }
                    }

                    override fun onFailure(call: Call<ResponseEditUserImg>, t: Throwable) {
                        Log.d(
                            NETWORK,
                            "InfoActivity - editUserImg 호출 결과 - 실패\nbecause : ${t.message}"
                        )
                    }
                })

            } else {
                Toast.makeText(this, "예기치 못한 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    private val galleryPermissionResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                executeGallery()
            } else {
                Log.d(TAG, "GroupPlusActivity - () called\ndeny")
                Toast.makeText(
                    this,
                    "갤러리 사용이 거부 되었습니다. 설정에 들어가서 사진 사용을 허가해 주세요",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    private var nameChangeActivityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data?.getStringExtra(NameChangeActivity.INTENT_TAG_NAME) ?: getUsername(this)
                binding.tvInfoUsername.text = data
            }
        }

    /*private var pickImageFromGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                var data: Intent? = result.data
                var imageUri = data?.data
                saveUserImgUrl(this, imageUri.toString())
                if (imageUri != null) {
                    var file = uriToFile(imageUri)
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
                callUserInfo()
            }
        }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityInfoBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        Log.d(LIFECYCLE, "InfoActivity: onCreate called")

        userInfo = requireNotNull(
            intent.intentSerializable(
                UserFragment.INTENT_TAG_USER,
                User::class.java
            )
        ) { "InfoActivity - intent 값 받기 실패" }

        binding.ivInfoBackBtn.setOnClickListener {
            finish()
        }

        binding.cvInfoMy.setOnClickListener {
            val intent = Intent(this, NameChangeActivity::class.java)
            intent.putExtra(INTENT_TAG_NAME, userInfo.name)
//            startActivity(intent)
            nameChangeActivityResultLauncher.launch(intent)
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

    override fun onStart() {
        super.onStart()
        Log.d(LIFECYCLE, "InfoActivity: onStart called")

        binding.tvInfoUsername.text = userInfo.name
        Glide.with(this)
            .load(userInfo.imgUrl)
            .placeholder(R.drawable.profile_purple)
            .error(R.drawable.profile_purple)
            .fallback(R.drawable.profile_purple)
            .circleCrop()
            .into(binding.ivInfoPhoto)
    }

    private fun openGallery() {
        galleryApiDetector = APIDetector(
            this,
            highCode = {
                checkGalleryPermission(Manifest.permission.READ_MEDIA_IMAGES)
            },
            lowCode = {
                checkGalleryPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        )
        galleryApiDetector.setThreadApi(Build.VERSION_CODES.TIRAMISU)
        galleryApiDetector.executeCode()

        /*var galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageFromGallery.launch(galleryIntent)*/
    }

    /*private fun sendImage(imageFile: File) {
        var responseImage = getRetrofit().create(RetrofitInterface::class.java)
        var refreshToken = getRefreshToken(this)

        var requestFile = imageFile.asRequestBody("image/png".toMediaTypeOrNull())
        var filePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

        responseImage.editImage(
            "Bearer $refreshToken",
            filePart
        ).enqueue(object : Callback<EditUserImage> {
            override fun onResponse(
                call: Call<EditUserImage>,
                response: Response<EditUserImage>
            ) {
                if (response.isSuccessful) {
                    saveUserImgUrl(this@InfoActivity, imageFile.name)
                    Log.d(NETWORK, "InfoActivity - editImage 호출 결과 - 성공")
                } else {
                    Log.d(NETWORK, "InfoActivity - editImage 호출 결과 - 안좋음")
                }
            }

            override fun onFailure(call: Call<EditUserImage>, t: Throwable) {
                Log.d(NETWORK, "InfoActivity - editImage 호출 결과 - 실패\nbecause : ${t.message}")
            }
        })
    }*/

    /*private fun uriToFile(uri: Uri): File? {
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
    }*/

    /*private fun callUserInfo() {
        val responseUser = getRetrofit().create(RetrofitInterface::class.java)
        val refreshToken = getRefreshToken(this)

        responseUser.showuser(
            "Bearer $refreshToken"
        ).enqueue(object : Callback<ShowUser> {
            override fun onResponse(call: Call<ShowUser>, response: Response<ShowUser>) {
                if (response.isSuccessful) {
                    Log.d(NETWORK, "InfoActivity - callUserInfo 호출 결과 - 성공")
                    var resp = response.body()// 성공했을 경우 response body불러오기
                    var userData = resp!!.result
                    //continuation.resume(userData)

                    binding.tvInfoUsername.text = userData.name
                    binding.tvInfoAccount.text = userData.email
                    Glide.with(this@InfoActivity)
                        .load(userData.memberImgUrl) // 불러올 이미지 url
                        .placeholder(R.drawable.profile_purple) // 이미지 로딩 시작하기 전 표시할 이미지
                        .error(R.drawable.profile_purple) // 로딩 에러 발생 시 표시할 이미지
                        .fallback(R.drawable.profile_purple) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
                        .circleCrop() // 동그랗게 자르기
                        .into(binding.ivInfoPhoto) // 이미지를 넣을 뷰
                } else {
                    //continuation.resumeWithException(Exception("Response not successful"))
                    Log.d(NETWORK, "InfoActivity - callUserInfo 호출 결과 - 안좋음")
                }
            }

            override fun onFailure(call: Call<ShowUser>, t: Throwable) {
                Log.d(NETWORK, "InfoActivity - callUserInfo 호출 결과 - 실패\nbecause : ${t.message}")
            }

        })
    }*/

    private fun checkGalleryPermission(permission: String) {
        when {
            ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                executeGallery()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                permission
            ) -> {
//                showInContextUI(...)
                Toast.makeText(this, "showInContextUI", Toast.LENGTH_SHORT).show()
                galleryPermissionResult.launch(permission)
            }

            else -> {
                galleryPermissionResult.launch(permission)
            }
        }

    }

    private fun executeGallery() {
        val state = Environment.getExternalStorageState()

        // 갤러리를 열어서 파일을 선택할 수 있도록 합니다.
        if (TextUtils.equals(state, Environment.MEDIA_MOUNTED)) {
            val intent = Intent(Intent.ACTION_PICK)
//                        intent.type = "image/*"
            intent.type = "image/*"
            galleryActivityResult.launch(intent)
        }
    }

    companion object {
        const val INTENT_TAG_NAME = "name"
    }
}