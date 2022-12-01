package com.demo.gotranslate.app

import android.app.Application
import com.demo.gotranslate.config.GoFirebase
import com.demo.gotranslate.manager.LanguageManager
import com.tencent.mmkv.MMKV

lateinit var goApp: GoApp
class GoApp:Application() {
    override fun onCreate() {
        super.onCreate()
        goApp=this
        MMKV.initialize(this)
        GoFirebase.readFirebase()
        LanguageManager.initAllLanguageList()
        ActivityCallback.register(this)
    }
}