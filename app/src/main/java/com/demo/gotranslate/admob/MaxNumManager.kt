package com.demo.gotranslate.admob

import android.util.Log
import com.tencent.mmkv.MMKV
import org.json.JSONObject
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

object MaxNumManager {
    private var maxClick=15
    private var maxShow=50

    private var currentClick=0
    private var currentShow=0

    fun readConfigMax(string: String){
        try {
            val jsonObject = JSONObject(string)
            maxClick=jsonObject.optInt("go_max_click")
            maxShow=jsonObject.optInt("go_max_show")
        }catch (e:Exception){

        }
    }

    fun readLocalNum(){
        currentClick=MMKV.defaultMMKV().decodeInt(key("currentClick"),0)
        currentShow=MMKV.defaultMMKV().decodeInt(key("currentShow"),0)
    }

    fun clickNumAdd(){
        currentClick++
        MMKV.defaultMMKV().encode(key("currentClick"), currentClick)
    }

    fun showNumAdd(){
        currentShow++
        MMKV.defaultMMKV().encode(key("currentShow"), currentShow)
    }

    fun checkLimit()= currentShow> maxShow|| currentClick> maxClick

    private fun key(string:String)="${string}...${SimpleDateFormat("yyyy-MM-dd").format(Date(System.currentTimeMillis()))}"
}