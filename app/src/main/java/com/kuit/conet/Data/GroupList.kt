package com.kuit.conet.Data

import com.kuit.conet.Network.ResultGetGroup

object GroupList {
    lateinit var groupList : ArrayList<ResultGetGroup>
    lateinit var favoriteGroupList : ArrayList<ResultGetGroup>

    fun updateFavoriteGroupList(){
        favoriteGroupList = arrayListOf()
        for(i in 0 until groupList.count()){
            if(groupList[i].favoriteTag){
                favoriteGroupList.add(groupList[i])
            }
        }
    }
}

object Group{
    var groupId: Int = 1
}