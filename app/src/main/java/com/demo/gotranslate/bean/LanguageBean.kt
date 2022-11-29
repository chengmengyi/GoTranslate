package com.demo.gotranslate.bean

class LanguageBean(
    val code:String,
    val name:String,
    var icon:Int,
    var type:Int=0, //0未下载 1下载中 2已下载
)