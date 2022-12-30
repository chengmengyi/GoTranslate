package com.demo.gotranslate.ui.dialog

import com.demo.gotranslate.R
import com.demo.gotranslate.base.BaseDialogUI
import com.demo.gotranslate.manager.SetPointManager
import kotlinx.android.synthetic.main.dialog_sure_cancel.*

class SureCancelDialog(
    private val text:String,
    private val isVpnDialog:Boolean=false,
    private val click:(sure:Boolean)->Unit
):BaseDialogUI(R.layout.dialog_sure_cancel) {

    override fun view() {
        dialog?.setCancelable(false)
        if (isVpnDialog){
            SetPointManager.point("go_pop")
        }
        tv_content.text=text
        iv_cancel.setOnClickListener {
            if (isVpnDialog){
                SetPointManager.point("go_pop_close")
            }
            dismiss()
            click.invoke(false)
        }

        tv_sure.setOnClickListener {
            if (isVpnDialog){
                SetPointManager.point("go_pop_click")
            }
            dismiss()
            click.invoke(true)
        }
    }
}