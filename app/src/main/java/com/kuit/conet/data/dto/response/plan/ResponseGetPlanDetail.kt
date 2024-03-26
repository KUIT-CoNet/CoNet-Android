package com.kuit.conet.data.dto.response.plan

import com.kuit.conet.domain.entity.plan.PlanDetail
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetPlanDetail(
    @SerialName("result")
    val result: ResultGetPlanDetail,
) {
    @Serializable
    data class ResultGetPlanDetail(
        @SerialName("planId")
        val planId: Long,
        @SerialName("planName")
        val planName: String,
        @SerialName("date")
        val date: String,
        @SerialName("time")
        val time: String,
        @SerialName("memberCount")
        val memberCount: Int,
        @SerialName("members")
        val members: List<Member>,
    ) {
        @Serializable
        data class Member(
            @SerialName("id")
            val id: Long,
            @SerialName("name")
            val name: String,
            @SerialName("image")
            val image: String,
        ) {
            fun asMember(): com.kuit.conet.domain.entity.member.Member {
                return com.kuit.conet.domain.entity.member.Member(
                    id = id,
                    name = name,
                    imageUrl = image,
                )
            }

        }

        fun asPlanDetail(): PlanDetail {
            return PlanDetail(
                id = planId,
                planName = planName,
                date = date,
                time = time,
                memberCount = memberCount,
                members = members.map { it.asMember() }
            )
        }
    }
}


/*@Serializable
data class ResultGetPlanDetail(
    @SerialName("planId") val planId: Int,
    @SerialName("planName") val planName: String,
    @SerialName("date") val date: String,
    @SerialName("time") val time: String,
    @SerialName("memberCount") val memberCount: Int,
    @SerialName("members") val members: ArrayList<Members>
) : Parcelable {

    @Serializable
    data class Members(
        @SerialName("id")
        val id: Long,
        @SerialName("name")
        val name: String,
        @SerialName("image")
        val image: String,
    ) : Parcelable {

        fun asMembers(): com.kuit.conet.Network.Members {
            return com.kuit.conet.Network.Members(
                id = id.toInt(),
                name = name,
                image = image,
            )
        }

        constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString() ?: "",
            parcel.readString() ?: "",
        )

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeLong(id)
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

}*/
