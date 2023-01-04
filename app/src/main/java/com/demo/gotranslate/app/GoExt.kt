package com.demo.gotranslate.app

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Toast
import com.demo.gotranslate.config.GoConfig


fun logGo(string: String){
//    Log.e("qwer",string)
}

fun Context.showToast(content:String){
    Toast.makeText(this,content,Toast.LENGTH_SHORT).show()
}

fun View.showView(show:Boolean){
    visibility=if (show) View.VISIBLE else View.GONE
}

fun Context.copyResult(content: String){
    try {
        if (content.isEmpty()) return
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val newPlainText = ClipData.newPlainText("Label", content)
        clipboardManager.setPrimaryClip(newPlainText)
        showToast("copy success")
    }catch (e:Exception){
        showToast("copy fail")
    }
}

fun Context.openEmail(){
    try {
        val uri = Uri.parse("mailto:${GoConfig.EMAIL}")
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        startActivity(intent)
    }catch (e: java.lang.Exception){
        showToast("Contact us by email：${GoConfig.EMAIL}")
    }
}

fun Context.shareResult(content: String){
    if (content.isEmpty()) return
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_TEXT, content)
    startActivity(Intent.createChooser(intent, "share"))
}

fun Context.netStatus(): Int {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connectivityManager.activeNetworkInfo
    if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
        if (activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI) {
            return 2
        } else if (activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE) {
            return 0
        }
    } else {
        return 1
    }
    return 1
}


fun Context.fit(){
    val metrics: DisplayMetrics = resources.displayMetrics
    val td = metrics.heightPixels / 760f
    val dpi = (160 * td).toInt()
    metrics.density = td
    metrics.scaledDensity = td
    metrics.densityDpi = dpi
}