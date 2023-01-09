package com.demo.gotranslate.ui

import android.animation.ValueAnimator
import android.content.Intent
import android.view.KeyEvent
import android.view.animation.LinearInterpolator
import com.demo.gotranslate.R
import com.demo.gotranslate.admob.LoadAdImpl
import com.demo.gotranslate.admob.MaxNumManager
import com.demo.gotranslate.admob.ShowOpenAd
import com.demo.gotranslate.app.isBuyUser
import com.demo.gotranslate.base.BaseUI
import com.demo.gotranslate.config.GoConfig
import com.demo.gotranslate.config.GoFirebase
import com.demo.gotranslate.manager.SetPointManager
import com.demo.gotranslate.ui.vpn.ConnectVpnUI
import com.demo.gotranslate.util.ReloadNativeAdManager
import com.demo.gotranslate.vpn.ConnectVpnManager
import kotlinx.android.synthetic.main.activity_main.*

class MainUI : BaseUI(R.layout.activity_main) {
    private var isColdLoad=true
    private var animator:ValueAnimator?=null
    private val showOpenAd by lazy { ShowOpenAd(this,GoConfig.GO_OPEN){ checkPlan() } }

    override fun view() {
        GoFirebase.readReferrer()
        ReloadNativeAdManager.reset()
        MaxNumManager.readLocalNum()
        preLoadAd()
        startAnimator()
        isColdLoad=intent.getBooleanExtra("isColdLoad",true)
    }

    private fun preLoadAd(){
        LoadAdImpl.loadAd(GoConfig.GO_OPEN)
        LoadAdImpl.loadAd(GoConfig.GO_HOME)
        LoadAdImpl.loadAd(GoConfig.GO_TRANSLATE)
        LoadAdImpl.loadAd(GoConfig.GO_WRITE_HOME)
        LoadAdImpl.loadAd(GoConfig.GO_VPN_HOME)
        LoadAdImpl.loadAd(GoConfig.GO_VPN_RESULT)
        LoadAdImpl.loadAd(GoConfig.GO_VPN_CONN)
        LoadAdImpl.loadAd(GoConfig.GO_VPN_LIST)
    }

    private fun startAnimator(){
        animator=ValueAnimator.ofInt(0, 100).apply {
            duration = 10000L
            interpolator = LinearInterpolator()
            addUpdateListener {
                val progress = it.animatedValue as Int
                progress_view.progress = progress
                val pro = (10 * (progress / 100.0F)).toInt()
                if (pro in 2..9){
                    showOpenAd.showOpenAd{ b->
                        progress_view.progress = 100
                        stopAnimator()
                        if(b){
                            checkPlan()
                        }
                    }
                }else if (pro>=10){
                    checkPlan()
                }
            }
            start()
        }
    }

    private fun checkPlan(){
        if (!GoFirebase.getLocalReferrer().isBuyUser()){
            SetPointManager.setUser("a")
            doLogicPlanA()
            return
        }
        GoFirebase.randomPlan()
        SetPointManager.setUser(GoFirebase.planType.toLowerCase())
        if (ConnectVpnManager.isConnected()){
            toHomeUI()
        }else{
            if(GoFirebase.planType=="B"){
                doLogicPlanB()
            }else{
                doLogicPlanA()
            }
        }
    }

    private fun doLogicPlanA(){
        if (GoFirebase.go_vpn_pop=="1"&&ConnectVpnManager.isStopped()&&checkCanShowVpnDialog()){
            toHomeUI(showDialog = true)
            return
        }
        if (GoFirebase.go_vpn_pop=="2"&&isColdLoad&&ConnectVpnManager.isStopped()&&checkCanShowVpnDialog()){
            toHomeUI(showDialog = true)
            return
        }
        toHomeUI()
    }

    private fun doLogicPlanB(){
        startActivity(Intent(this,ConnectVpnUI::class.java).apply {
            putExtra("auto",true)
        })
        finish()
    }

    private fun checkCanShowVpnDialog():Boolean{
        when(GoFirebase.go_re){
            "1"->return true
            "2"->return GoFirebase.getLocalReferrer().isBuyUser()
            "3"->{
                val localReferrer = GoFirebase.getLocalReferrer()
                return localReferrer.contains("facebook")||localReferrer.contains("fb4a")
            }
        }
        return false
    }

//    private fun showVpnDialog(){
//        SureCancelDialog("Turn on vpn to Protect Privacy",isVpnDialog = true){
//            if (it){
//                doLogicPlanB()
//            }else{
//                toHomeUI()
//            }
//        }.show(supportFragmentManager,"SureCancelDialog")
//    }

    private fun toHomeUI(showDialog:Boolean=false){
        startActivity(Intent(this,HomeUI::class.java).apply {
            putExtra("showDialog",showDialog)
        })
        finish()
    }

    private fun stopAnimator(){
        animator?.removeAllUpdateListeners()
        animator?.cancel()
        animator=null
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode== KeyEvent.KEYCODE_BACK){
            return true
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        animator?.resume()
    }

    override fun onPause() {
        super.onPause()
        animator?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAnimator()
    }
}