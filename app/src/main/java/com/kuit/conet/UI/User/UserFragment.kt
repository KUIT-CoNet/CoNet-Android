package com.kuit.conet.UI.User

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.kuit.conet.Network.RetrofitClient
import com.kuit.conet.R
import com.kuit.conet.UI.application.CoNetApplication
import com.kuit.conet.Utils.LIFECYCLE
import com.kuit.conet.Utils.NETWORK
import com.kuit.conet.data.dto.response.member.ResponseGetUserInfo
import com.kuit.conet.databinding.FragmentUserBinding
import com.kuit.conet.domain.entity.user.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding
        get() = requireNotNull(_binding) { "UserFragment's binding is null" }
    private lateinit var userInfo: User

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
            intent.putExtra(INTENT_TAG_USER, userInfo)
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
    }


    override fun onStart() {
        super.onStart()
        Log.d(LIFECYCLE, "UserFragment: onStart called")

        viewLifecycleOwner.lifecycleScope.launch {
            val bearerAccessToken =
                CoNetApplication.getInstance().getDataStore().bearerAccessToken.first()

            userInfo = getUserInfo(bearerAccessToken)

            binding.tvUserName.text = userInfo.name
            Glide.with(requireContext())
                .load(userInfo.imgUrl)
                .placeholder(R.drawable.profile_purple)
                .error(R.drawable.profile_purple)
                .fallback(R.drawable.profile_purple)
                .circleCrop()
                .into(binding.ivUserProfile)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d(LIFECYCLE, "UserFragment: onDestroyView called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(LIFECYCLE, "UserFragment: onStop called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(LIFECYCLE, "UserFragment: onDestroy called")
    }

    private suspend fun getUserInfo(accessToken: String): User =
        suspendCancellableCoroutine { continuation ->

            val call = RetrofitClient.memberInstance.getUserInfo(
                authorization = accessToken,
            )

            continuation.invokeOnCancellation {
                Log.d(NETWORK, "UserFragment - getUserInfo 호출 취소됨")
                call.cancel()
            }

            call.enqueue(object : retrofit2.Callback<ResponseGetUserInfo> {
                override fun onResponse(
                    call: retrofit2.Call<ResponseGetUserInfo>,
                    response: retrofit2.Response<ResponseGetUserInfo>
                ) {
                    if (response.isSuccessful) {
                        Log.d(NETWORK, "UserFragment - getUserInfo 호출 결과 - 성공")

                        val resp =
                            requireNotNull(response.body()) { "UserFragment - getUserInfo 호출 결과 불러오기 실패" }
                        continuation.resume(resp.result.asUser())
                    } else {
                        Log.d(NETWORK, "UserFragment - getUserInfo 호출 결과 - 안좋음")
                        continuation.resume(User.EMPTY_USER)
                    }
                }

                override fun onFailure(call: retrofit2.Call<ResponseGetUserInfo>, t: Throwable) {
                    Log.d(NETWORK, "UserFragment - getUserInfo 호출 결과 - 실패\nbecause : ${t.message}")
                    continuation.resumeWithException(t)
                }
            })
        }

    companion object {
        const val INTENT_TAG_USER = "userInfo"
    }
}