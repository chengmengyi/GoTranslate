package com.demo.gotranslate.vpn

import com.demo.gotranslate.base.BaseUI
import com.demo.gotranslate.bean.VpnBean
import com.demo.gotranslate.interfaces.IConnectCallback
import com.github.shadowsocks.Core
import com.github.shadowsocks.aidl.IShadowsocksService
import com.github.shadowsocks.aidl.ShadowsocksConnection
import com.github.shadowsocks.bg.BaseService
import com.github.shadowsocks.preference.DataStore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object ConnectVpnManager: ShadowsocksConnection.Callback {
    private var baseUI:BaseUI?=null
    private var state=BaseService.State.Stopped
    var currentVpn:VpnBean?=null
    var lastVpn:VpnBean?=null
    private var iConnectCallback:IConnectCallback?=null
    private val sc= ShadowsocksConnection(true)

    fun onInit(baseUI: BaseUI, iConnectCallback: IConnectCallback){
        this.baseUI=baseUI
        this.iConnectCallback=iConnectCallback
        sc.connect(baseUI,this)
    }

    fun initVpnBean(){
        currentVpn=VpnManager.smartVpnBean
        lastVpn=VpnManager.smartVpnBean
    }

    fun connect(){
        state= BaseService.State.Connecting
        GlobalScope.launch {
            DataStore.profileId=currentVpn?.getVpnId()?:0L
            Core.startService()
        }
        ConnectTimeManager.reset()
    }

    fun disconnect(){
        state= BaseService.State.Stopping
        GlobalScope.launch {
            Core.stopService()
        }
    }

    fun isConnected()=state==BaseService.State.Connected

    fun isStopped()=state==BaseService.State.Stopped

    override fun stateChanged(state: BaseService.State, profileName: String?, msg: String?) {
        this.state=state
        if (isConnected()){
            lastVpn= currentVpn
            ConnectTimeManager.start()
        }
        if (isStopped()){
            ConnectTimeManager.end()
            iConnectCallback?.disconnectSuccess()
        }
    }

    override fun onServiceConnected(service: IShadowsocksService) {
        val state = BaseService.State.values()[service.state]
        this.state=state
        if (isConnected()){
            ConnectTimeManager.start()
            lastVpn= currentVpn
            iConnectCallback?.connectSuccess()
        }
    }

    override fun onBinderDied() {
        baseUI?.let {
            sc.disconnect(it)
        }
    }

    fun onDestroy(){
        onBinderDied()
        baseUI=null
        iConnectCallback=null
    }
}