package com.demo.gotranslate.app

import android.app.ActivityManager
import android.app.Application
import com.demo.gotranslate.config.GoFirebase
import com.demo.gotranslate.manager.LanguageManager
import com.demo.gotranslate.ui.HomeUI
import com.github.shadowsocks.Core
import com.tencent.mmkv.MMKV

lateinit var goApp: GoApp
class GoApp:Application() {
    override fun onCreate() {
        super.onCreate()
        goApp=this
        Core.init(this,HomeUI::class)
        if (!packageName.equals(processName(this))){
            return
        }
        MMKV.initialize(this)
        GoFirebase.readFirebase()
        LanguageManager.initAllLanguageList()
        ActivityCallback.register(this)
    }

    private fun processName(applicationContext: Application): String {
        val pid = android.os.Process.myPid()
        var processName = ""
        val manager = applicationContext.getSystemService(Application.ACTIVITY_SERVICE) as ActivityManager
        for (process in manager.runningAppProcesses) {
            if (process.pid === pid) {
                processName = process.processName
            }
        }
        return processName
    }
}