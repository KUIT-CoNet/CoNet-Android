package com.kuit.conet.UI.GroupMain

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.kuit.conet.Network.RetrofitClient
import com.kuit.conet.R
import com.kuit.conet.UI.Group.GroupAdapter
import com.kuit.conet.UI.GroupMain.dialog.AllMembersDialog
import com.kuit.conet.UI.Plan.CreatePlanActivity
import com.kuit.conet.UI.Plan.detail.PlanListActivity
import com.kuit.conet.UI.application.CoNetApplication
import com.kuit.conet.Utils.*
import com.kuit.conet.Utils.calendar.compareWithLocalDate
import com.kuit.conet.Utils.calendar.toString
import com.kuit.conet.databinding.ActivityGroupMainBinding
import com.kuit.conet.data.dto.request.member.RequestPostBookmark
import com.kuit.conet.data.dto.response.member.ResponsePostBookmark
import com.kuit.conet.data.dto.response.team.ResponseGetGroupDetail
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.time.LocalDate

class GroupMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupMainBinding
    private val groupId: Long by lazy { intent.getLongExtra(GroupAdapter.INTENT_GROUP_ID, 0) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d(LIFECYCLE, "GroupMainActivity - onCreate() called")

        lifecycleScope.launch {
            val bearerAccessToken =
                CoNetApplication.getInstance().getDataStore().bearerAccessToken.first()
            getGroupDetail(bearerAccessToken, groupId)
        }


        binding.ivBackBtn.setOnClickListener {
            finish()
        }

        binding.ivStar.setOnClickListener {     // 북마크 삭제
            binding.ivStar.visibility = View.GONE
            binding.ivStarUn.visibility = View.VISIBLE
            lifecycleScope.launch {
                val bearerAccessToken =
                    CoNetApplication.getInstance().getDataStore().bearerAccessToken.first()
                postBookmark(bearerAccessToken, groupId)
            }
        }
        binding.ivStarUn.setOnClickListener {       // 북마크 등록
            binding.ivStar.visibility = View.VISIBLE
            binding.ivStarUn.visibility = View.GONE

            lifecycleScope.launch {
                val bearerAccessToken =
                    CoNetApplication.getInstance().getDataStore().bearerAccessToken.first()
                postBookmark(bearerAccessToken, groupId)
            }
        }

        binding.tvPlanMaker.setOnClickListener {
            val intent = Intent(this, CreatePlanActivity::class.java)
            intent.putExtra(INTENT_GROUP_ID, groupId.toInt())
            startActivity(intent)
        }

        binding.llGroupMainMembers.setOnClickListener {
            val allMembersDialog = AllMembersDialog()
            allMembersDialog.arguments = Bundle().apply {
                putLong(BUNDLE_GROUP_ID, groupId)
            }
            allMembersDialog.show(supportFragmentManager, AllMembersDialog.TAG)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(LIFECYCLE, "GroupMainActivity - onResume() called")

        val groupCalendarFragment = GroupCalendarFragment(groupId.toInt())
        groupCalendarFragment.setOnDateChangedListener { widget, date, selected ->
            val today = LocalDate.now()
            if (date.compareWithLocalDate(today)) {
                binding.tvGroupMainSelectDayTitle.text = getString(R.string.today_plan)
            } else {
                binding.tvGroupMainSelectDayTitle.text = "${date.toString(false)}의 약속"
            }

            val todolist = Todolist(date, groupId.toInt())
            supportFragmentManager.commit {
                replace(R.id.fl_todolist, todolist)
            }
            lifecycleScope.launch {
                todolist.waitForInit()
                binding.tvGroupMainSelectPromiseCount.text = todolist.returnsize().toString()
            }
        }

        supportFragmentManager.commit {
            replace(R.id.fl_group_calendar, groupCalendarFragment)
        }
    }

    fun showDetail(option: Int, groupId: Int) {
        val intent = Intent(this, PlanListActivity::class.java)
        intent.putExtra(INTENT_GROUP_ID, groupId)
        intent.putExtra(INTENT_SIDE_OPTION, option)
        startActivity(intent)
    }

    private fun checkFavoriteTag(favoriteTag: Boolean) {
        if (favoriteTag) {
            binding.ivStar.visibility = View.VISIBLE
            binding.ivStarUn.visibility = View.GONE
        } else {
            binding.ivStar.visibility = View.GONE
            binding.ivStarUn.visibility = View.VISIBLE
        }
    }

    private fun getGroupDetail(accessToken: String, groupId: Long) {
        RetrofitClient.teamInstance.getGroupDetail(
            authorization = accessToken,
            groupId = groupId
        ).enqueue(object : retrofit2.Callback<ResponseGetGroupDetail> {
            override fun onResponse(
                call: Call<ResponseGetGroupDetail>,
                response: Response<ResponseGetGroupDetail>
            ) {
                if (response.isSuccessful) {
                    Log.d(NETWORK, "GroupMainActivity - getGroupDetail()실행 결과 - 성공")

                    val resp =
                        requireNotNull(response.body()) { "GroupMainActivity's getGroupDetail 결과 불러오기 실패" }
                    val groupData = resp.result.asGroup()

                    binding.tvGroupName.text = groupData.name
                    binding.tvCount.text = "${groupData.memberCount} 명"
                    checkFavoriteTag(groupData.isFavorite)
                    Glide.with(this@GroupMainActivity)
                        .load(groupData.imageUrl) // 불러올 이미지 url
                        .centerCrop()
                        .placeholder(R.drawable.profile_purple) // 이미지 로딩 시작하기 전 표시할 이미지
                        .error(R.drawable.profile_purple) // 로딩 에러 발생 시 표시할 이미지
                        .fallback(R.drawable.profile_purple) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
                        .into(binding.ivGroupImage) // 이미지를 넣을 뷰

                    binding.ivMenuBtn.setOnClickListener {
                        val sideBar = SideBar(
                            groupData.name,
                            groupData.memberCount,
                            groupData.id,
                            this@GroupMainActivity,
                            groupData.imageUrl
                        )

                        supportFragmentManager.commit {
                            add(R.id.fl_sidebar_menu, sideBar)
                            binding.flSidebarMenu.visibility = View.VISIBLE
                            setCustomAnimations(R.anim.to_left, R.anim.from_right)
                        }

                        sideBar.setOnItemClickListener(object : SideBar.OnItemClickListener {
                            override fun onItemClick(option: Int) {
                                showDetail(option, groupId.toInt())
                            }

                            override fun exitSidebar() {
                                supportFragmentManager.commit {
                                    val sideBarFragment =
                                        supportFragmentManager.findFragmentById(R.id.fl_sidebar_menu) as SideBar
                                    binding.flSidebarMenu.visibility = View.GONE
                                    remove(sideBarFragment)
                                }
                            }
                        })
                    }
                } else {
                    Log.d(
                        NETWORK,
                        "GroupMainActivity - getGroupDetail()실행 결과 - 안좋음\nresponse : $response"
                    )
                    finish()
                }
            }

            override fun onFailure(call: Call<ResponseGetGroupDetail>, t: Throwable) {
                Log.d(NETWORK, "GroupMainActivity - getGroupDetail()실행 결과 - 실패\nbecause: $t")
                finish()
            }

        })
    }

    private fun postBookmark(accessToken: String, groupId: Long) {
        RetrofitClient.memberInstance.postBookmark(
            authorization = accessToken,
            request = RequestPostBookmark(
                teamId = groupId
            )
        ).enqueue(object : retrofit2.Callback<ResponsePostBookmark> {
            override fun onResponse(
                call: Call<ResponsePostBookmark>,
                response: Response<ResponsePostBookmark>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()?.result ?: "실행결과 불러오기 실패"
                    Log.d(
                        NETWORK,
                        "GroupAdapter - Retrofit postBookmark() 북마크 등록 실행결과 - 성공\nresult : $result"
                    )
                } else {
                    Log.d(
                        NETWORK,
                        "GroupAdapter - Retrofit postBookmark() 북마크 등록 실행결과 - 안좋음"
                    )
                }
            }

            override fun onFailure(call: Call<ResponsePostBookmark>, t: Throwable) {
                Log.d(NETWORK, "GroupAdapter - Retrofit postBookmark() 북마크 등록 실행결과 - 실패")
            }
        })
    }

    companion object {
        const val INTENT_GROUP_ID = "GroupId"
        const val INTENT_SIDE_OPTION = "OPTION"
        const val BUNDLE_GROUP_ID = "groupId"
    }
}