package com.demo.gotranslate.ui.translator.camera

import android.content.DialogInterface
import android.content.Intent
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.demo.gotranslate.R
import com.demo.gotranslate.admob.ShowOpenAd
import com.demo.gotranslate.app.copyResult
import com.demo.gotranslate.app.showToast
import com.demo.gotranslate.app.showView
import com.demo.gotranslate.base.BaseUI
import com.demo.gotranslate.config.GoConfig
import com.demo.gotranslate.manager.LanguageManager
import com.demo.gotranslate.manager.PhotoTranslateManager
import com.demo.gotranslate.ui.HomeUI
import com.demo.gotranslate.ui.dialog.TranslatingDialog
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.android.synthetic.main.activity_take_result.*
import kotlinx.coroutines.launch

class TakeResultUI:BaseUI(R.layout.activity_take_result) {
    private val showOpenAd by lazy {  ShowOpenAd(this, GoConfig.GO_TRANSLATE){} }

    private val textRecognizer by lazy {
        when(LanguageManager.topLanguage.code){
            "zh" -> TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())
            "ja" -> TextRecognition.getClient(JapaneseTextRecognizerOptions.Builder().build())
            "ko" -> TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())
            "sa" -> TextRecognition.getClient(DevanagariTextRecognizerOptions.Builder().build())
            else -> TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        }.apply {
            lifecycleScope.launch { lifecycle.addObserver(this@apply) }
        }
    }

    override fun view() {
        immersionBar.statusBarView(view).init()
        iv_back.setOnClickListener { finish() }
        llc_camera.setOnClickListener { finish() }
        llc_sure.setOnClickListener { recognizerText() }
        llc_copy.setOnClickListener { copyResult(tv_result.text.toString()) }

        if(null!=PhotoTranslateManager.photoBitmap){
            iv_photo.setImageBitmap(PhotoTranslateManager.photoBitmap)
        }else{
            showToast("take photo fail")
            finish()
        }
    }

    private fun recognizerText(){
        if(null!=PhotoTranslateManager.photoBitmap){
            textRecognizer.process(InputImage.fromBitmap(PhotoTranslateManager.photoBitmap!!, 0))
                .addOnSuccessListener {
                    if(it.text.isEmpty()){
                        showRecognizerFailDialog()
                    }else{
                        translate(it.text)
                    }
                }
                .addOnFailureListener {
                    showRecognizerFailDialog()
                }
        }
    }

    private fun showRecognizerFailDialog(){
        AlertDialog.Builder(this)
            .setTitle("recognizer text fail")
            .setPositiveButton("try again") { p0, p1 -> finish()}
            .setNegativeButton("cancel") { p0, p1 ->
                startActivity(Intent(this,HomeUI::class.java))
                finish()
            }
            .show()
    }

    private fun translate(content:String){
        val translatingDialog = TranslatingDialog()
        translatingDialog.show(supportFragmentManager,"TranslatingDialog")
        LanguageManager.translate(content){
            translatingDialog.dismiss()
            if (it.isEmpty()){
                showToast("translate fail")
            }else{
                llc_copy.showView(true)
                scrollview.showView(true)
                llc_camera.showView(false)
                llc_sure.showView(false)
                tv_result.text=it
                showOpenAd.showOpenAd {  }
            }
        }
    }
}