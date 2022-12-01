package com.demo.gotranslate.admob

import com.demo.gotranslate.app.logGo
import com.demo.gotranslate.base.BaseUI
import com.demo.gotranslate.config.GoConfig
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ShowOpenAd(
    private val baseUI: BaseUI,
    private val key:String,
    private val close:()->Unit
) {
    fun showOpenAd(next:(next:Boolean)->Unit){
        val result = LoadAdImpl.getResult(key)
        if(result==null&&MaxNumManager.checkLimit()){
            next.invoke(true)
        }else{
            if(null!=result){
                if(LoadAdImpl.fullAdShowing||!baseUI.resume){
                    next.invoke(false)
                }else{
                    next.invoke(false)
                    logGo("show $key ad")
                    if(result is AppOpenAd){
                        result.fullScreenContentCallback=callback
                        result.show(baseUI)
                    }
                    if(result is InterstitialAd){
                        result.fullScreenContentCallback=callback
                        result.show(baseUI)
                    }
                }
            }
        }
    }

    private val callback=object : FullScreenContentCallback() {
        override fun onAdDismissedFullScreenContent() {
            super.onAdDismissedFullScreenContent()
            LoadAdImpl.fullAdShowing=false
            closeAd()
        }

        override fun onAdShowedFullScreenContent() {
            super.onAdShowedFullScreenContent()
            LoadAdImpl.fullAdShowing=true
            MaxNumManager.showNumAdd()
            LoadAdImpl.removeResult(key)
        }

        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
            super.onAdFailedToShowFullScreenContent(p0)
            LoadAdImpl.fullAdShowing=false
            LoadAdImpl.removeResult(key)
            closeAd()
        }

        override fun onAdClicked() {
            super.onAdClicked()
            MaxNumManager.clickNumAdd()
        }

        private fun closeAd(){
            if (key!=GoConfig.GO_OPEN){
                LoadAdImpl.loadAd(key)
            }
            GlobalScope.launch(Dispatchers.Main) {
                delay(200L)
                if (baseUI.resume){
                    close.invoke()
                }
            }
        }
    }
}