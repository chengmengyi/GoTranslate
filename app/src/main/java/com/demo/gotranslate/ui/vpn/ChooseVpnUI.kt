package com.demo.gotranslate.ui.vpn

import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import com.demo.gotranslate.R
import com.demo.gotranslate.adapter.VpnListAdapter
import com.demo.gotranslate.admob.LoadAdImpl
import com.demo.gotranslate.admob.ShowNativeAd
import com.demo.gotranslate.admob.ShowOpenAd
import com.demo.gotranslate.app.checkShowBackAd
import com.demo.gotranslate.app.logGo
import com.demo.gotranslate.base.BaseUI
import com.demo.gotranslate.bean.VpnBean
import com.demo.gotranslate.config.GoConfig
import com.demo.gotranslate.config.GoFirebase
import com.demo.gotranslate.ui.dialog.SureCancelDialog
import com.demo.gotranslate.util.ReloadNativeAdManager
import com.demo.gotranslate.vpn.ConnectVpnManager
import com.demo.gotranslate.vpn.VpnManager
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import kotlinx.android.synthetic.main.activity_choose_vpn.*

class ChooseVpnUI:BaseUI(R.layout.activity_choose_vpn), OnRefreshListener {
    private val vpnList= arrayListOf<VpnBean>()
    private val vpnAdapter by lazy { VpnListAdapter(this@ChooseVpnUI,vpnList){ click(it) } }
    private val showBackAd by lazy { ShowOpenAd(this,GoConfig.GO_VPN_BACK){ finish() } }
    private val showNativeAd by lazy { ShowNativeAd(this,GoConfig.GO_VPN_LIST) }

    override fun view() {
        immersionBar.statusBarView(top).init()

        rv_list.apply {
            layoutManager=GridLayoutManager(this@ChooseVpnUI,3)
            adapter=vpnAdapter
        }
        if(VpnManager.otherVpnList.isEmpty()){
            VpnManager.refreshOtherVpnList()
        }
        updateList()
        refresh_layout.setOnRefreshListener(this)
        LoadAdImpl.loadAd(GoConfig.GO_VPN_BACK)
        iv_back.setOnClickListener { onBackPressed() }
    }

    private fun click(vpnBean: VpnBean){
        val connected = ConnectVpnManager.isConnected()
        val currentVpn = ConnectVpnManager.currentVpn
        if (connected&&currentVpn?.go_s_ip==vpnBean.go_s_ip){
            SureCancelDialog("do you confirm to reconnect?"){
                if (it){
                    chooseBack("disconnect",vpnBean)
                }
            }.show(supportFragmentManager,"SureCancelDialog")
        }else{
            if (connected){
                chooseBack("",vpnBean)
            }else{
                chooseBack("connect",vpnBean)
            }
        }
    }

    private fun chooseBack(action:String,vpnBean: VpnBean){
        ConnectVpnManager.currentVpn=vpnBean
        setResult(1228, Intent().apply {
            putExtra("action",action)
        })
        finish()
    }

    private fun updateList(){
        vpnList.clear()
        vpnList.add(VpnManager.smartVpnBean)
        vpnList.addAll(VpnManager.otherVpnList)
        vpnAdapter.notifyDataSetChanged()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        if(ConnectVpnManager.isConnected()){
            SureCancelDialog("do you confirm to reconnect?"){
                if(it){
                    refresh()
                }else{
                    refresh_layout.finishRefresh(false)
                }
            }.show(supportFragmentManager,"SureCancelDialog")
        }else{
            refresh()
        }
    }

    private fun refresh(){
        GoFirebase.reloadVpnConfig {
            VpnManager.refreshOtherVpnList()
            updateList()
            refresh_layout.finishRefresh(true)
        }
    }

    override fun onResume() {
        super.onResume()
        if(ReloadNativeAdManager.canReload(GoConfig.GO_VPN_LIST)&& checkShowBackAd()){
            showNativeAd.showAd()
        }
    }

    override fun onBackPressed() {
        val result = LoadAdImpl.getResult(GoConfig.GO_VPN_BACK)
        if(null!=result){
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
        ReloadNativeAdManager.setBool(GoConfig.GO_VPN_LIST,true)
        showNativeAd.stopShow()
    }
}