package com.demo.gotranslate.bean

class ConfigAdBean(
    val go_source:String,
    val go_id:String,
    val go_type:String,
    val go_sort:Int
) {

    override fun toString(): String {
        return "ConfigAdBean(go_source='$go_source', go_id='$go_id', go_type='$go_type', go_sort=$go_sort)"
    }
}