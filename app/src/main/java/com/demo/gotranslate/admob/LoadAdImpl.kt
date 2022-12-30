package com.demo.gotranslate.admob

import com.demo.gotranslate.app.logGo
import com.demo.gotranslate.bean.ConfigAdBean
import com.demo.gotranslate.bean.ResultAdBean
import com.demo.gotranslate.config.GoConfig

object LoadAdImpl:LoadAd() {
    var fullAdShowing=false
    private val loadingKey= arrayListOf<String>()
    private val result= hashMapOf<String,ResultAdBean>()

    fun loadAd(key:String,loadAgain: Boolean=true){
        if (MaxNumManager.checkLimit()){
            logGo("load ad limit")
            return
        }

        if(loadingKey.contains(key)){
            logGo("$key loading")
            return
        }

        if(result.containsKey(key)){
            val resultAdBean = result[key]
            if(resultAdBean?.ad!=null){
                if(resultAdBean.expired()){
                    removeResult(key)
                }else{
                    logGo("$key has cache")
                    return
                }
            }
        }

        val parseAdList = parseAdList(key)
        if(parseAdList.isEmpty()){
            logGo("$key no ad data")
            return
        }
        loadingKey.add(key)
        loopLoadAd(key,parseAdList.iterator(),loadAgain)
    }

    private fun loopLoadAd(key: String, iterator: Iterator<ConfigAdBean>, loadAgain: Boolean){
        startLoad(key,iterator.next()){
            if(it.fail.isEmpty()){
                logGo("$key load success")
                loadingKey.remove(key)
                result[key]=it
            }else{
                logGo("$key load fail, ${it.fail}")
                if(iterator.hasNext()){
                    loopLoadAd(key,iterator,loadAgain)
                }else{
                    loadingKey.remove(key)
                    if(loadAgain&&key==GoConfig.GO_OPEN){
                        loadAd(key,loadAgain = false)
                    }
                }
            }
        }
    }

    fun getResult(key: String)= result[key]?.ad

    fun removeResult(key: String){
        result.remove(key)
    }

    fun removeAll(){
        result.clear()
        loadingKey.clear()
        loadAd(GoConfig.GO_OPEN)
        loadAd(GoConfig.GO_HOME)
        loadAd(GoConfig.GO_TRANSLATE)
        loadAd(GoConfig.GO_WRITE_HOME)
        loadAd(GoConfig.GO_VPN_HOME)
        loadAd(GoConfig.GO_VPN_RESULT)
        loadAd(GoConfig.GO_VPN_CONN)
    }
}