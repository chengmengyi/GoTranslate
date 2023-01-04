package com.demo.gotranslate.bean

import com.github.shadowsocks.database.Profile
import com.github.shadowsocks.database.ProfileManager

class VpnBean(
    var go_s_account:String="",
    var go_s_port:Int=0,
    var go_s_num:Int=0,
    var go_s_password:String="",
    var go_s_coun:String="Smart Server",
    var go_s_city:String="",
    var go_s_ip:String="",
    var isSmart:Boolean=false
) {

    fun getVpnId():Long{
        ProfileManager.getActiveProfiles()?.forEach {
            if (it.host==go_s_ip&&it.remotePort==go_s_port){
                return it.id
            }
        }
        return 0L
    }

    fun createVpn(){
        val profile = Profile(
            id = 0L,
            name = "${go_s_coun} - ${go_s_city}",
            host = go_s_ip,
            remotePort = go_s_port,
            password = go_s_password,
            method = go_s_account
        )

        var id: Long? = null
        ProfileManager.getActiveProfiles()?.forEach {
            if (it.remotePort == profile.remotePort && it.host == profile.host) {
                id = it.id
                return@forEach
            }
        }
        if (null == id) {
            ProfileManager.createProfile(profile)
        } else {
            profile.id = id!!
            ProfileManager.updateProfile(profile)
        }
    }

    override fun toString(): String {
        return "VpnBean(go_s_account='$go_s_account', go_s_port=$go_s_port, go_s_password='$go_s_password', go_s_coun='$go_s_coun', go_s_city='$go_s_city', go_s_ip='$go_s_ip', isSmart=$isSmart)"
    }
}