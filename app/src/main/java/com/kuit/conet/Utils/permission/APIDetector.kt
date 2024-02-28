package com.kuit.conet.Utils.permission

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.kuit.conet.Utils.TAG

class APIDetector(
    private val context: Context,
    private val highCode: () -> Unit,
    private val lowCode: () -> Unit,
) {

    private var threadAPI: Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {    // getPackageInfo의 변경사항으로 인한 코드
            context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.PackageInfoFlags.of(0.toLong())
            ).applicationInfo.targetSdkVersion
        } else {
            @Suppress("DEPRECATION")
            context.packageManager.getPackageInfo(
                context.packageName,
                0
            ).applicationInfo.targetSdkVersion
        }

    fun setThreadApi(apiVersion: Int) {
        Log.d(TAG, "APIDetector - setThreadApi() called")
        threadAPI = apiVersion
    }

    fun executeCode(): Unit {
        Log.d(TAG, "APIDetector - executeCode() called")
        return if (Build.VERSION.SDK_INT >= threadAPI) {
            highCode()
        } else {
            lowCode()
        }
    }

}