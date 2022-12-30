package com.demo.gotranslate.util

object ReloadNativeAdManager {
    private val map= hashMapOf<String,Boolean>()

    fun canReload(key:String)=map[key]?:true

    fun setBool(key: String,bool:Boolean){
        map[key]=bool
    }

    fun reset(){
        map.keys.forEach {
            map[it]=true
        }
    }
}