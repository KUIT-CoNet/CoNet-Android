package com.kuit.conet.UI.User

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.kuit.conet.*
import com.kuit.conet.Network.*
import com.kuit.conet.Utils.LIFECYCLE
import com.kuit.conet.Utils.getUserImg
import com.kuit.conet.Utils.getUsername
import com.kuit.conet.databinding.FragmentUserBinding


class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding
        get() = requireNotNull(_binding) { "UserFragment's binding is null" }
    private lateinit var userData: ShowUserInfo

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        Log.d(LIFECYCLE, "UserFragment: onCreateView called")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(LIFECYCLE, "UserFragment: onViewCreated called")

        binding.cvUserInfo.setOnClickListener {
            val intent = Intent(requireContext(), InfoActivity::class.java)
            intent.putExtra("imageURL", getUserImg(requireContext()))
            startActivity(intent)
        }

        binding.cvUserNotice.setOnClickListener {
            val intent = Intent(requireContext(), NoticeActivity::class.java)
            startActivity(intent)
        }

        binding.cvUserInquiry.setOnClickListener {
            val intent = Intent(requireContext(), InquiryActivity::class.java)
            startActivity(intent)
        }

        binding.cvUserTerms.setOnClickListener {
            val urlIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://conet.notion.site/f94cdd700d5e4ce3a38bffaa9719df07")
            )
            startActivity(urlIntent)
        }

        binding.tvUserLogout.setOnClickListener {
            val logoutDialog = LogoutDialog()
            parentFragmentManager.beginTransaction()
                .replace(R.id.cl_user, logoutDialog)
                .commit()
        }

        binding.tvUserName.text = getUsername(requireContext())

        Glide.with(requireContext())
            .load(getUserImg(requireContext())) // 불러올 이미지 url
            .placeholder(R.drawable.profile_purple) // 이미지 로딩 시작하기 전 표시할 이미지
            .error(R.drawable.profile_purple) // 로딩 에러 발생 시 표시할 이미지
            .fallback(R.drawable.profile_purple) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
            .circleCrop() // 동그랗게 자르기 동그란 맘속에 피어난 How is the life...
            .into(binding.ivUserProfile) // 이미지를 넣을 뷰
    }

    override fun onStart() {
        super.onStart()
        Log.d(LIFECYCLE, "UserFragment: onStart called")

    }

//    private fun callUserInfo() {
//        var responseUser = getRetrofit().create(RetrofitInterface::class.java)
//        val refreshToken = getRefreshToken(requireContext())
//        responseUser.showuser(
//            "Bearer $refreshToken"
//        ).enqueue(object :
//            retrofit2.Callback<ShowUser>{
//            override fun onResponse(call: Call<ShowUser>, response: Response<ShowUser>) {
//                if (response.isSuccessful) {
//                    var resp = response.body()// 성공했을 경우 response body불러오기
//                    Log.d(NETWORK, resp.toString())
//                    var userData  = resp!!.result
//                    binding.tvUserName.text = userData.name
//                    var imageUri = userData.memberImgUrl.toUri()
//                    saveUserImgUrl(requireContext(), imageUri.toString())
//                    Glide.with(requireContext())
//                        .load(imageUri) // 로컬 파일로 변환한 이미지를 로드
//                        .placeholder(R.drawable.profile_purple)
//                        .error(R.drawable.profile_purple)
//                        .fallback(R.drawable.profile_purple)
//                        .circleCrop()
//                        .into(binding.ivUserProfile)
//                } else {
////                    continuation.resumeWithException(Exception("Response not successful"))
//                    Log.d(NETWORK, "[callUserInfo in User] onResponse: Response not successful")
//                }
//            }
//
//            override fun onFailure(call: Call<ShowUser>, t: Throwable) {
//                Log.d(NETWORK, t.message.toString()) // 실패한 이유 메세지 출력
////                continuation.resumeWithException(t)
//            }
//
//        })
//    }
}