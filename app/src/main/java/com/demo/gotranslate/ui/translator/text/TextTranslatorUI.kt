package com.demo.gotranslate.ui.translator.text

import android.annotation.SuppressLint
import android.content.Intent
import com.demo.gotranslate.R
import com.demo.gotranslate.app.copyResult
import com.demo.gotranslate.app.shareResult
import com.demo.gotranslate.app.showToast
import com.demo.gotranslate.base.BaseUI
import com.demo.gotranslate.manager.LanguageManager
import com.demo.gotranslate.ui.dialog.TranslatingDialog
import com.demo.gotranslate.ui.translator.LanguageUI
import kotlinx.android.synthetic.main.activity_text_translator.*

class TextTranslatorUI: BaseUI(R.layout.activity_text_translator){
    override fun view() {
        immersionBar.statusBarView(view).init()
        setLanguageInfo()
        setListener()
    }

    private fun setLanguageInfo(){
        val topLanguage = LanguageManager.topLanguage
        iv_top_logo.setImageResource(topLanguage.icon)
        tv_top_name.text=topLanguage.name

        val bottomLanguage = LanguageManager.bottomLanguage
        tv_bottom_name.text=bottomLanguage.name
        iv_bottom_logo.setImageResource(bottomLanguage.icon)
    }

    private fun setListener(){
        iv_back.setOnClickListener { finish() }
        tv_top_content.setOnClickListener {
            val intent = Intent(this, InputContentUI::class.java).apply {
                putExtra("content", tv_top_content.text.toString().trim())
            }
            startActivityForResult(intent,1000)
        }
        llc_top.setOnClickListener { toChooseLanguage(true) }
        cl_bottom.setOnClickListener { toChooseLanguage(false) }
        iv_top_cancel.setOnClickListener {
            tv_top_content.text=""
            tv_bottom_content.text=""
        }
        iv_change.setOnClickListener { changeTranslate() }
        iv_top_copy.setOnClickListener {
            copyResult(tv_bottom_content.text.toString().trim())
        }
        iv_top_share.setOnClickListener {
            shareResult(tv_bottom_content.text.toString().trim())
        }
    }

    private fun toChooseLanguage(top:Boolean){
        val intent = Intent(this, LanguageUI::class.java).apply {
            putExtra("top",top)
        }
        startActivityForResult(intent,100)
    }

    private fun changeTranslate(){
        val bottom = tv_bottom_content.text.toString()
        LanguageManager.changeLanguage()
        setLanguageInfo()
        tv_top_content.text=bottom
        tv_bottom_content.text=""
        translate(bottom)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(resultCode){
            100->{
                setLanguageInfo()
                val isTop = data?.getBooleanExtra("isTop",false)
                if(isTop==true){
                    tv_top_content.text=""
                    tv_bottom_content.text=""
                }
                if(isTop==false){
                    tv_bottom_content.text=""
                    translate(tv_top_content.text.toString())
                }

            }
            1000->translate(data?.getStringExtra("content")?:"")
        }
    }

    private fun translate(content: String) {
        if(content.isEmpty()){
            return
        }
        tv_top_content.text=content
        val translatingDialog = TranslatingDialog()
        translatingDialog.show(supportFragmentManager,"TranslatingDialog")
        LanguageManager.translate(content){
            translatingDialog.dismiss()
            if (it.isEmpty()){
                showToast("translate fail")
            }else{
                tv_bottom_content.text=it
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setLanguageInfo()
    }
}