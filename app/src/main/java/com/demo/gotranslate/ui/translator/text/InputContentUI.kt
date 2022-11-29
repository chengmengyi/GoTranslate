package com.demo.gotranslate.ui.translator.text

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import com.demo.gotranslate.R
import com.demo.gotranslate.app.showView
import com.demo.gotranslate.base.BaseUI
import kotlinx.android.synthetic.main.activity_input_content.*

class InputContentUI:BaseUI(R.layout.activity_input_content) {
    override fun view() {
        immersionBar.statusBarView(view).init()

        iv_back.setOnClickListener { finish() }

        et_content.addTextChangedListener {
            val content = it?.toString()?.trim()
            tv_translate.showView(!content.isNullOrEmpty())
        }
        et_content.setText(intent.getStringExtra("content"))

        tv_translate.setOnClickListener {
            val content = et_content.text?.toString()?.trim()
            if(!content.isNullOrEmpty()){
                val intent = Intent()
                intent.putExtra("content",content)
                setResult(1000,intent)
                finish()
            }
        }

        Handler().postDelayed({
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
            et_content.requestFocus()
        },500)
    }
}