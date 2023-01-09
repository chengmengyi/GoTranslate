package com.demo.gotranslate.ui.vpn

import android.content.Intent
import android.net.VpnService
import com.blankj.utilcode.util.ActivityUtils
import com.demo.gotranslate.R
import com.demo.gotranslate.admob.LoadAdImpl
import com.demo.gotranslate.admob.ShowNativeAd
import com.demo.gotranslate.admob.ShowOpenAd
import com.demo.gotranslate.app.*
import com.demo.gotranslate.base.BaseUI
import com.demo.gotranslate.config.GoConfig
import com.demo.gotranslate.config.GoFirebase
import com.demo.gotranslate.interfaces.IConnectCallback
import com.demo.gotranslate.manager.SetPointManager
import com.demo.gotranslate.ui.HomeUI
import com.demo.gotranslate.ui.dialog.SureCancelDialog
import com.demo.gotranslate.util.ReloadNativeAdManager
import com.demo.gotranslate.vpn.ConnectVpnManager
import com.demo.gotranslate.vpn.VpnManager
import com.github.shadowsocks.utils.StartService
import kotlinx.android.synthetic.main.activity_connect_vpn.*
import kotlinx.android.synthetic.main.activity_connect_vpn.top
import kotlinx.coroutines.*

class ConnectVpnUI:BaseUI(R.layout.activity_connect_vpn), IConnectCallback {
    private var click=true
    private var permission=false
    private var connect=true
    private var autoConnect=false
    private var checkConnectJob: Job?=null
    private val registerResult=registerForActivityResult(StartService()) {
        if (!it && permission) {
            SetPointManager.point("go_vpn_get")
            permission = false
            startConnectVpn()
        } else {
            click=true
            SetPointManager.point("go_vpn_fail")
            showToast("Connected fail")
        }
    }

    private val showConnectAd by lazy { ShowOpenAd(this, GoConfig.GO_VPN_CONN){ jumpToResult() } }
    private val showNativeAd by lazy { ShowNativeAd(this,GoConfig.GO_VPN_HOME) }

    override fun view() {
        immersionBar.statusBarView(top).init()
        setClickListener()
        if (ConnectVpnManager.isStopped()){
            VpnManager.createSmartVpn()
            ConnectVpnManager.initVpnBean()
        }
        updateVpnInfo()
        ConnectVpnManager.onInit(this,this)
        if (ConnectVpnManager.isConnected()){
            updateConnectedUI()
        }

        if(GoFirebase.irUser){
            SureCancelDialog("The current country does not support vpn services due to policy reasons. You can continue to use the translate function"){
                finish()
            }
            return
        }

        autoConnect = intent.getBooleanExtra("auto", false)
        if (autoConnect){
            iv_connect_btn.performClick()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        autoConnect = intent?.getBooleanExtra("auto", false)?:false
        if (autoConnect){
            iv_connect_btn.performClick()
        }
    }

    private fun setClickListener(){
        llc_vpn_info.setOnClickListener {
            if (click){
                startActivityForResult(Intent(this,ChooseVpnUI::class.java),1228)
            }
        }

        iv_connect_btn.setOnClickListener {
//            updateConnectingUI()
            clickConnectBtn()
        }
        iv_connect_idle.setOnClickListener { iv_connect_btn.performClick() }

        iv_back.setOnClickListener { onBackPressed() }
        iv_connect_type_auto.setOnClickListener {
            updateConnectType(1)
        }
        iv_connect_type_open.setOnClickListener {
            updateConnectType(2)
        }
        iv_connect_type_ss.setOnClickListener {
            updateConnectType(3)
        }
        updateConnectType(GoFirebase.connectType,true)
    }

    private fun clickConnectBtn(){
        if(!click||GoFirebase.irUser) return
        LoadAdImpl.loadAd(GoConfig.GO_VPN_RESULT)
        LoadAdImpl.loadAd(GoConfig.GO_VPN_CONN)
        click=false
        if (!autoConnect){
            SetPointManager.point("cute_vpnfc_click")
        }
        if (ConnectVpnManager.isConnected()){
            updateStoppingUI()
            checkConnectResult(false)
        }else{
            updateVpnInfo()
            if (netStatus()==1){
                SetPointManager.point("go_vpn_fail")
                SureCancelDialog("You are not currently connected to the network"){}.show(supportFragmentManager,"SureCancelDialog")
                click=true
                return
            }
            if (VpnService.prepare(this) != null) {
                permission = true
                registerResult.launch(null)
                return
            }
            startConnectVpn()
        }
    }

    private fun startConnectVpn(){
        SetPointManager.point("go_conn_link")
        updateConnectingUI()
        checkConnectResult(true)
    }

    private fun checkConnectResult(connect:Boolean){
        this.connect=connect
        checkConnectJob= GlobalScope.launch(Dispatchers.Main) {
            var time = 0
            while (true) {
                if (!isActive) {
                    break
                }
                delay(1000)
                time++
                if (time==3){
                    if (connect){
                        ConnectVpnManager.connect()
                    }else{
                        ConnectVpnManager.disconnect()
                    }
                }
                if (time in 3..9){
                    if (connectedOrStopped()){
                        showConnectAd.showOpenAd { jump->
                            cancel()
                            connectOrStopComplete(jumpToResult=jump)
                        }
                    }
                }

                if (time >= 10) {
                    cancel()
                    connectOrStopComplete()
                }
            }
        }
    }

    private fun connectedOrStopped()=if (connect) ConnectVpnManager.isConnected() else ConnectVpnManager.isStopped()

    private fun connectOrStopComplete(jumpToResult:Boolean=true){
        if (connectedOrStopped()){
            if (connect){
                SetPointManager.point("cute_vpnsucc")
                updateConnectedUI()
                if (autoConnect&&GoFirebase.planType=="B"){
                    LoadAdImpl.removeAll()
                }
            }else{
                updateStoppedUI()
                updateVpnInfo()
            }
            if (jumpToResult){
                jumpToResult()
            }
        }else{
            updateStoppedUI()
            if (connect){
                SetPointManager.point("go_vpn_fail")
            }
            showToast(if (connect) "Connect Fail" else "Disconnect Fail")
        }
        click=true
    }

    private fun jumpToResult(){
        if(ActivityCallback.goFront){
            startActivity(Intent(this,ResultUI::class.java).apply {
                putExtra("connect",connect)
            })
        }
    }

    private fun updateVpnInfo(){
        val currentVpn = ConnectVpnManager.currentVpn
        if (null!=currentVpn){
            if (currentVpn.isSmart){
                tv_vpn_name.text="Auto:${currentVpn.go_s_coun} - ${currentVpn.go_s_city} - ${currentVpn.go_s_num}"
                iv_vpn_logo.setImageResource(R.drawable.fast)
            }else{
                tv_vpn_name.text="${currentVpn.go_s_coun} - ${currentVpn.go_s_city} - ${currentVpn.go_s_num}"
                iv_vpn_logo.setImageResource(getVpnLogo(currentVpn.go_s_coun))
            }
        }
    }

    private fun updateConnectingUI(){
        lottie_view.showView(true)
        btn_lottie_view.showView(true)
        iv_connect_idle.showViewInvisible(false)
        iv_connect_btn.setImageResource(R.drawable.btn_connecting)
        tv_connect_status.text="Status: Connecting"
    }

    private fun updateStoppingUI(){
        lottie_view.showView(true)
        btn_lottie_view.showView(true)
        iv_connect_idle.showViewInvisible(false)
        iv_connect_btn.setImageResource(R.drawable.btn_stopping)
        tv_connect_status.text="Status: Stopping"
    }

    private fun updateStoppedUI(){
        lottie_view.showView(false)
        btn_lottie_view.showView(false)
        iv_connect_idle.showViewInvisible(true)
        iv_connect_btn.setImageResource(R.drawable.btn_connect)
        tv_connect_status.text="Status: Disconnected"
    }

    private fun updateConnectedUI(){
        lottie_view.showView(false)
        btn_lottie_view.showView(false)
        iv_connect_idle.showViewInvisible(true)
        iv_connect_btn.setImageResource(R.drawable.btn_connected)
        tv_connect_status.text="Status: Connected"
    }

    private fun updateConnectType(type:Int,noLimit:Boolean=false){
        if (!noLimit){
            if (!click){
                return
            }
            if(ConnectVpnManager.isConnected()){
                SureCancelDialog("Switching the connection mode will disconnect the current connection whether to continue"){
                    if (it){
                        clickConnectBtn()
                    }
                }.show(supportFragmentManager,"SureCancelDialog")
                return
            }
        }
        GoFirebase.connectType=type
        when(GoFirebase.connectType){
            1->{
                iv_connect_type_auto.isSelected=true
                iv_connect_type_open.isSelected=false
                iv_connect_type_ss.isSelected=false
            }
            2->{
                iv_connect_type_auto.isSelected=false
                iv_connect_type_open.isSelected=true
                iv_connect_type_ss.isSelected=false
            }
            3->{
                iv_connect_type_auto.isSelected=false
                iv_connect_type_open.isSelected=false
                iv_connect_type_ss.isSelected=true
            }
        }

    }

    override fun connectSuccess() {
        updateConnectedUI()
    }

    override fun disconnectSuccess() {
        if (click){
            updateStoppedUI()
        }
    }

    override fun onResume() {
        super.onResume()
        if(ReloadNativeAdManager.canReload(GoConfig.GO_VPN_HOME)){
            showNativeAd.showAd()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode==1228){
            when(data?.getStringExtra("action")){
                "disconnect"->{
                    iv_connect_btn.performClick()
                }
                "connect"->{
                    updateVpnInfo()
                    iv_connect_btn.performClick()
                }
            }
        }
    }

    override fun onBackPressed() {
        if(click){
            val activityExistsInStack = ActivityUtils.isActivityExistsInStack(HomeUI::class.java)
            if (activityExistsInStack){
                finish()
            }else{
                startActivity(Intent(this,HomeUI::class.java))
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        checkConnectJob?.cancel()
        checkConnectJob=null
        showNativeAd.stopShow()
        ConnectVpnManager.onDestroy()
        ReloadNativeAdManager.setBool(GoConfig.GO_VPN_HOME,true)

    }
}