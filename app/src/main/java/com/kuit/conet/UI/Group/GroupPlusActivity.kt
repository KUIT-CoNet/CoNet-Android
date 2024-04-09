package com.kuit.conet.UI.Group

import android.Manifest
import com.kuit.conet.R
import com.kuit.conet.databinding.ActivityGroupPlusBinding
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.*
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.kuit.conet.Network.RetrofitClient
import com.kuit.conet.Utils.LIFECYCLE
import com.kuit.conet.Utils.permission.APIDetector
import com.kuit.conet.Utils.NETWORK
import com.kuit.conet.Utils.TAG
import com.kuit.conet.Utils.multipart.ContentUriRequestBody
import com.kuit.conet.Utils.getAccessToken
import com.kuit.conet.data.dto.response.team.ResponseCreateGroup
import com.kuit.conet.data.dto.response.team.ResponseUpdateGroup
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Response

class GroupPlusActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityGroupPlusBinding
    lateinit var body: MultipartBody.Part
    private val option: Int by lazy {
        intent.getIntExtra(
            "option",
            0
        )
    }     // 0: 그룹 추가하기 , 1: 그룹 수정하기
    private val groupId: Long by lazy { intent.getLongExtra("groupId", 0) }
    private var groupName: String? = null
    private var groupImage: String? = null
    private lateinit var galleryApiDetector: APIDetector

    private val activityResult: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                Log.d(TAG, "okay")
                val uri = result.data?.data

                if (uri == null) {
                    setImageSection(false)
                    return@registerForActivityResult
                }

                Glide.with(this)
                    .load(uri)
                    .centerCrop()
                    .into(binding.ivGroupPicture)

                setImageSection(true)
                binding.imagePlusBtn.text = "수정"

                //creating a file
                val requestBody = ContentUriRequestBody(this, uri)
                body = requestBody.toFormData("file")
                /*val file = File(getRealFilePath(uri))
                val requestBody: RequestBody = file.asRequestBody("image/png".toMediaTypeOrNull())
//            val requestBody : RequestBody = file.asRequestBody("image/png".toMediaType())
//            val requestBody: RequestBody.create(MediaType.parse("image/png"), file)
                body = MultipartBody.Part.createFormData("file", file.name, requestBody)*/

                validateFinish(groupName, groupImage)
            } else {    // 화면 전환 후 아무것도 고르지 않고 뒤로가기를 눌렀을 때
                setImageSection(true)
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
                )
                    .show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupPlusBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d(LIFECYCLE, "GroupPlusActivity - onCreate() called")
        selectOption(option = option)

        binding.ivClose.setOnClickListener(this)
        binding.imagePlusBtn.setOnClickListener(this)
        binding.tvFinish.setOnClickListener(this)

        binding.tfGroupName.doAfterTextChanged {
            val name = binding.tfGroupName.text ?: ""

            if (name.count() > 20) {
                binding.tilGroupName.error = " "

                binding.tvFinish.isEnabled = false
                binding.tvFinish.setTextColor(getColor(R.color.gray200))

                binding.tvGroupNameHint.visibility = View.GONE
            } else if (name.isEmpty()) {
                binding.tilGroupName.error = null

                binding.tvFinish.isEnabled = false
                binding.tvFinish.setTextColor(getColor(R.color.gray200))

                binding.tvGroupNameHint.visibility = View.VISIBLE
            } else {
                binding.tilGroupName.error = null

                binding.tvFinish.isEnabled = true
                binding.tvFinish.setTextColor(getColor(R.color.purpleMain))

                binding.tvGroupNameHint.visibility = View.GONE

                groupName = name.toString()
                validateFinish(groupName, groupImage)
            }
        }

        binding.tfGroupName.setOnFocusChangeListener { _, hasFocus ->
            if (binding.tfGroupName.error == null) {
                binding.tfGroupName.isSelected = !hasFocus
                binding.tilGroupName.isSelected = !hasFocus
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_close -> finish()

            R.id.tv_finish -> {
                when (option) {
                    1 -> {  // 모임 수정하기

                        val jsonString =
                            "{\"teamId\" : ${groupId}, \"teamName\" : \"${groupName}\"}"
                        val jsonList =
                            jsonString.toRequestBody("application/json".toMediaTypeOrNull())

                        if (!this::body.isInitialized) {

                            val imageUrl =
                                requireNotNull(groupImage) { "GroupPlusActivity's body 초기화 안됨, groupImage's value is null" }

                            body = MultipartBody.Part.createFormData(
                                "file",
                                "file",
                                imageUrl.toRequestBody()
                            )
                        }

                        RetrofitClient.teamInstance.updateGroup(
                            authorization = "Bearer ${getAccessToken(this)}",
                            file = body,
                            request = jsonList
                        ).enqueue(object : retrofit2.Callback<ResponseUpdateGroup> {
                            override fun onResponse(
                                call: Call<ResponseUpdateGroup>,
                                response: Response<ResponseUpdateGroup>
                            ) {
                                if (response.isSuccessful) {
                                    Log.d(
                                        NETWORK,
                                        "GroupPlusActivity - Retrofit updateGroup() 실행결과 - 성공"
                                    )
                                    finish()
                                } else {
                                    Log.d(
                                        NETWORK,
                                        "GroupPlusActivity - Retrofit updateGroup() 실행결과 - 안좋음"
                                    )
                                }
                            }

                            override fun onFailure(
                                call: Call<ResponseUpdateGroup>,
                                t: Throwable
                            ) {
                                Log.d(
                                    NETWORK,
                                    "GroupPlusActivity - Retrofit updateGroup() 실행결과 - 실패\nbecause : $t"
                                )
                            }
                        })
                    }

                    else -> {   // 모임 생성하기
                        val jsonString = "{\"teamName\" : \"${groupName}\"}"
                        val jsonList =
                            jsonString.toRequestBody("application/json".toMediaTypeOrNull())

                        RetrofitClient.teamInstance.createGroup(
                            authorization = "Bearer ${getAccessToken(this)}",
                            file = body,
                            request = jsonList
                        ).enqueue(object : retrofit2.Callback<ResponseCreateGroup> {
                            override fun onResponse(
                                call: Call<ResponseCreateGroup>,
                                response: Response<ResponseCreateGroup>
                            ) {
                                if (response.isSuccessful) {
                                    Log.d(
                                        NETWORK,
                                        "GroupPlusActivity - Retrofit creatTeam() 실행결과 - 성공\n" +
                                                "response: $response"
                                    )
                                    finish()
                                } else {
                                    Log.d(
                                        NETWORK,
                                        "GroupPlusActivity - Retrofit creatTeam() 실행결과 - 안좋음\n" +
                                                "response: $response"
                                    )
                                }
                            }

                            override fun onFailure(
                                call: Call<ResponseCreateGroup>,
                                t: Throwable
                            ) {
                                Log.d(
                                    NETWORK,
                                    "GroupPlusActivity - Retrofit creatTeam() 실행결과 - 실패\nbecause : $t"
                                )
                            }
                        })
                    }
                }
            }

            R.id.image_plus_btn -> {
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
            }
        }
    }

    private fun selectOption(option: Int) {
        when (option) {
            1 -> {  // 모임 수정시
                binding.tvTitle.text = "모임 수정하기"
                binding.imagePlusBtn.text = getString(R.string.edit)
                binding.tvFinish.isEnabled = true
                binding.tvGroupNameHint.visibility = View.INVISIBLE

                intent.apply {
                    groupName = this.getStringExtra("groupName")
                    groupImage = this.getStringExtra("groupImage")
                }

                binding.tfGroupName.setText(groupName)
                Glide.with(this@GroupPlusActivity)
                    .load(groupImage)
                    .centerCrop()
                    .into(binding.ivGroupPicture)

                body = MultipartBody.Part.createFormData("file", "", "".toRequestBody())
            }

            else -> {   // 모임 생성시
                binding.tvTitle.text = "모임 추가하기"
                binding.imagePlusBtn.text = getString(R.string.attach)
                binding.tvFinish.isEnabled = false
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action === MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is TextInputEditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun setImageSection(isDone: Boolean) {
        if (isDone) {
            binding.ivGroupPicture.setBackgroundResource(R.drawable.border_line)
            binding.llGroupPlusLoadingPicture.visibility = View.INVISIBLE
            binding.llGroupPlusInputPicture.visibility = View.VISIBLE
            return
        }

        binding.ivGroupPicture.setBackgroundResource(R.drawable.border_dotted_line)
        binding.llGroupPlusLoadingPicture.visibility = View.VISIBLE
        binding.llGroupPlusInputPicture.visibility = View.INVISIBLE
    }

    private fun executeGallery() {
        val state = Environment.getExternalStorageState()

        // 갤러리를 열어서 파일을 선택할 수 있도록 합니다.
        if (TextUtils.equals(state, Environment.MEDIA_MOUNTED)) {
            val intent = Intent(Intent.ACTION_PICK)
//                        intent.type = "image/*"
            intent.type = "image/png"
            activityResult.launch(intent)
        }
    }

    private fun checkGalleryPermission(permission: String) {
        when {
            ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                setImageSection(false)
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

    private fun validateFinish(groupName: String?, groupImg: String?) {
        binding.tvFinish.isEnabled = !(groupName.isNullOrBlank() || groupImg.isNullOrBlank())
    }
}