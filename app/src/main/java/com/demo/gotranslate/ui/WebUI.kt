package com.demo.gotranslate.ui

import com.demo.gotranslate.R
import com.demo.gotranslate.base.BaseUI
import com.demo.gotranslate.config.GoConfig
import kotlinx.android.synthetic.main.activity_web.*

class WebUI:BaseUI(R.layout.activity_web) {
    override fun view() {
        immersionBar.statusBarView(view).init()
        iv_back.setOnClickListener { finish() }

        webview.apply {
            settings.javaScriptEnabled=true
            loadUrl(GoConfig.WEB)
        }
    }
}