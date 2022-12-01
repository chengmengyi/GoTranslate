package com.demo.gotranslate.config

import com.demo.gotranslate.admob.MaxNumManager
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.tencent.mmkv.MMKV
import java.lang.Exception

object GoFirebase {

    fun readFirebase(){
        MaxNumManager.readLocalNum()

//        val remoteConfig = Firebase.remoteConfig
//        remoteConfig.fetchAndActivate().addOnCompleteListener {
//            if (it.isSuccessful){
//                parseAdJson(remoteConfig.getString("go_ad"))
//            }
//        }
    }

    private fun parseAdJson(string: String){
        try {
            MaxNumManager.readConfigMax(string)
            MMKV.defaultMMKV().encode("go_ad",string)
        }catch (e:Exception){

        }
    }

    fun getAdJson() = MMKV.defaultMMKV().decodeString("go_ad")?:GoConfig.localAdConfig
}