package com.demo.gotranslate.admob

object RefreshAdManager {
    val refreshNativeAd = hashMapOf<String,Boolean>()


    fun canRefresh(key:String)=refreshNativeAd[key]?:true

    fun reset(key: String){
        refreshNativeAd[key]=true
    }

    fun reset(){
        refreshNativeAd.keys.forEach {
            refreshNativeAd[it]=true
        }
    }
}