package com.kuit.conet.UI.GroupMain

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.kuit.conet.Network.ResponseDeleteBookmark
import com.kuit.conet.Network.ResponseEnrollBookmark
import com.kuit.conet.Network.RetrofitClient
import com.kuit.conet.R
import com.kuit.conet.UI.Plan.CreatePlanActivity
import com.kuit.conet.Utils.*
import com.kuit.conet.databinding.ActivityGroupMainBinding
import com.kuit.conet.getAccessToken
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class GroupMainActivity : AppCompatActivity() {

    lateinit var binding : ActivityGroupMainBinding
    lateinit var oncallList : OncallList


    override fun onResume() {
        super.onResume()
        binding = ActivityGroupMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val title = intent.getStringExtra("GroupName")
        val image = intent.getStringExtra("GroupImg")
        val groupId = intent.getIntExtra("GroupId", 0)
        val groupMemberCount = intent.getIntExtra("GroupMemberCount", 0)
        val groupFavoriteTag: Boolean = intent.getBooleanExtra("GroupFavorite", false)
        binding.tvGroupName.text = title
        binding.tvCount.text = groupMemberCount.toString() + "명"
        checkFavoriteTag(groupFavoriteTag)
        //binding.ivGroupImage.setImageResource(image)
        Glide.with(this)
            .load(image) // 불러올 이미지 url
            .centerCrop()
            .placeholder(R.drawable.profile_purple) // 이미지 로딩 시작하기 전 표시할 이미지
            .error(R.drawable.profile_purple) // 로딩 에러 발생 시 표시할 이미지
            .fallback(R.drawable.profile_purple) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
            .into(binding.ivGroupImage) // 이미지를 넣을 뷰

        val groupCalendarFragment = GroupCalendarFragment(groupId)
        groupCalendarFragment.setOnDateChangedListener(object : OnDateSelectedListener {
            override fun onDateSelected(
                widget: MaterialCalendarView,
                date: CalendarDay,
                selected: Boolean
            ) {
                Log.d("인터페이스","실행")
                val selectedDateString = dateString(date.date, 1)
                val today = Date() // 오늘 날짜
                val todayString = dateString(today, 1)
                // 로그로 선택한 날짜 정보 출력
                Log.d("Selected date", "${selectedDateString}")

                if (todayString == selectedDateString) {
                    binding.tvPromiseDate.text = "오늘의 약속"
                } else {
                    binding.tvPromiseDate.text = selectedDateString + "의 약속"
                }
                Log.d("dateString테스트", "${dateString(date.date, 2)}")
                val todolist = Todolist(date, groupId)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fl_todolist, todolist)
                    .commitAllowingStateLoss()
                CoroutineScope(Dispatchers.Main).launch {
                    todolist.waitForInit()
                    binding.tvPromiseCount.text = todolist.returnsize().toString()
                }
            }
        })
        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_group_calendar, groupCalendarFragment)
            .commitAllowingStateLoss()

        oncallList = OncallList(groupId, 1)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_oncalllist, oncallList)
            .commitAllowingStateLoss()

        CoroutineScope(Dispatchers.Main).launch {
            oncallList.waitForInit()
            binding.tvPromiseallCount.text = oncallList.returnsize().toString()
        }

        binding.ivBackBtn.setOnClickListener {
            finish()
        }

        binding.starIv.setOnClickListener {
            binding.starIv.visibility = View.GONE
            binding.starUnIv.visibility = View.VISIBLE
            RetrofitClient.instance.deleteBookmark("Bearer " + getAccessToken(this),
                groupId).enqueue(object: retrofit2.Callback<ResponseDeleteBookmark>{
                override fun onResponse(
                    call: Call<ResponseDeleteBookmark>,
                    response: Response<ResponseDeleteBookmark>
                ) {
                    if(response.isSuccessful){
                        Log.d(TAG, "GroupMainActivity - Retrofit deleteBookmark() 실행결과 - 성공\n" +
                                "result : ${response.body()!!.result}")
                    } else {
                        Log.d(TAG, "GroupMainActivity - Retrofit deleteBookmark() 실행결과 - 안좋음")
                    }
                }
                override fun onFailure(call: Call<ResponseDeleteBookmark>, t: Throwable) {
                    Log.d(TAG, "GroupMainActivity - Retrofit deleteBookmark() 실행결과 - 실패")
                }
            })
        }

        binding.starUnIv.setOnClickListener {
            binding.starIv.visibility = View.VISIBLE
            binding.starUnIv.visibility = View.GONE
            RetrofitClient.instance.enrollBookmark("Bearer " + getAccessToken(this),
                groupId).enqueue(object: retrofit2.Callback<ResponseEnrollBookmark>{
                override fun onResponse(
                    call: Call<ResponseEnrollBookmark>,
                    response: Response<ResponseEnrollBookmark>
                ) {
                    if(response.isSuccessful){
                        Log.d(TAG, "GroupMainActivity - Retrofit enrollBookmark() 실행결과 - 성공\n" +
                                "result : ${response.body()!!.result}")
                    } else {
                        Log.d(TAG, "GroupMainActivity - Retrofit enrollBookmark() 실행결과 - 안좋음")
                    }
                }

                override fun onFailure(call: Call<ResponseEnrollBookmark>, t: Throwable) {
                    Log.d(TAG, "GroupMainActivity - Retrofit enrollBookmark() 실행결과 - 실패")
                }

            })
        }
        val sideBar = SideBar(title, groupMemberCount, groupId, this, image)
        binding.ivMenuBtn.setOnClickListener {
            binding.flSidebar.visibility = View.VISIBLE
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.to_left, R.anim.from_right)
                .replace(R.id.fl_sidebar, sideBar)
                .commitAllowingStateLoss()
        }

        sideBar?.setOnItemClickListener(object : SideBar.OnItemClickListener{
            override fun onItemClick(option: Int) {
                showDetail(option, groupId)
            }
        })

        binding.cvMakePlan.setOnClickListener {
            val intent = Intent(this, CreatePlanActivity::class.java)
            intent.putExtra("GroupId",groupId)
            startActivity(intent)
        }

    }
    fun dateString(date : Date, option : Int) : String{ // option이 1이면 m월 d일 형식, option이 2면 yyyy년 m월 d일 형식
        var dateFormat : SimpleDateFormat
        if(option == 1){
            dateFormat = SimpleDateFormat("M월 d일", Locale.getDefault())
        }
        else{
            dateFormat = SimpleDateFormat("yyyy년 M월 d일", Locale.getDefault())
        }
        val selectedDateString = dateFormat.format(date)
        return selectedDateString
    }

    fun showDetail(option : Int, groupId : Int){
        binding.flSidebarMenu.visibility = View.VISIBLE
        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_sidebar_menu, SideBarDetailMenu(option, groupId))
            .commit()
    }

    fun checkFavoriteTag(favortieTag: Boolean){
        if(favortieTag){
            binding.starIv.visibility = View.VISIBLE
            binding.starUnIv.visibility = View.GONE
        } else{
            binding.starIv.visibility = View.GONE
            binding.starUnIv.visibility = View.VISIBLE
        }
    }
}