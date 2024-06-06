package com.kuit.conet.UI.application

import android.app.Application
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import com.kuit.conet.BuildConfig
import com.kuit.conet.domain.repository.datastore.DataStoreModule

class CoNetApplication : Application() {

    private lateinit var dataStore: DataStoreModule

    override fun onCreate() {
        super.onCreate()
        Log.d("LifeCycle", "CoNetApplication - onCreate() called")
        instance = this
        dataStore = DataStoreModule(this)

        KakaoSdk.init(
            context = this,
            appKey = BuildConfig.KAKAO_APP_KEY,
        )
    }

    fun getDataStore(): DataStoreModule {
        return dataStore
    }

    companion object {
        private lateinit var instance: CoNetApplication
        fun getInstance(): CoNetApplication {
            return instance
        }
    }
}