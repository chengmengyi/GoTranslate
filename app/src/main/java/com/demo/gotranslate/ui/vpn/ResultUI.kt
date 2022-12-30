package com.demo.gotranslate.ui.vpn

import com.demo.gotranslate.R
import com.demo.gotranslate.admob.LoadAdImpl
import com.demo.gotranslate.admob.ShowNativeAd
import com.demo.gotranslate.admob.ShowOpenAd
import com.demo.gotranslate.app.checkShowBackAd
import com.demo.gotranslate.app.getVpnLogo
import com.demo.gotranslate.base.BaseUI
import com.demo.gotranslate.config.GoConfig
import com.demo.gotranslate.interfaces.IConnectTimeCallback
import com.demo.gotranslate.util.ReloadNativeAdManager
import com.demo.gotranslate.vpn.ConnectTimeManager
import com.demo.gotranslate.vpn.ConnectVpnManager
import kotlinx.android.synthetic.main.activity_result.*

class ResultUI:BaseUI(R.layout.activity_result), IConnectTimeCallback {
    private var connect=false
    private val showNativeAd by lazy { ShowNativeAd(this,GoConfig.GO_VPN_RESULT) }
    private val showBackAd by lazy { ShowOpenAd(this,GoConfig.GO_VPN_RESULT_BACK){finish()} }

    override fun view() {
        immersionBar.statusBarView(top).init()
        connect=intent.getBooleanExtra("connect",false)
        if (!connect){
            iv_title.setImageResource(R.drawable.result2)
            iv_result.setImageResource(R.drawable.result4)
        }
        tv_vpn_name.isSelected=connect
        tv_vpn_time.isSelected=connect
        if (connect){
            ConnectTimeManager.setConnectTimeCallback(this)
        }else{
            tv_vpn_time.text=ConnectTimeManager.getTotalTime()
        }

        val lastVpn = ConnectVpnManager.lastVpn
        if (lastVpn?.isSmart == false){
            tv_vpn_name.text=lastVpn.go_s_coun
            iv_vpn_logo.setImageResource(getVpnLogo(lastVpn.go_s_coun))
        }
        iv_back.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        if (ReloadNativeAdManager.canReload(GoConfig.GO_VPN_RESULT)){
            showNativeAd.showAd()
        }
    }

    override fun onBackPressed() {
        val result = LoadAdImpl.getResult(GoConfig.GO_VPN_RESULT_BACK)
        if(null!=result&& checkShowBackAd()){
            showBackAd.showOpenAd {
                if (it){
                    finish()
                }
            }
            return
        }
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (connect){
            ConnectTimeManager.setConnectTimeCallback(null)
        }
        showNativeAd.stopShow()
        ReloadNativeAdManager.setBool(GoConfig.GO_VPN_RESULT,true)
    }

    override fun connectTime(time: String) {
        tv_vpn_time.text=time
    }
}