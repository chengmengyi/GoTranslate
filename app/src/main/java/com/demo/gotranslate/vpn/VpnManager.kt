package com.demo.gotranslate.vpn

import com.demo.gotranslate.app.logGo
import com.demo.gotranslate.bean.VpnBean
import com.demo.gotranslate.config.GoConfig
import com.demo.gotranslate.config.GoFirebase

object VpnManager {
    var smartVpnBean=VpnBean()
    val otherVpnList = arrayListOf<VpnBean>()

    fun createSmartVpn(){
        val smartVpnBean = if (GoFirebase.countryList.isNotEmpty()&&GoFirebase.vpnList.isNotEmpty()){
            val filter = GoFirebase.vpnList.filter { GoFirebase.countryList.contains(it.go_s_coun) }
            if (filter.isEmpty()){
                GoFirebase.vpnList.random()
            }else{
                filter.random()
            }
        }else{
            val filter = GoConfig.localVpnList.filter { GoConfig.localCountryList.contains(it.go_s_coun) }
            if (filter.isEmpty()){
                GoConfig.localVpnList.random()
            }else{
                filter.random()
            }
        }
        this.smartVpnBean.go_s_account=smartVpnBean.go_s_account
        this.smartVpnBean.go_s_port=smartVpnBean.go_s_port
        this.smartVpnBean.go_s_num=smartVpnBean.go_s_num
        this.smartVpnBean.go_s_password=smartVpnBean.go_s_password
        this.smartVpnBean.go_s_coun=smartVpnBean.go_s_coun
        this.smartVpnBean.go_s_city=smartVpnBean.go_s_city
        this.smartVpnBean.go_s_ip=smartVpnBean.go_s_ip
        this.smartVpnBean.isSmart=true
        refreshOtherVpnList()
    }

    fun refreshOtherVpnList(){
        val list = GoFirebase.vpnList.ifEmpty { GoConfig.localVpnList }
        val country= arrayListOf<String>()
        val vpnMap= hashMapOf<String,List<VpnBean>>()
        list.forEach {
            if(!country.contains(it.go_s_coun)){
                country.add(it.go_s_coun)
            }
        }
        country.forEach { coun->
            vpnMap[coun]=list.filter { bean->bean.go_s_coun==coun }
        }

        val otherList= arrayListOf<VpnBean>()
        vpnMap.keys.forEach {
            val l = vpnMap[it]
            if (null!=l&&l.isNotEmpty()){
                if (l.size>1){
                    val filter = l.filter { bean -> !otherVpnList.contains(bean) }
                    if (filter.isNotEmpty()){
                        otherList.add(filter.random())
                    }
                }else{
                    otherList.add(l.first())
                }
            }
        }
        otherVpnList.clear()
        otherVpnList.addAll(otherList)
//        otherVpnList.forEach { logGo(it.toString()) }
    }
}