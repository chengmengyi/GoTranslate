package com.demo.gotranslate.app

import android.app.Application
import com.demo.gotranslate.manager.LanguageManager
import com.tencent.mmkv.MMKV

class GoApp:Application() {
    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        LanguageManager.initAllLanguageList()
    }
}