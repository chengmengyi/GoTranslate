package com.demo.gotranslate.config

import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.demo.gotranslate.admob.MaxNumManager
import com.demo.gotranslate.app.goApp
import com.demo.gotranslate.app.logGo
import com.demo.gotranslate.bean.VpnBean
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.tencent.mmkv.MMKV
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

object GoFirebase {
    val countryList= arrayListOf<String>()
    val vpnList= arrayListOf<VpnBean>()
    var go_re="1"
    var go_vpn_pop="2"
    var oa_program=""
    var auto_go="1"
    var irUser=false


    fun readFirebase(){
        checkIRUser()
        createVpn(GoConfig.localVpnList)

//        val remoteConfig = Firebase.remoteConfig
//        remoteConfig.fetchAndActivate().addOnCompleteListener {
//            if (it.isSuccessful){
//                parseAdJson(remoteConfig.getString("go_ad"))
//                parseCity(remoteConfig.getString("smart_go"))
//                parseVpn(remoteConfig.getString("go_all_s"))
//
//                val s1=remoteConfig.getString("go_re")
//                if (s1.isNotEmpty()){
//                    go_re=s1
//                }
//                val s2=remoteConfig.getString("go_vpn_pop")
//                if (s2.isNotEmpty()){
//                    go_vpn_pop=s2
//                }
//                val s3=remoteConfig.getString("oa_program")
//                if (s3.isNotEmpty()){
//                    oa_program=s3
//                }
//
//                val s4=remoteConfig.getString("auto_go")
//                if (s4.isNotEmpty()){
//                    auto_go=s4
//                }
//            }
//        }

    }

    fun reloadVpnConfig(callback:()->Unit){
//        val remoteConfig = Firebase.remoteConfig
//        remoteConfig.fetchAndActivate()
//            .addOnCompleteListener {
//                if (it.isSuccessful){
//                    parseVpn(remoteConfig.getString("go_all_s"))
//                }
//                callback.invoke()
//            }
//            .addOnFailureListener {
//                callback.invoke()
//            }

        callback.invoke()
    }

    private fun parseCity(string: String){
        try {
            val jsonArray = JSONArray(string)
            for (index in 0 until jsonArray.length()){
                countryList.add(jsonArray.optString(index))
            }
        }catch (e:Exception){

        }
    }

    private fun parseVpn(string: String){
        try {
            val jsonArray = JSONObject(string).getJSONArray("go_all_s")
            for (index in 0 until jsonArray.length()){
                val jsonObject = jsonArray.getJSONObject(index)
                vpnList.add(VpnBean(
                    jsonObject.optString("go_s_account"),
                    jsonObject.optInt("go_s_port"),
                    jsonObject.optString("go_s_password"),
                    jsonObject.optString("go_s_coun"),
                    jsonObject.optString("go_s_city"),
                    jsonObject.optString("go_s_ip"),
                ))
            }
            createVpn(vpnList)
        }catch (e:Exception){

        }
    }

    private fun createVpn(list:ArrayList<VpnBean>){
        list.forEach { it.createVpn() }
    }

    private fun parseAdJson(string: String){
        try {
            MaxNumManager.readConfigMax(string)
            MMKV.defaultMMKV().encode("go_ad",string)
        }catch (e:Exception){

        }
    }

    fun getAdJson() = MMKV.defaultMMKV().decodeString("go_ad")?:GoConfig.localAdConfig

    fun readReferrer(){
        if(getLocalReferrer().isEmpty()){
            val referrerClient = InstallReferrerClient.newBuilder(goApp).build()
            referrerClient.startConnection(object : InstallReferrerStateListener {
                override fun onInstallReferrerSetupFinished(responseCode: Int) {
                    try {
                        referrerClient.endConnection()
                        when (responseCode) {
                            InstallReferrerClient.InstallReferrerResponse.OK -> {
                                val installReferrer = referrerClient.installReferrer.installReferrer
                                MMKV.defaultMMKV().encode("go_referrer",installReferrer)
                            }
                            else->{

                            }
                        }
                    } catch (e: Exception) {

                    }
                }
                override fun onInstallReferrerServiceDisconnected() {
                }
            })
        }
    }

    fun getLocalReferrer()= MMKV.defaultMMKV().decodeString("go_referrer", "")?:""

    fun randomPlan(){
        if(oa_program.isEmpty()){
            val oa_program = MMKV.defaultMMKV().decodeString("oa_program") ?: ""
            if (oa_program.isEmpty()){
                val nextInt = Random().nextInt(100)
                this.oa_program=if (nextInt>20) "B" else "A"
                MMKV.defaultMMKV().encode("oa_program",oa_program)
            }else{
                this.oa_program=oa_program
            }
        }
    }

    private fun checkIRUser(){
        val country = Locale.getDefault().country
        if(country=="IR"){
            irUser=true
        }else{
            OkGo.get<String>("https://api.myip.com/")
                .execute(object : StringCallback(){
                    override fun onSuccess(response: Response<String>?) {
//                        ipJson="""{"ip":"89.187.185.11","country":"United States","cc":"IR"}"""
                        try {
                            irUser=JSONObject(response?.body()?.toString()).optString("cc")=="IR"
                        }catch (e:Exception){

                        }
                    }

                    override fun onError(response: Response<String>?) {
                        super.onError(response)
                    }
                })
        }
    }
}