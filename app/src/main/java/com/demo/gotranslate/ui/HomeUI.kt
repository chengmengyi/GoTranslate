package com.demo.gotranslate.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.view.Gravity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.demo.gotranslate.R
import com.demo.gotranslate.admob.RefreshAdManager
import com.demo.gotranslate.admob.ShowNativeAd
import com.demo.gotranslate.app.logGo
import com.demo.gotranslate.app.openEmail
import com.demo.gotranslate.app.showToast
import com.demo.gotranslate.base.BaseUI
import com.demo.gotranslate.config.GoConfig
import com.demo.gotranslate.ui.translator.camera.CameraUI
import com.demo.gotranslate.ui.translator.text.TextTranslatorUI
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home_content.*
import kotlinx.android.synthetic.main.activity_home_drawer.*

class HomeUI:BaseUI(R.layout.activity_home) {
    private val showNativeAd by lazy { ShowNativeAd(this,GoConfig.GO_HOME) }

    override fun view() {
        immersionBar.statusBarView(view).init()

        llc_text.setOnClickListener {
            if(!drawer_layout.isOpen){
                startActivity(Intent(this, TextTranslatorUI::class.java))
            }
        }

        llc_ocr.setOnClickListener {
            if(!drawer_layout.isOpen){
                if(checkCameraPermission()){
                    toCameraUI()
                }
            }
        }

        iv_set.setOnClickListener {
            if(!drawer_layout.isOpen){
                drawer_layout.openDrawer(Gravity.LEFT)
            }
        }
        llc_web.setOnClickListener { startActivity(Intent(this,WebUI::class.java)) }
        llc_contact.setOnClickListener { openEmail() }
    }

    private fun toCameraUI(){
        startActivity(Intent(this, CameraUI::class.java))
    }

    private fun checkCameraPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) return true
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 101)
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                toCameraUI()
            }else{
                showToast("Please open permission in settings")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(RefreshAdManager.canRefresh(GoConfig.GO_HOME)){
            showNativeAd.showAd()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        showNativeAd.stopShow()
    }
}