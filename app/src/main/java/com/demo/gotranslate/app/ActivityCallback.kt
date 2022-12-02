package com.demo.gotranslate.app

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.demo.gotranslate.admob.RefreshAdManager
import com.demo.gotranslate.ui.HomeUI
import com.demo.gotranslate.ui.MainUI
import com.google.android.gms.ads.AdActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object ActivityCallback {
    var goFront=true
    private var jumpToMain=false
    private var job: Job?=null
    var choosePic=false


    fun register(application: Application){
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks{
            private var pages=0
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

            override fun onActivityStarted(activity: Activity) {
                pages++
                job?.cancel()
                job=null
                if (pages==1){
                    start(activity)
                }
            }

            override fun onActivityResumed(activity: Activity) {}

            override fun onActivityPaused(activity: Activity) {}

            override fun onActivityStopped(activity: Activity) {
                pages--
                if (pages<=0){
                    stop()
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityDestroyed(activity: Activity) {}
        })
    }

    private fun start(activity: Activity){
        goFront=true
        if (jumpToMain&&!choosePic){
            if (ActivityUtils.isActivityExistsInStack(HomeUI::class.java)){
                activity.startActivity(Intent(activity, MainUI::class.java))
            }
        }
        jumpToMain=false
    }

    private fun stop(){
        goFront=false
        RefreshAdManager.reset()
        job= GlobalScope.launch {
            delay(3000L)
            jumpToMain=true
            ActivityUtils.finishActivity(MainUI::class.java)
            ActivityUtils.finishActivity(AdActivity::class.java)
        }
    }
}