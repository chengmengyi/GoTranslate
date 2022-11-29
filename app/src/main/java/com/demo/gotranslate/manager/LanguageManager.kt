package com.demo.gotranslate.manager

import com.demo.gotranslate.R
import com.demo.gotranslate.app.logGo
import com.demo.gotranslate.bean.LanguageBean
import com.demo.gotranslate.ui.dialog.TranslatingDialog
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.tencent.mmkv.MMKV
import java.util.*

object LanguageManager {
    lateinit var topLanguage:LanguageBean
    lateinit var bottomLanguage:LanguageBean

    val allList= arrayListOf<LanguageBean>()

    init {
        initTopLanguage()
        initBottomLanguage()
    }

    fun initTopLanguage(){
        topLanguage=LanguageBean("en", Locale("en").displayName, getIconByCode("en"), type = 2)
    }


    fun initBottomLanguage(){
        bottomLanguage=LanguageBean("en", Locale("en").displayName, getIconByCode("en"), type = 2)
    }

    fun initAllLanguageList(){
        val allLanguages = TranslateLanguage.getAllLanguages()
        allList.clear()
        allLanguages.forEach {
            allList.add(LanguageBean(it, Locale(it).displayName, getIconByCode(it)))
        }
        readChooseLanguage()
        checkDownloaded()
    }

    private fun checkDownloaded(){
        RemoteModelManager.getInstance().getDownloadedModels(TranslateRemoteModel::class.java)
            .addOnSuccessListener { models ->
                allList.forEach {
                    models.forEach { model->
                        if(it.code == model.language){
                            it.type=2
                        }
                    }
                }
            }
            .addOnFailureListener {

            }

    }

    fun changeLanguage(){
        val top = topLanguage
        val bottom = bottomLanguage
        saveChooseLanguage(true,bottom)
        saveChooseLanguage(false,top)
    }

    fun saveChooseLanguage(isTop:Boolean,languageBean: LanguageBean){
        if(isTop){
            topLanguage=languageBean
            MMKV.defaultMMKV().encode("top_code",languageBean.code)
        }else{
            bottomLanguage=languageBean
            MMKV.defaultMMKV().encode("bottom_code",languageBean.code)
        }
    }

    private fun readChooseLanguage(){
        val topCode = MMKV.defaultMMKV().decodeString("top_code") ?: ""
        if(topCode.isNotEmpty()){
            topLanguage=LanguageBean(topCode, Locale(topCode).displayName, getIconByCode(topCode))
        }
        val bottomCode = MMKV.defaultMMKV().decodeString("bottom_code") ?: ""
        if(bottomCode.isNotEmpty()){
            bottomLanguage=LanguageBean(bottomCode, Locale(bottomCode).displayName, getIconByCode(bottomCode))
        }
    }

    fun download(languageBean: LanguageBean,callback:()->Unit){
        if(languageBean.type==1) {
            return
        }
        val instance = RemoteModelManager.getInstance()
        val build = TranslateRemoteModel.Builder(languageBean.code).build()
        val conditions = DownloadConditions.Builder().build()
        //0未下载 1下载中 2已下载
        languageBean.type=1
        callback.invoke()
        instance.download(build,conditions)
            .addOnSuccessListener {
                languageBean.type=2
                callback.invoke()
            }
            .addOnFailureListener {
                languageBean.type=0
                callback.invoke()
            }
    }

    fun delete(languageBean: LanguageBean,callback:(success:Boolean)->Unit){
        val instance = RemoteModelManager.getInstance()
        val build = TranslateRemoteModel.Builder(languageBean.code).build()
        instance.deleteDownloadedModel(build)
            .addOnSuccessListener {
                languageBean.type=0
                callback.invoke(true)
            }
            .addOnFailureListener {
                callback.invoke(false)
            }
    }

    fun translate(content:String,callback: (result:String) -> Unit){
        val client = Translation.getClient(
            TranslatorOptions.Builder()
                .setSourceLanguage(topLanguage.code)
                .setTargetLanguage(bottomLanguage.code)
                .build()
        )
        client.translate(content)
            .addOnSuccessListener {
                callback.invoke(it)
            }
            .addOnFailureListener {
                callback.invoke("")
            }
    }

    private fun getIconByCode(code:String):Int{
        return when(code){
            "af"-> R.drawable.za
            "sq"-> R.drawable.al
            "ar"-> R.drawable.dz
            "be"-> R.drawable.by
            "bg"-> R.drawable.bg_icon
            "bn"-> R.drawable.bd
            "ca"-> R.drawable.ad
            "zh"-> R.drawable.cn
            "hr"-> R.drawable.hr
            "cs"-> R.drawable.cz
            "da"-> R.drawable.dk
            "nl"-> R.drawable.cz
            "en"-> R.drawable.us
            "et"-> R.drawable.ee
            "fr"-> R.drawable.fr
            "gl","es"-> R.drawable.es
            "ka"-> R.drawable.ge
            "de"-> R.drawable.de
            "el"-> R.drawable.gr
            "gu","hi","mr","ta","te"-> R.drawable.india
            "he"-> R.drawable.il
            "hu"-> R.drawable.hu
            "is"-> R.drawable.is_country
            "id"-> R.drawable.id
            "ga"-> R.drawable.ie
            "it"-> R.drawable.it
            "mk"-> R.drawable.mk
            "ms"-> R.drawable.my
            "mt"-> R.drawable.mt
            "no"-> R.drawable.no
            "fa"-> R.drawable.ir
            "pl"-> R.drawable.pl
            "pt"-> R.drawable.pt
            "ro"-> R.drawable.ro
            "ru"-> R.drawable.ru
            "sk"-> R.drawable.sk
            "sl"-> R.drawable.sl
            "sv"-> R.drawable.se
            "tl"-> R.drawable.ph
            "th"-> R.drawable.th
            "tr"-> R.drawable.tr
            "uk"-> R.drawable.ua
            "ur"-> R.drawable.pk
            "vi"-> R.drawable.vn
            "cy"-> R.drawable.gb
            "nl"-> R.drawable.nl
            else-> R.drawable.default_country
        }
    }
}