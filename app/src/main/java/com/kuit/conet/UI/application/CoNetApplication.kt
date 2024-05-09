package com.kuit.conet.UI.application

import android.app.Application
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import com.kuit.conet.BuildConfig

class CoNetApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("LifeCycle", "CoNetApplication - onCreate() called")

        KakaoSdk.init(
            context = this,
            appKey = BuildConfig.KAKAO_APP_KEY,
        )
    }
}