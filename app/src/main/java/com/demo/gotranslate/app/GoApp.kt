package com.demo.gotranslate.app

import android.app.Application
import com.demo.gotranslate.manager.LanguageManager
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.tencent.mmkv.MMKV

class GoApp:Application() {
    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(this)
        MMKV.initialize(this)
        LanguageManager.initAllLanguageList()
    }
}