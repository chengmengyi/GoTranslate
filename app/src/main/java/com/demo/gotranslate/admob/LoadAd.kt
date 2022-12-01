package com.demo.gotranslate.admob

import com.demo.gotranslate.app.goApp
import com.demo.gotranslate.app.logGo
import com.demo.gotranslate.bean.ConfigAdBean
import com.demo.gotranslate.bean.ResultAdBean
import com.demo.gotranslate.config.GoFirebase
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAdOptions
import org.json.JSONObject

abstract class LoadAd {
    fun startLoad(type:String,configAdBean: ConfigAdBean,result:(resultAdBean:ResultAdBean)->Unit){
        logGo("load $type ad , $configAdBean")
        when(configAdBean.go_type){
            "kaiping"->loadKaiPing(configAdBean, result)
            "chaping"->loadChaPing(configAdBean, result)
            "yuansheng"->loadYuanSheng(configAdBean, result)
        }
    }
    
    private fun loadKaiPing(configAdBean: ConfigAdBean,result:(resultAdBean:ResultAdBean)->Unit){
        AppOpenAd.load(
            goApp,
            configAdBean.go_id,
            AdRequest.Builder().build(),
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAd.AppOpenAdLoadCallback(){
                override fun onAdLoaded(p0: AppOpenAd) {
                    result.invoke(ResultAdBean(ad = p0, time = System.currentTimeMillis()))
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    result.invoke(ResultAdBean(fail = p0.message))
                }
            }
        )
    }

    private fun loadChaPing(configAdBean: ConfigAdBean,result:(resultAdBean:ResultAdBean)->Unit){
        InterstitialAd.load(
            goApp,
            configAdBean.go_id,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback(){
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    result.invoke(ResultAdBean(fail = p0.message))
                }

                override fun onAdLoaded(p0: InterstitialAd) {
                    result.invoke(ResultAdBean(ad = p0, time = System.currentTimeMillis()))
                }
            }
        )
    }

    private fun loadYuanSheng(configAdBean: ConfigAdBean,result:(resultAdBean:ResultAdBean)->Unit){
        AdLoader.Builder(
            goApp,
            configAdBean.go_id,
        ).forNativeAd {
            result.invoke(ResultAdBean(ad = it, time = System.currentTimeMillis()))
        }
            .withAdListener(object : AdListener(){
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    result.invoke(ResultAdBean(fail = p0.message))
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    MaxNumManager.clickNumAdd()
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .setAdChoicesPlacement(
                        NativeAdOptions.ADCHOICES_BOTTOM_LEFT
                    )
                    .build()
            )
            .build()
            .loadAd(AdRequest.Builder().build())
    }
    
    protected fun parseAdList(key:String):List<ConfigAdBean>{
        val list= arrayListOf<ConfigAdBean>()
        try {
            val jsonArray = JSONObject(GoFirebase.getAdJson()).getJSONArray(key)
            for (index in 0 until jsonArray.length()){
                val jsonObject = jsonArray.getJSONObject(index)
                list.add(
                    ConfigAdBean(
                        jsonObject.optString("go_source"),
                        jsonObject.optString("go_id"),
                        jsonObject.optString("go_type"),
                        jsonObject.optInt("go_sort"),
                    )
                )
            }
        }catch (e:Exception){
        }
        return list.filter { it.go_source == "admob" }.sortedByDescending { it.go_sort }
    }
}