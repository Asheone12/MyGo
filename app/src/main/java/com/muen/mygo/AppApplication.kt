package com.muen.mygo

import android.app.Application
import com.alibaba.android.arouter.launcher.ARouter
import com.muen.mygo.util.GlideUtils
import com.muen.mygo.util.ToastUtils
import com.tencent.mmkv.MMKV
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AppApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        //初始化MMKV，返回缓存地址
        MMKV.initialize(this)
        //初始化ARouter
        ARouter.init(this)
        //初始化ToastUtils
        ToastUtils.init(this)
        //初始化GlideUtils
        GlideUtils.init(this)
    }
}