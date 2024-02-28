package com.kuit.conet.UI.GroupMain

import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.bumptech.glide.Glide
import com.kuit.conet.Network.ResponseBookmark
//import com.kuit.conet.Network.ResponseDeleteBookmark
//import com.kuit.conet.Network.ResponseEnrollBookmark
import com.kuit.conet.Network.RetrofitClient
import com.kuit.conet.R
import com.kuit.conet.UI.Group.GroupAdapter
import com.kuit.conet.UI.Group.GroupData
import com.kuit.conet.UI.Plan.CreatePlanActivity
import com.kuit.conet.UI.Plan.detail.PlanListActivity
import com.kuit.conet.Utils.*
import com.kuit.conet.databinding.ActivityGroupMainBinding
import com.kuit.conet.getAccessToken
import com.prolificinteractive.materialcalendarview.CalendarDay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class GroupMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupMainBinding
    private val fragmentManager = supportFragmentManager
    private lateinit var groupData: GroupData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var _groupData: GroupData?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            _groupData =
                intent.getSerializableExtra(GroupAdapter.INTENT_GROUP, GroupData::class.java)
        } else {
            _groupData = intent.getSerializableExtra(GroupAdapter.INTENT_GROUP) as GroupData?
        }
        groupData = requireNotNull(_groupData)

        binding.tvGroupName.text = groupData.groupName
        binding.tvCount.text = "${groupData.groupMemberCount} 명"
        checkFavoriteTag(groupData.favoriteTag)
        Glide.with(this@GroupMainActivity)
            .load(groupData.groupUrl) // 불러올 이미지 url
            .centerCrop()
            .placeholder(R.drawable.profile_purple) // 이미지 로딩 시작하기 전 표시할 이미지
            .error(R.drawable.profile_purple) // 로딩 에러 발생 시 표시할 이미지
            .fallback(R.drawable.profile_purple) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
            .into(binding.ivGroupImage) // 이미지를 넣을 뷰

        binding.ivBackBtn.setOnClickListener {
            finish()
        }

        binding.ivStar.setOnClickListener {
            binding.ivStar.visibility = View.GONE
            binding.ivStarUn.visibility = View.VISIBLE
            RetrofitClient.instance.checkBookmark(
                "Bearer " + getAccessToken(this),
                groupData.groupId
            ).enqueue(object : retrofit2.Callback<ResponseBookmark> {
                override fun onResponse(
                    call: Call<ResponseBookmark>,
                    response: Response<ResponseBookmark>
                ) {
                    if (response.isSuccessful) {
                        Log.d(
                            NETWORK,
                            "GroupAdapter - Retrofit checkBookmark() 북마크 삭제 실행결과 - 성공\n" + "result : ${response.body()!!.result}"
                        )
                    } else {
                        Log.d(NETWORK, "GroupAdapter - Retrofit checkBookmark() 북마크 삭제 실행결과 - 안좋음")
                    }
                }

                override fun onFailure(call: Call<ResponseBookmark>, t: Throwable) {
                    Log.d(NETWORK, "GroupAdapter - Retrofit checkBookmark() 북마크 삭제 실행결과 - 실패")
                }

            })
        }

        binding.ivStarUn.setOnClickListener {
            binding.ivStar.visibility = View.VISIBLE
            binding.ivStarUn.visibility = View.GONE

            RetrofitClient.instance.checkBookmark(
                "Bearer " + getAccessToken(this),
                groupData.groupId
            ).enqueue(object : retrofit2.Callback<ResponseBookmark> {
                override fun onResponse(
                    call: Call<ResponseBookmark>,
                    response: Response<ResponseBookmark>
                ) {
                    if (response.isSuccessful) {
                        Log.d(
                            NETWORK,
                            "GroupAdapter - Retrofit checkBookmark() 북마크 추가 실행결과 - 성공\n" + "result : ${response.body()!!.result}"
                        )
                    } else {
                        Log.d(NETWORK, "GroupAdapter - Retrofit checkBookmark() 북마크 추가 실행결과 - 안좋음")
                    }
                }

                override fun onFailure(call: Call<ResponseBookmark>, t: Throwable) {
                    Log.d(NETWORK, "GroupAdapter - Retrofit checkBookmark() 북마크 추가 실행결과 - 실패")
                }

            })
        }

        binding.ivMenuBtn.setOnClickListener {
            val sideBar = SideBar(
                groupData.groupName,
                groupData.groupMemberCount,
                groupData.groupId,
                this,
                groupData.groupUrl
            )

            fragmentManager.commit {
                add(R.id.fl_group_image, sideBar)
                setCustomAnimations(R.anim.to_left, R.anim.from_right)
            }

            sideBar.setOnItemClickListener(object : SideBar.OnItemClickListener {
                override fun onItemClick(option: Int) {
                    showDetail(option, groupData.groupId)
                }

                override fun exitSidebar() {
                    fragmentManager.commit {
                        val sideBarFragment =
                            fragmentManager.findFragmentById(R.id.fl_group_image) as SideBar
                        if (sideBarFragment != null) remove(sideBarFragment)
                    }
                }
            })
        }

        binding.tvPlanMaker.setOnClickListener {
            val intent = Intent(this, CreatePlanActivity::class.java)
            intent.putExtra(INTENT_GROUP_ID, groupData.groupId)
            startActivity(intent)
        }

        /*val todolist = Todolist(CalendarDay.today(), groupData.groupId)
        fragmentManager.commit {
            replace(R.id.fl_todolist, todolist)
        }
        CoroutineScope(Dispatchers.Main).launch {
            todolist.waitForInit()
            binding.tvGroupMainSelectPromiseCount.text = todolist.returnsize().toString()
        }*/
    }

    override fun onResume() {
        super.onResume()
        Log.d(LIFECYCLE, "GroupMainActivity - onResume() called")

        val groupCalendarFragment = GroupCalendarFragment(groupData.groupId)
        groupCalendarFragment.setOnDateChangedListener { widget, date, selected ->
            val selectedDateString = dateString(date.date, 1)
            val today = Date() // 오늘 날짜
            val todayString = dateString(today, 1)
            if (todayString == selectedDateString) {
                binding.tvGroupMainSelectDayTitle.text = R.string.today_plan.toString()
            } else {
                binding.tvGroupMainSelectDayTitle.text = selectedDateString + "의 약속"
            }

            val todolist = Todolist(date, groupData.groupId)
            fragmentManager.commit {
                replace(R.id.fl_todolist, todolist)
            }
            CoroutineScope(Dispatchers.Main).launch {
                todolist.waitForInit()
                binding.tvGroupMainSelectPromiseCount.text = todolist.returnsize().toString()
            }
        }
        fragmentManager.commit {
            replace(R.id.fl_group_calendar, groupCalendarFragment)
        }
    }

    private fun dateString(
        date: Date,
        option: Int
    ): String { // option이 1이면 m월 d일 형식, option이 2면 yyyy년 m월 d일 형식
        val dateFormat: SimpleDateFormat = if (option == 1) {
            SimpleDateFormat("M월 d일", Locale.getDefault())
        } else {
            SimpleDateFormat("yyyy년 M월 d일", Locale.getDefault())
        }
        return dateFormat.format(date)
    }

    fun showDetail(option: Int, groupId: Int) {
        /*binding.flSidebarMenu.visibility = View.VISIBLE
        fragmentManager.beginTransaction()
            .replace(R.id.fl_sidebar_menu, SideBarDetailMenu(option, groupId))
            .commit()*/
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

    companion object {
        const val INTENT_GROUP_ID = "GroupId"
        const val INTENT_SIDE_OPTION = "OPTION"
    }
}