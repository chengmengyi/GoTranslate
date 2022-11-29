package com.demo.gotranslate.ui.dialog

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import com.demo.gotranslate.R
import com.demo.gotranslate.base.BaseDialogUI
import kotlinx.android.synthetic.main.dialog_translating.*

class TranslatingDialog:BaseDialogUI(R.layout.dialog_translating) {
    private var objectAnimator:ObjectAnimator?=null

    override fun view() {
        objectAnimator=ObjectAnimator.ofFloat(iv_loading, "rotation", 0f, 360f).apply {
            duration=1000L
            repeatCount= ValueAnimator.INFINITE
            repeatMode= ObjectAnimator.RESTART
            start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        objectAnimator?.removeAllUpdateListeners()
        objectAnimator?.cancel()
        objectAnimator=null
    }
}