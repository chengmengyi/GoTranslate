package com.demo.gotranslate.admob

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.utils.widget.ImageFilterView
import com.blankj.utilcode.util.SizeUtils
import com.demo.gotranslate.R
import com.demo.gotranslate.app.showView
import com.demo.gotranslate.base.BaseUI
import com.demo.gotranslate.config.GoConfig
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import kotlinx.coroutines.*

class ShowNativeAd(
    private val baseUI: BaseUI,
    private val key:String,
) {
    private var showJob:Job?=null
    private var lastAd:NativeAd?=null
    
    fun showAd(){
        LoadAdImpl.loadAd(key)
        stopShow()
        showJob= GlobalScope.launch(Dispatchers.Main) {
            delay(300L)
            if (!baseUI.resume){
                return@launch
            }
            while (true) {
                if (!isActive) {
                    break
                }

                val result = LoadAdImpl.getResult(key)
                if(baseUI.resume && null!=result && result is NativeAd){
                    cancel()
                    lastAd?.destroy()
                    lastAd=result
                    show(result)
                }

                delay(1000L)
            }
        }
    }
    
    private fun show(result: NativeAd) {
        val viewNative = baseUI.findViewById<NativeAdView>(R.id.native_ad)
        viewNative.iconView=baseUI.findViewById(R.id.native_logo)
        (viewNative.iconView as ImageFilterView).setImageDrawable(result.icon?.drawable)

        viewNative.callToActionView=baseUI.findViewById(R.id.native_action)
        (viewNative.callToActionView as AppCompatTextView).text= result.callToAction

        if(key==GoConfig.GO_HOME){
            viewNative.mediaView=baseUI.findViewById(R.id.native_cover)
            result.mediaContent?.let {
                viewNative.mediaView?.apply {
                    setMediaContent(it)
                    setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                    outlineProvider = object : ViewOutlineProvider() {
                        override fun getOutline(view: View?, outline: Outline?) {
                            if (view == null || outline == null) return
                            outline.setRoundRect(
                                0,
                                0,
                                view.width,
                                view.height,
                                SizeUtils.dp2px(6F).toFloat()
                            )
                            view.clipToOutline = true
                        }
                    }
                }
            }
        }

        viewNative.bodyView=baseUI.findViewById(R.id.native_desc)
        (viewNative.bodyView as AppCompatTextView).text=result.body

        viewNative.headlineView=baseUI.findViewById(R.id.native_title)
        (viewNative.headlineView as AppCompatTextView).text=result.headline

        viewNative.setNativeAd(result)
        viewNative.showView(true)
        baseUI.findViewById<AppCompatImageView>(R.id.ad_cover).showView(false)

        MaxNumManager.showNumAdd()
        LoadAdImpl.removeResult(key)
        LoadAdImpl.loadAd(key)

        RefreshAdManager.refreshNativeAd[key]=false
    }
    
    fun stopShow(){
        showJob?.cancel()
        showJob=null
    }
}