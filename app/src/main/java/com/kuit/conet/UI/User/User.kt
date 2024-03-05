package com.kuit.conet.UI.User

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.kuit.conet.*
import com.kuit.conet.Network.*
import com.kuit.conet.Utils.getRefreshToken
import com.kuit.conet.Utils.getUserImg
import com.kuit.conet.Utils.getUsername
import com.kuit.conet.Utils.saveUserEmail
import com.kuit.conet.Utils.saveUserImgUrl
import com.kuit.conet.Utils.saveUsername
import com.kuit.conet.databinding.FragmentUserBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class User : Fragment(){
    lateinit var binding : FragmentUserBinding
    private lateinit var userData : ShowUserInfo

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUserBinding.inflate(inflater, container, false)
        CoroutineScope(Dispatchers.Main).launch {
            userData = callUserInfo()
            saveUsername(requireContext(), userData.name)
            saveUserImgUrl(requireContext(), userData.memberImgUrl)
            saveUserEmail(requireContext(), userData.email)
            binding.tvUserName.text = userData.name
            Glide.with(requireContext())
                .load(userData.memberImgUrl) // 불러올 이미지 url
                .placeholder(R.drawable.profile_purple) // 이미지 로딩 시작하기 전 표시할 이미지
                .error(R.drawable.profile_purple) // 로딩 에러 발생 시 표시할 이미지
                .fallback(R.drawable.profile_purple) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
                .circleCrop() // 동그랗게 자르기 동그란 맘속에 피어난 How is the life...
                .into(binding.ivUserProfile) // 이미지를 넣을 뷰
        }
        binding.cvUserInfo.setOnClickListener {
            val intent = Intent(requireContext(), InfoActivity::class.java)
            startActivity(intent)
        }

        binding.cvUserNotice.setOnClickListener {
            val intent = Intent(requireContext(), NoticeActivity::class.java)
            startActivity(intent)
        }

        binding.cvUserInquiry.setOnClickListener{
            val intent = Intent(requireContext(), InquiryActivity::class.java)
            startActivity(intent)
        }

        binding.cvUserTerms.setOnClickListener {
            //문의하기 노션페이지로 이동?
        }

        binding.tvUserLogout.setOnClickListener {
            val logoutDialog = LogoutDialog()
            parentFragmentManager.beginTransaction()
                .replace(R.id.cl_user, logoutDialog)
                .commit()
        }

        return binding.root
    }

    override fun onStop() {
        super.onStop()
        Log.d("stop","프래그먼트 종료")
    }

    override fun onStart() {
        super.onStart()
        Log.d("start","프래그먼트 시작")
        binding.tvUserName.text = getUsername(requireContext())
        Glide.with(requireContext())
            .load(getUserImg(requireContext())) // 불러올 이미지 url
            .placeholder(R.drawable.profile_purple) // 이미지 로딩 시작하기 전 표시할 이미지
            .error(R.drawable.profile_purple) // 로딩 에러 발생 시 표시할 이미지
            .fallback(R.drawable.profile_purple) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
            .circleCrop() // 동그랗게 자르기 동그란 맘속에 피어난 How is the life...
            .into(binding.ivUserProfile) // 이미지를 넣을 뷰
    }


    private suspend fun callUserInfo() : ShowUserInfo{
        return suspendCoroutine {
            continuation ->
            val responseUser = getRetrofit().create(RetrofitInterface::class.java)
            val refreshToken = getRefreshToken(requireContext())
            responseUser.showuser(
                "Bearer $refreshToken"
            ).enqueue(object :
            retrofit2.Callback<ShowUser>{
                override fun onResponse(call: Call<ShowUser>, response: Response<ShowUser>) {
                    if (response.isSuccessful) {
                        val resp = response.body()// 성공했을 경우 response body불러오기
                        Log.d("SIGNUP/SUCCESS", resp.toString())
                        Log.d("성공!","success")
                        val userData  = resp!!.result
                        continuation.resume(userData)
                    }
                    else{
                        continuation.resumeWithException(Exception("Response not successful"))
                    }
                }

                override fun onFailure(call: Call<ShowUser>, t: Throwable) {
                    Log.d("SIGNUP/FAILURE", t.message.toString()) // 실패한 이유 메세지 출력
                    continuation.resumeWithException(t)
                }

            })
        }
    }


}