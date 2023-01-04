package com.demo.gotranslate.vpn


import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.ServiceUtils
import com.demo.gotranslate.app.logGo
import com.demo.gotranslate.base.BaseUI
import com.demo.gotranslate.bean.VpnBean
import com.demo.gotranslate.config.GoFirebase
import com.demo.gotranslate.interfaces.IConnectCallback
import com.github.shadowsocks.Core
import com.github.shadowsocks.aidl.IShadowsocksService
import com.github.shadowsocks.aidl.ShadowsocksConnection
import com.github.shadowsocks.bg.BaseService
import com.github.shadowsocks.preference.DataStore
import de.blinkt.openvpn.api.ExternalOpenVPNService
import de.blinkt.openvpn.api.IOpenVPNAPIService
import de.blinkt.openvpn.api.IOpenVPNStatusCallback
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

object ConnectVpnManager: ShadowsocksConnection.Callback {
    private var baseUI:BaseUI?=null
    private var state=BaseService.State.Stopped
    var currentVpn:VpnBean?=null
    var lastVpn:VpnBean?=null
    private var mService: IOpenVPNAPIService? = null
    private var iConnectCallback:IConnectCallback?=null
    private val sc= ShadowsocksConnection(true)

    fun onInit(baseUI: BaseUI, iConnectCallback: IConnectCallback){
        this.baseUI=baseUI
        this.iConnectCallback=iConnectCallback
        sc.connect(baseUI,this)
        ServiceUtils.bindService(
            Intent(baseUI, ExternalOpenVPNService::class.java),
            mConnection,
            AppCompatActivity.BIND_AUTO_CREATE
        )
    }


    //监听链接状态注册方法
    private val mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            className: ComponentName,
            service: IBinder,
        ) {
            mService = IOpenVPNAPIService.Stub.asInterface(service)
            runCatching {
                mService?.registerStatusCallback(mCallback)
            }
        }

        override fun onServiceDisconnected(className: ComponentName) {
            mService = null
        }
    }

    //接状态方法监听
    private val mCallback = object : IOpenVPNStatusCallback.Stub() {
        override fun newStatus(uuid: String?, s: String?, message: String?, level: String?) {
            //这里就可以处理open vpn的连接逻辑
            // state // NOPROCESS 未连接  CONNECTED 已连接  RECONNECTING 尝试重新链接  EXITING 这个应该是退出的意思
//            logGo("==kkkk==${s}=")
            when(s){
                "NOPROCESS"->{
                    state=BaseService.State.Stopped
                    ConnectTimeManager.end()
                    iConnectCallback?.disconnectSuccess()
                }
                "CONNECTED"->{
                    state=BaseService.State.Connected
                    lastVpn= currentVpn
                    ConnectTimeManager.start()
                }
            }
        }

    }

    //连接方法
    private fun connectOpenVon(server: IOpenVPNAPIService) {
        val data = currentVpn
        runCatching {
            //这个是春哥给你的配置文件，然后你放到项目的assets目录下即可，填上对应的名字
            val conf = baseUI?.assets?.open("fast_gotranslate.ovpn")
            val br = BufferedReader(InputStreamReader(conf))
            val config = StringBuilder()
            var line: String?
            //以下操作则是读取配置文件的内容
            while (true) {
                line = br.readLine()
                if (line == null) break
                //配置文件的内容是死的这里则需要替换成对应的内容例如ip 端口号
                //这里就写一个示例
                if (line.contains("34.213.33.79", true)) {
                    line = line.replace("34.213.33.79", data?.go_s_ip?:"")
                }
//                if (line.contains("443", true)) {
//                    line = line.replace("443", data?.go_s_port?.toString()?:"")
//                }
                config.append(line).append("\n")
            }
            br.close()
            conf?.close()
            server.startVPN(config.toString())
        }.onFailure {
//            这里可以打印链接抛出的其他错误，如果以上步骤正确基本上不会出现打印
        }
    }

    private fun disconnectOpenVpn(){
        mService?.disconnect()
    }


    fun initVpnBean(){
        currentVpn=VpnManager.smartVpnBean
        lastVpn=VpnManager.smartVpnBean
    }

    fun connect(){
        state= BaseService.State.Connecting
        when(getConnectType()){
            2->{
                if (null==mService){

                }else{
                    connectOpenVon(mService!!)
                }
            }
            3->{
                GlobalScope.launch {
                    DataStore.profileId=currentVpn?.getVpnId()?:0L
                    Core.startService()
                }
                ConnectTimeManager.reset()
            }
        }
    }

    fun disconnect(){
        state= BaseService.State.Stopping
        when(getConnectType()){
            2->{
                disconnectOpenVpn()
            }
            3->{
                GlobalScope.launch {
                    Core.stopService()
                }
            }
        }
    }

    fun isConnected()=state==BaseService.State.Connected

    fun isStopped()=state==BaseService.State.Stopped

    override fun stateChanged(state: BaseService.State, profileName: String?, msg: String?) {
        if (getConnectType()==3){
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
    }

    override fun onServiceConnected(service: IShadowsocksService) {
        if (getConnectType()==3){
            val state = BaseService.State.values()[service.state]
            this.state=state
            if (isConnected()){
                ConnectTimeManager.start()
                lastVpn= currentVpn
                iConnectCallback?.connectSuccess()
            }
        }
    }

    private fun getConnectType():Int{
        return if(GoFirebase.connectType==1){
            if (GoFirebase.auto_go=="1") 2 else 3
        }else{
            GoFirebase.connectType
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