package com.demo.gotranslate.base

import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import com.demo.gotranslate.app.fit
import com.gyf.immersionbar.ImmersionBar

abstract class BaseUI(private val id:Int):AppCompatActivity() {
    protected lateinit var immersionBar: ImmersionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fit()
        setContentView(id)
        immersionBar= ImmersionBar.with(this).apply {
            statusBarAlpha(0f)
            autoDarkModeEnable(true)
            statusBarDarkFont(false)
            init()
        }
        view()
    }

    abstract fun view()
}