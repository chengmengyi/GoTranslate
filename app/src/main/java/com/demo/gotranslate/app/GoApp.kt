package com.demo.gotranslate.app

import android.app.Application
import com.demo.gotranslate.config.GoFirebase
import com.demo.gotranslate.manager.LanguageManager
import com.google.android.gms.ads.MobileAds
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.tencent.mmkv.MMKV

lateinit var goApp: GoApp
class GoApp:Application() {
    override fun onCreate() {
        super.onCreate()
        goApp=this
        Firebase.initialize(this)
        MobileAds.initialize(this)
        MMKV.initialize(this)
        GoFirebase.readFirebase()
        LanguageManager.initAllLanguageList()
        ActivityCallback.register(this)
    }
}