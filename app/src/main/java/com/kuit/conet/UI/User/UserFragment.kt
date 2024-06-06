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

            getUserInfo(bearerAccessToken)
        }
    }

    private fun getUserInfo(accessToken: String) {
        RetrofitClient.memberInstance.getUserInfo(
            authorization = accessToken,
        ).enqueue(object : retrofit2.Callback<ResponseGetUserInfo> {
            override fun onResponse(
                call: retrofit2.Call<ResponseGetUserInfo>,
                response: retrofit2.Response<ResponseGetUserInfo>
            ) {
                if (response.isSuccessful) {
                    Log.d(NETWORK, "UserFragment - getUserInfo 호출 결과 - 성공")

                    val resp =
                        requireNotNull(response.body()) { "UserFragment - getUserInfo 호출 결과 불러오기 실패" }
                    userInfo = resp.result.asUser()

                    binding.tvUserName.text = userInfo.name
                    Glide.with(requireContext())
                        .load(userInfo.imgUrl)
                        .placeholder(R.drawable.profile_purple)
                        .error(R.drawable.profile_purple)
                        .fallback(R.drawable.profile_purple)
                        .circleCrop()
                        .into(binding.ivUserProfile)
                } else {
                    Log.d(NETWORK, "UserFragment - getUserInfo 호출 결과 - 안좋음")
                }
            }

            override fun onFailure(call: retrofit2.Call<ResponseGetUserInfo>, t: Throwable) {
                Log.d(NETWORK, "UserFragment - getUserInfo 호출 결과 - 실패\nbecause : ${t.message}")
            }
        })
    }

    companion object {
        const val INTENT_TAG_USER = "userInfo"
    }
}