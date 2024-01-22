package com.kuit.conet.UI.Plan.delete

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.kuit.conet.Data.Group
import com.kuit.conet.Network.*
import com.kuit.conet.R
import com.kuit.conet.UI.Plan.detail.ParticipantAdapter
import com.kuit.conet.UI.Plan.dialog.DateDialog
import com.kuit.conet.UI.Plan.dialog.TimeDialog
import com.kuit.conet.Utils.TAG
import com.kuit.conet.databinding.ActivityDetailEditPastBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class DetailEditPastActivity : AppCompatActivity(), View.OnClickListener, TimeDialog.BottomSheetListener, DateDialog.OnButtonClickListener {

    private val binding: ActivityDetailEditPastBinding by lazy { ActivityDetailEditPastBinding.inflate(layoutInflater) }

    private lateinit var imageFile: File
    var body: MultipartBody.Part? = null
    lateinit var data: ResultGetPlanDetail

    lateinit var participantAdapter: ParticipantAdapter

    lateinit var dateData: ArrayList<String>

    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == RESULT_OK){
            val uri = result.data?.data ?: return@registerForActivityResult

            Glide.with(this)
                .load(uri)
                .into(binding.pictureIv)


            imageFile = File(getRealFilePath(uri))
            //creating a file
            val file = File(getRealFilePath(uri))

            Log.d(
                TAG, "GroupPlusActivity - activityResult 결과 출력\n" +
                    "uri : ${uri}\n" +
                    "getReadFilePath(uri) : ${getRealFilePath(uri)}\n" +
                    "file : ${file.name}")

            val requestBody : RequestBody = file.asRequestBody("image/png".toMediaTypeOrNull())
            body = MultipartBody.Part.createFormData("file",file.name,requestBody)

            binding.pictureIv.visibility = View.VISIBLE
            binding.pictureDeleteIv.visibility = View.VISIBLE
            binding.countPictureTv.text = "1/1"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        data = intent.getParcelableExtra<ResultGetPlanDetail>("data")!!
        Log.d(TAG, "DetailEditPastActivity - onCreat() intent로 데이터 받아오기 성공!\n" +
                "data : $data")

        val date = data.date.split(". ")
        dateData = arrayListOf(date[0], date[1], date[2])

        binding.nameTf.setText(data.planName)
        binding.dateTf.setText(data.date)
        binding.timeTf.setText(data.time)

        setHistory()


        binding.backIv.setOnClickListener(this)
        binding.finishTv.setOnClickListener(this)
        binding.picturePlusView.setOnClickListener(this)
        binding.pictureDeleteIv.setOnClickListener(this)
        binding.dateTil.setOnClickListener(this)
        binding.dateTf.setOnClickListener(this)
        binding.timeTil.setOnClickListener(this)
        binding.timeTf.setOnClickListener(this)

        checkEnable()

        data.members.add(Members(0, "추가하기", null))
        participantAdapter = ParticipantAdapter(this, data.members, 1)
        participantAdapter.supportFragmentManager = supportFragmentManager
        participantAdapter.groupId = Group.groupId
        binding.participantsRv.adapter = participantAdapter
        binding.participantsRv.layoutManager = GridLayoutManager(this, 2)

        binding.nameTf.addTextChangedListener {
            checkEnable()
        }

        binding.contentTf.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                when(binding.contentTf.text!!.count()){
                    0 -> binding.contentHintTv.visibility = View.VISIBLE
                    else -> binding.contentHintTv.visibility = View.GONE
                }
            }
        })
    }

    private fun setHistory() {
        if(data.historyImageUrl != null){
            binding.countPictureTv.text = "1/1"
            binding.pictureDeleteIv.visibility = View.VISIBLE
            binding.pictureIv.visibility = View.VISIBLE
            Glide.with(this)
                .load(data.historyImageUrl) // 불러올 이미지 url
                .centerCrop()
                .into(binding.pictureIv) // 이미지를 넣을 뷰
        } else {
            binding.countPictureTv.text = "사진 추가"
            binding.pictureDeleteIv.visibility = View.GONE
            binding.pictureIv.visibility = View.GONE
        }

        if(data.historyDescription != null){
            binding.contentTf.setText(data.historyDescription)
            binding.contentHintTv.visibility = View.GONE
        } else {
            binding.contentHintTv.visibility = View.VISIBLE
        }
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.back_iv -> {
                finish()
            }
            R.id.finish_tv -> {
                if(binding.nameTf.text!!.isEmpty()){
                    Log.d(TAG, "DetailEditPastActivity - 완료버튼 클릭됨\n" +
                            "nameTf 빈값이라 안됨")
                } else if(binding.dateTf.text!!.isEmpty()){
                    Log.d(TAG, "DetailEditPastActivity - 완료버튼 클릭됨\n" +
                            "dateTf 빈값이라 안됨")
                } else if(binding.timeTf.text!!.isEmpty()) {
                    Log.d(
                        TAG, "DetailEditFPastActivity - 완료버튼 클릭됨\n" +
                                "timeTf 빈값이라 안됨"
                    )
                }
//                여기에 picture와 content도 검사해야 하지 않나?
                else{
//                    TODO 서버에 해당 내용 전송하기
                    val members : ArrayList<Int> = arrayListOf()
                    for (i in 0 until participantAdapter.membersList.count()) {
                        if(participantAdapter.membersList[i].name == "추가하기") continue
                        members.add(participantAdapter.membersList[i].id)
                    }
                    members.sort()


                    val jsonString = "{\"planId\" : ${data.planId}," +
                            " \"planName\" : \"${binding.nameTf.text}\"," +
                            " \"date\" : \"${dateData[0]}-${dateData[1]}-${dateData[2]}\"," +
                            " \"time\" : \"${binding.timeTf.text}\"," +
                            " \"members\" : ${members}," +
                            " \"isRegisteredToHistory\" : ${data.isRegisteredToHistory}," +
                            " \"historyDescription\" : \"${binding.contentTf.text}\"}"
                    val jsonList = jsonString.toRequestBody("application/json".toMediaTypeOrNull())

                    Log.d(TAG, "jsonList : $jsonString")

                    if(body == null){
                        /*RetrofitClient.instance.updatePlanDetail(jsonList, null).enqueue(object: retrofit2.Callback<ResponseUpdatePlanDetail>{
                        override fun onResponse(
                            call: Call<ResponseUpdatePlanDetail>,
                            response: Response<ResponseUpdatePlanDetail>
                        ) {
                            if(response.isSuccessful){
                                Log.d(TAG, "DetailEditPastActivity - Retrofit updatePlanDetail() 실행 결과 - 성공")
                                finish()
                            }else {
                                Log.d(TAG, "DetailEditPastActivity - Retrofit updatePlanDetail() 실행 결과 - 안좋음\n" +
                                        "response : $response")
                            }
                        }
                        override fun onFailure(call: Call<ResponseUpdatePlanDetail>, t: Throwable) {
                            Log.d(TAG, "DetailEditPastActivity - Retrofit updatePlanDetail() 실행 결과 - 실패\n" +
                                    "t : $t")
                        }

                    })*/
                    } else{
                        /*RetrofitClient.instance.updatePlanDetail(jsonList, body).enqueue(object: retrofit2.Callback<ResponseUpdatePlanDetail>{
                        override fun onResponse(
                            call: Call<ResponseUpdatePlanDetail>,
                            response: Response<ResponseUpdatePlanDetail>
                        ) {
                            if(response.isSuccessful){
                                Log.d(TAG, "DetailEditPastActivity - Retrofit updatePlanDetail() 실행 결과 - 성공")
                                finish()
                            }else {
                                Log.d(TAG, "DetailEditPastActivity - Retrofit updatePlanDetail() 실행 결과 - 안좋음\n" +
                                        "response : $response")
                            }
                        }
                        override fun onFailure(call: Call<ResponseUpdatePlanDetail>, t: Throwable) {
                            Log.d(TAG, "DetailEditPastActivity - Retrofit updatePlanDetail() 실행 결과 - 실패\n" +
                                    "t : $t")
                        }

                    })*/
                    }
                    finish()
                }
            }
            R.id.date_til, R.id.date_tf -> {
                val dateDialog = DateDialog(dateData[0], dateData[1], dateData[2])
                dateDialog.setOnButtonClickListener(this)
                dateDialog.show(supportFragmentManager, dateDialog.tag)
            }
            R.id.time_til, R.id.time_tf -> {
                val timeDialog = TimeDialog(data.time)
                timeDialog.setBottomSheetListener(this)
                timeDialog.show(supportFragmentManager, timeDialog.tag)
            }
            R.id.picture_plus_view -> {
                // 현재 기기에 설정된 읽기/쓰기 권한을 가져오기 위한 변수
                val readPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                val writePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE )
                if (writePermission == PackageManager.PERMISSION_DENIED || readPermission == PackageManager.PERMISSION_DENIED) {
//                    읽기 권한과 쓰기 권한에 대해서 설정이 되어있지 않다면 읽기, 쓰기 권한을 요청합니다.
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        1)
                }
                else {// 위 경우가 아니라면 권한에 대해서 설정이 되어 있으므로
                    val state = Environment.getExternalStorageState()

                    // 갤러리를 열어서 파일을 선택할 수 있도록 합니다.
                    if (TextUtils.equals(state, Environment.MEDIA_MOUNTED)) {
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.type = "image/*"
                        activityResult.launch(intent)
                    }
                }
            }
            R.id.picture_delete_iv -> {
                binding.pictureIv.visibility = View.INVISIBLE
                binding.pictureDeleteIv.visibility = View.INVISIBLE

                binding.countPictureTv.text = "사진 추가"
            }
        }
    }

    //    uri 가지고 해당 파일의 진짜 위치를 보낸다.
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

    override fun onAdditionalInfoSubmitted(info: String) {
            binding.timeTf.setText(info)
            checkEnable()
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

    fun checkEnable(){
        if(binding.nameTf.text!!.isNotEmpty() && binding.dateTf.text!!.isNotEmpty() && binding.timeTf.text!!.isNotEmpty()){
            binding.finishTv.isEnabled = true
        }
    }

    override fun onButtonClicked(year: String, month: String, date: String) {
        binding.dateTf.setText("$year. $month. $date")
        dateData = arrayListOf(year, month, date)
        checkEnable()
    }
}