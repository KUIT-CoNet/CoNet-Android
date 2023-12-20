package com.kuit.conet.UI.Group

import android.content.ContentUris
import com.kuit.conet.R
import com.kuit.conet.databinding.ActivityGroupPlusBinding
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
import android.os.*
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
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
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.kuit.conet.Network.ResponseCreateGroup
import com.kuit.conet.Network.ResponseGetGroupDetail
import com.kuit.conet.Network.ResponseUpdateGroup
import com.kuit.conet.Network.RetrofitClient
import com.kuit.conet.Utils.TAG
import com.kuit.conet.getAccessToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class GroupPlusActivity: AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityGroupPlusBinding
    lateinit var body: MultipartBody.Part
    private lateinit var imageFile: File

    private val option: Int by lazy { intent.getIntExtra("option", 0) }
    private val groupId : Int by lazy{intent.getIntExtra("groupId",0) }
    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == RESULT_OK){
            val uri = result.data?.data ?: return@registerForActivityResult

            Glide.with(this)
                .load(uri)
                .centerCrop()
                .into(binding.groupPictureIv)

            binding.groupPictureIv.setBackgroundResource(R.drawable.border_line)
            binding.loadingPictureLl.visibility = View.INVISIBLE
            binding.inputPictureLl.visibility = View.VISIBLE
            binding.imagePlusBtn.text = "수정"

            imageFile = File(getRealFilePath(uri))
            //creating a file
            val file = File(getRealFilePath(uri))
            val requestBody : RequestBody = file.asRequestBody("image/png".toMediaTypeOrNull())
//            val requestBody : RequestBody = file.asRequestBody("image/png".toMediaType())
//            val requestBody: RequestBody.create(MediaType.parse("image/png"), file)
            body = MultipartBody.Part.createFormData("file", file.name, requestBody)

            Log.d(TAG, "GroupPlusActivity - activityResult 결과 출력\n" +
                    "uri : ${uri}\n" +
                    "getReadFilePath(uri) : ${getRealFilePath(uri)}\n" +
                    "file : ${file.name}\n" +
                    "body : $body")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupPlusBinding.inflate(layoutInflater)
        setContentView(binding.root)
        selectOption()

//        clickListener 설정
        binding.closeIv.setOnClickListener(this)
        binding.imagePlusBtn.setOnClickListener(this)
        binding.finishTv.setOnClickListener(this)

        binding.groupNameTf.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if(binding.groupNameTf.text!!.isNotEmpty()){
                    binding.groupNameHintTv.visibility = View.GONE
                } else{
                    binding.groupNameHintTv.visibility = View.VISIBLE
                }

                if(binding.groupNameTf.text!!.count() > 20){
                    binding.groupNameTil.error = " "
                    binding.finishTv.isEnabled = false
                    binding.finishTv.setTextColor(getColor(R.color.gray200)) //R.color.select_enabled 이걸로 했는데 제대로 적용이 안됨
                } else if(binding.groupNameTf.text!!.isEmpty()){
                    binding.finishTv.isEnabled = false
                    binding.finishTv.setTextColor(getColor(R.color.gray200))
                } else{
                    binding.groupNameTil.error = null
                    binding.finishTv.isEnabled = true
                    binding.finishTv.setTextColor(getColor(R.color.purpleMain))
                }
            }
        })

        binding.groupNameTf.setOnFocusChangeListener { _, hasFocus ->
            if(binding.groupNameTf.error == null){
                binding.groupNameTv.isSelected = !hasFocus
                binding.groupNameTil.isSelected = !hasFocus
                if(binding.groupNameTf.text!!.isEmpty()){
                    binding.groupNameHintTv.visibility = View.VISIBLE
                } else{
                    binding.groupNameHintTv.visibility = View.GONE
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.close_iv -> finish()
            R.id.finish_tv ->{
                if(binding.groupNameTf.text != null && binding.groupNameTil.error == null /**/){
//                    TODO : 이름과 사진에 대한 정보를 서버에 보내기, 사진을 파일화 하기
                    when(option){
                        1 -> {
                            val jsonString = "{\"teamId\" : ${groupId}, \"teamName\" : \"${binding.groupNameTf.text}\"}"
                            val jsonList = jsonString.toRequestBody("application/json".toMediaTypeOrNull())

                            if(::body.isInitialized == false){
                                finish()
                            }
                            else{
                                RetrofitClient.instance.updateGroup("Bearer ${getAccessToken(this)}",body, jsonList).enqueue(object: retrofit2.Callback<ResponseUpdateGroup> {
                                    override fun onResponse(
                                        call: Call<ResponseUpdateGroup>,
                                        response: Response<ResponseUpdateGroup>
                                    ) {
                                        if(response.isSuccessful){
                                            Log.d(TAG, "GroupPlusActivity - Retrofit updateGroup() 실행결과 - 성공\n" +
                                                    "그룹 id : ${response.body()?.result?.groupName}\n" +
                                                    "초대코드 : ${response.body()?.result?.imageUrl}")
                                            finish()
                                        } else{
                                            Log.d(TAG, "GroupPlusActivity - Retrofit updateGroup() 실행결과 - 안좋음")
                                        }
                                    }

                                    override fun onFailure(
                                        call: Call<ResponseUpdateGroup>,
                                        t: Throwable
                                    ) {
                                        Log.d(TAG, "GroupPlusActivity - Retrofit updateGroup() 실행결과 - 실패")
                                        Log.d(TAG, "t : $t")
                                    }

                                })
                            }

                        }
                        else -> {
                            val jsonString = "{\"teamName\" : \"${binding.groupNameTf.text}\"}"
                            val jsonList = jsonString.toRequestBody("application/json".toMediaTypeOrNull())

                            RetrofitClient.instance.createGroup("Bearer ${getAccessToken(this)}",body, jsonList).enqueue(object: retrofit2.Callback<ResponseCreateGroup>{
                                override fun onResponse(
                                    call: Call<ResponseCreateGroup>,
                                    response: Response<ResponseCreateGroup>) {
                                    if(response.isSuccessful){
                                        Log.d(TAG, "GroupPlusActivity - Retrofit creatTeam() 실행결과 - 성공\n" +
                                                "그룹 id : ${response.body()?.result?.groupId}\n" +
                                                "초대코드 : ${response.body()?.result?.enrollCode}")
                                        finish()
                                    } else {
                                        Log.d(TAG, "GroupPlusActivity - Retrofit creatTeam() 실행결과 - 성공\n" +
                                                "response: ${response}")
                                    }
                                }
                                override fun onFailure(call: Call<ResponseCreateGroup>, t: Throwable) {
                                    Log.d(TAG, "GroupPlusActivity - Retrofit creatTeam() 실행결과 - 실패\n${t}")
                                }
                            })
                        }
                    }
                } else Toast.makeText(this, "조건이 충족되지 않았습니다!", Toast.LENGTH_SHORT).show()
            }
            R.id.image_plus_btn -> {
                binding.groupPictureIv.setBackgroundResource(R.drawable.border_dotted_line)
                binding.loadingPictureLl.visibility = View.VISIBLE
                binding.inputPictureLl.visibility = View.INVISIBLE

                // 현재 기기에 설정된 읽기/쓰기 권한을 가져오기 위한 변수
                val readPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                val writePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE )
                if (writePermission == PackageManager.PERMISSION_DENIED || readPermission == PackageManager.PERMISSION_DENIED) {
//                    읽기 권한과 쓰기 권한에 대해서 설정이 되어있지 않다면 읽기, 쓰기 권한을 요청합니다.
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        1)

                    binding.groupPictureIv.setBackgroundResource(R.drawable.border_line)
                    binding.loadingPictureLl.visibility = View.INVISIBLE
                    binding.inputPictureLl.visibility = View.VISIBLE
                    binding.imagePlusBtn.text = "첨부"

                } else {// 위 경우가 아니라면 권한에 대해서 설정이 되어 있으므로
                    val state = Environment.getExternalStorageState()

                    // 갤러리를 열어서 파일을 선택할 수 있도록 합니다.
                    if (TextUtils.equals(state, Environment.MEDIA_MOUNTED)) {
                        val intent = Intent(Intent.ACTION_PICK)
//                        intent.type = "image/*"
                        intent.type = "image/png"
                        activityResult.launch(intent)
                    }
                }
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
                    val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun selectOption(){
        when(option){
            1 -> {//모임이 생성된 상태에서 모임 정보 수정시
                binding.finishTv.setTextColor(getColor(R.color.purpleMain))
                binding.titleTv.text = "모임 수정하기"
//                TODO 이전화면에 있는 데이터 가져오기
                binding.groupNameTf.setText(intent.getStringExtra("groupName"))
                binding.groupNameHintTv.text = ""
                val image = intent.getStringExtra("groupImage")
                val imageUri = Uri.parse(image)
                Glide.with(this)
                    .load(imageUri)
                    .centerCrop()
                    .into(binding.groupPictureIv)
                binding.imagePlusBtn.text = "수정"
                binding.finishTv.isEnabled = true
            }
            else -> {//모임을 생성하는 단계
                binding.titleTv.text = "모임 추가하기"
            }
        }
    }

    private fun changeUrltoFile(groupImgUrl: String) {
        val url = URL(groupImgUrl)
        val connection = url.openConnection()
        connection.doInput = true
        connection.connect()

        val input = connection.getInputStream()
        val tempFile = File.createTempFile("temp_image", ".png", this.cacheDir)
        val output = FileOutputStream(tempFile)
        val buffer = ByteArray(4096)
        var bytesRead: Int

        while (input.read(buffer).also { bytesRead = it } != -1) {
            output.write(buffer, 0, bytesRead)
        }

        output.flush()
        output.close()
        input.close()

        val requestBody : RequestBody = tempFile.asRequestBody("image/png".toMediaType())
        body = MultipartBody.Part.createFormData("file", tempFile.name, requestBody)
    }

    //    uri를 가지고 해당 파일의 진짜 위치를 보낸다.
    private fun getRealFilePath(uri: Uri): String {
        val buildName = Build.MANUFACTURER
        if(buildName.equals("Xiaomi")) {
            return uri.path.toString()
        }

        var columnIndex = 0
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, proj, null, null, null)

        if(cursor!!.moveToFirst()) {
            columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        }

        return cursor.getString(columnIndex)
    }


}