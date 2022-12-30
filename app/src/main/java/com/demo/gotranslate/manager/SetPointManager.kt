package com.demo.gotranslate.manager

import android.os.Bundle
import com.demo.gotranslate.app.logGo
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

object SetPointManager {
//    private val remoteConfig= Firebase.analytics

    fun setUser(plan:String){
        logGo("==point==$plan")
//        remoteConfig.setUserProperty("go_user",plan)
    }

    fun point(name:String){
        logGo("==point==$name")
//        remoteConfig.logEvent(name, Bundle())
    }
}