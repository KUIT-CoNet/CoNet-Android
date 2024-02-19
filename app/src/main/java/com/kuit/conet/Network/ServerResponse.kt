package com.kuit.conet.Network

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Login(
    @SerializedName("platform") var platform: String,
    @SerializedName("idToken") var idToken: String?
)

data class KaKaoResponse( // 맨 처음 카카오 로그인 했을 때 id token넘겨주고 그에 대한 응답을 처리하는 데이터 클래스
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: resultData
)

data class resultData(
    @SerializedName("email") val email: String,
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String,
    @SerializedName("isRegistered") val isRegistered: Boolean
)

data class refreshResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: resultToken
)

data class resultToken(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String,
    @SerializedName("email") val email: String
)

data class registedResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: userInfo
)

data class sendInfo(
    @SerializedName("name") val name: String,
)

data class userInfo(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("serviceTerm") val serviceTerm: Boolean,
    @SerializedName("optionTerm") val optionTerm: Boolean
)

// 홈 화면 , 모임 화면 확정 약속 몇일 몇일 있는지 반환
data class HomePlanShow(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: PromiseDate
)

data class PromiseDate(
    @SerializedName("count") val count: Int,
    @SerializedName("dates") val dates: ArrayList<Int>
)

//홈 화면 특정 날짜의 약속 조회(응답)
data class HomePlanInfo(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: PromiseInfo
)

data class PromiseInfo(
    @SerializedName("count") val count: Int?,
    @SerializedName("plans") val plans: ArrayList<plans>
)

data class plans(
    @SerializedName("planId") val planId: Int,
    @SerializedName("date") val date: String,
    @SerializedName("time") val time: String,
    @SerializedName("teamName") val teamName: String,
    @SerializedName("planName") val planName: String,
    @SerializedName("dday") val dday: String?,
//    @SerializedName("isRegisteredToHistory") val isRegisteredToHistory: Boolean?
)

// 대기 중 약속 조회
data class HomeOncall(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: ListOncall
)

data class ListOncall(
    @SerializedName("count") val count: Int,
    @SerializedName("plans") val plans: ArrayList<Oncall>
)

data class Oncall(
    @SerializedName("planId") val planId: Int,
    @SerializedName("startDate") val startDate: String,
    @SerializedName("endDate") val endDate: String,
    @SerializedName("teamName") val teamName: String,
    @SerializedName("planName") val planName: String
)

//사용자 조회
data class ShowUser(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: ShowUserInfo
)

data class ShowUserInfo(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("memberImgUrl") val memberImgUrl: String,
    @SerializedName("platform") val platform: String
)

//사용자 이름 변경
data class EditUserName(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: String
)

//사용자 이미지 변경

data class EditUserImage(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: UserImage
)

data class UserImage(
    @SerializedName("name") val name: String,
    @SerializedName("imgUrl") val imgUrl: String
)

data class DeleteUser(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: String
)

data class ResponseGetGroup(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: ArrayList<ResultGetGroup>
)

data class ResultGetGroup(
    @SerializedName("teamId") val groupId: Int,
    @SerializedName("teamName") var groupName: String,
    @SerializedName("teamImgUrl") var groupUrl: String,
    @SerializedName("teamMemberCount") val groupMemberCount: Int,
    @SerializedName("isNew") val newTag: Boolean,
    @SerializedName("bookmark") var favoriteTag: Boolean
)

data class ResponseCreateGroup(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: ResultCreateGroup
)

data class ResultCreateGroup(
    @SerializedName("teamId") val groupId: String,
    @SerializedName("inviteCode") val enrollCode: String
)

//모임 참여
data class ResponseEnrollGroup(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: ResultEnrollGroup
)

data class ResultEnrollGroup(
    @SerializedName("userName") val userName: String,
    @SerializedName("teamName") val groupName: String,
    @SerializedName("status") val status: Boolean
)

//참여코드조회
data class ResponseGroupCode(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: ResultEnrollCode
)

data class ResultEnrollCode(
    @SerializedName("teamId") val groupId: Int,
    @SerializedName("inviteCode") val inviteCode: String,
    @SerializedName("codeDeadLine") val codeDeadLine: String

)

data class ResponseUpdateGroup(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: ResultUpdateGroup
)

data class ResultUpdateGroup(
    @SerializedName("name") val groupName: String,
    @SerializedName("imageUrl") val imageUrl: String
)

data class ResponseGetGroupMembers(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: ArrayList<Members>
)

data class ResultGetGroupMembers(
    @SerializedName("userId") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("image") val image: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: null
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(image)
    }

    companion object CREATOR : Parcelable.Creator<Members> {
        override fun createFromParcel(parcel: Parcel): Members {
            return Members(parcel)
        }

        override fun newArray(size: Int): Array<Members?> {
            return arrayOfNulls(size)
        }
    }
}

data class ResponseDeletePlan(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: String
)

data class ResponseUpdatePlanDetail(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: String
)

//약속 생성 - 요청 부분

data class MakePlanInfo(
    @SerializedName("teamId") val teamId: Int,
    @SerializedName("planName") val planName: String?,
    @SerializedName("planStartDate") val planStartDate: String?
)

//약속 생성 - 응답 부분

data class ResponseMakePlan(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: MakePlanId
)

data class MakePlanId(
    @SerializedName("planId") val planId: Int
)

data class ResponseBookmark(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: String
)

data class Notice(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: NoticeInfo
)

data class NoticeInfo(
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("date") val date: String
)

//가능한 시간 저장
data class InputMyTime(
    @SerializedName("planId") val planId: Int,
    @SerializedName("availableDateTimes") val availableDateTimes: ArrayList<AvailableDateTimes>
)

data class ResponseInputMyTime(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: String
)

data class AvailableDateTimes(
    @SerializedName("date") var date: String,
    @SerializedName("time") val time: ArrayList<Int>
)

//나의 가능한 시간 조회
data class ShowMyTime(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: MyTimeInfo,
)

data class MyTimeInfo(
    @SerializedName("planId") val planId: Int,
    @SerializedName("memberId") val memberId: Int,
    @SerializedName("availableTimeRegisteredStatus") val availableTimeRegisteredStatus: Int, // 0: 입력한 적 없음, 1: 가능한 시간 없음, 2: 시간 있음
    @SerializedName("timeSlot") val timeSlot: ArrayList<UserAvailableTimeDTO>,
)

data class UserAvailableTimeDTO(
    @SerializedName("date") var date: String,
    @SerializedName("availableTimes") var availableTimes: ArrayList<Int>,
)

data class ResponseGetPlanDetail(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: ResultGetPlanDetail
)

data class ResultGetPlanDetail(
    @SerializedName("planId") val planId: Int,
    @SerializedName("planName") val planName: String,
    @SerializedName("date") val date: String,
    @SerializedName("time") val time: String,
    @SerializedName("memberCount") val memberCount: Int,
    @SerializedName("members") val members: ArrayList<Members>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.createTypedArrayList(Members.CREATOR) ?: ArrayList(),
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(planId)
        parcel.writeString(planName)
        parcel.writeString(date)
        parcel.writeString(time)
        parcel.writeTypedList(members)
//        parcel.writeByte(if(isRegisteredToHistory) 1 else 0)
//        parcel.writeString(historyImageUrl)
//        parcel.writeString(historyDescription)
    }

    companion object CREATOR : Parcelable.Creator<ResultGetPlanDetail> {
        override fun createFromParcel(parcel: Parcel): ResultGetPlanDetail {
            return ResultGetPlanDetail(parcel)
        }

        override fun newArray(size: Int): Array<ResultGetPlanDetail?> {
            return arrayOfNulls(size)
        }
    }

}

data class UpdateWaiting( //약속 수정- 대기중
    @SerializedName("planId") val planId: Int,
    @SerializedName("planName") val planName: String
)

data class ResponseUpdateWaiting(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: String
)

//구성원의 가능한 시간 조회
data class ShowMemTime(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: MemTimeInfo
)

data class MemTimeInfo(
    @SerializedName("teamId") val teamId: Int,
    @SerializedName("planId") val planId: Int,
    @SerializedName("planName") val planName: String,
    @SerializedName("planStartPeriod") val planStartPeriod: String,
    @SerializedName("planEndPeriod") val planEndPeriod: String,
    @SerializedName("endNumberForEachSection") val endNumberForEachSection: ArrayList<EndNumberForEachSection>,
    @SerializedName("availableMemberDateTime") val availableMemberDateTime: ArrayList<AvailableMemberDateTime>
)

data class EndNumberForEachSection(
    @SerializedName("section") val section: Int,
    @SerializedName("memberCount") val memberCount: Int
)

data class AvailableMemberDateTime(
    @SerializedName("date") val date: String,
    @SerializedName("possibleMember") val possibleMember: ArrayList<PossibleMember>
)

data class PossibleMember(
    @SerializedName("time") var time: Int,
    @SerializedName("section") var section: Int,
    @SerializedName("memberNames") var memberNames: ArrayList<String>,
    @SerializedName("memberIds") var memberIds: ArrayList<Int>
)

//약속 확정
data class FixPlan(
    @SerializedName("planId") var planId: Int,
    @SerializedName("fixedDate") var fixedDate: String,
    @SerializedName("fixedTime") var fixedTime: Int,
    @SerializedName("memberIds") var memberIds: ArrayList<Int>
)

data class ResponseFixPlan(
    @SerializedName("code") var code: Int,
    @SerializedName("status") var status: Int,
    @SerializedName("message") var message: String,
    @SerializedName("result") var result: String
)


data class Members(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("image") val image: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: null
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(image)
    }

    companion object CREATOR : Parcelable.Creator<Members> {
        override fun createFromParcel(parcel: Parcel): Members {
            return Members(parcel)
        }

        override fun newArray(size: Int): Array<Members?> {
            return arrayOfNulls(size)
        }
    }
}

data class ResponseSideBarPlan(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: ArrayList<SideBarPlan>
)

data class SideBarPlan(
    @SerializedName("count") val count: Int,
    @SerializedName("plans") val plans: ArrayList<PlanInfo>
)

data class PlanInfo(
    @SerializedName("planId") val planId: Int,
    @SerializedName("planName") val planName: String,
    @SerializedName("date") val date: String,
    @SerializedName("time") val time: String,
    @SerializedName("participant") val participant: Boolean,
    @SerializedName("dday") val dday: String?,
)

data class ResponseGetGroupDetail(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: ResultGetGroupDetail
)

data class ResultGetGroupDetail(
    @SerializedName("teamId") val groupId: Int,
    @SerializedName("teamName") val groupName: String,
    @SerializedName("teamImgUrl") val groupImgUrl: String,
    @SerializedName("teamMemberCount") val groupMemberCount: Int,
    @SerializedName("isNew") val isNew: Boolean?,
    @SerializedName("bookmark") val bookmark: Boolean
)