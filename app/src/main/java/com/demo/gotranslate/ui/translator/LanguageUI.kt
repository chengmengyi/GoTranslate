package com.demo.gotranslate.ui.translator

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.gotranslate.R
import com.demo.gotranslate.adapter.LanguageAdapter
import com.demo.gotranslate.base.BaseUI
import com.demo.gotranslate.bean.LanguageBean
import com.demo.gotranslate.manager.LanguageManager
import kotlinx.android.synthetic.main.activity_language.*
import java.util.*

class LanguageUI:BaseUI(R.layout.activity_language) {
    private var isTop=true

    override fun view() {
        immersionBar.statusBarView(view).init()
        iv_back.setOnClickListener { finish() }
        llc_now.setOnClickListener { finish() }
        setNowLanguageInfo()
        setAdapter()
    }

    private fun setNowLanguageInfo(){
        isTop=intent.getBooleanExtra("top",true)
        val bean=if (isTop)LanguageManager.topLanguage else LanguageManager.bottomLanguage
        tv_now_name.text=bean.name
        iv_now_logo.setImageResource(bean.icon)
    }

    private fun setAdapter(){
        rv.apply {
            layoutManager=LinearLayoutManager(this@LanguageUI)
            adapter=LanguageAdapter(
                this@LanguageUI,
                clickItem = { clickItem(it) },
                deleteItem = { deleteItem(it) })
        }
    }

    private fun clickItem(languageBean: LanguageBean){
        if(languageBean.type==2){
            LanguageManager.saveChooseLanguage(isTop,languageBean)
            setResult(100, Intent().apply { putExtra("isTop",isTop) })
            finish()
        }
    }

    private fun deleteItem(languageBean: LanguageBean){
        if(isTop&&LanguageManager.topLanguage.code==languageBean.code){
            LanguageManager.initTopLanguage()
        }
        if(!isTop&&LanguageManager.bottomLanguage.code==languageBean.code){
            LanguageManager.initBottomLanguage()
        }
    }
}