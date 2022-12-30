package com.demo.gotranslate.config

import com.demo.gotranslate.bean.VpnBean

object GoConfig {
    const val EMAIL=""
    const val WEB=""

    const val GO_OPEN="go_open"
    const val GO_HOME="go_home"
    const val GO_WRITE_HOME="go_write_home"
    const val GO_TRANSLATE="go_translate"
    const val GO_VPN_HOME="go_vpn_home"
    const val GO_VPN_RESULT="go_vpn_result"
    const val GO_VPN_CONN="go_vpn_conn"
    const val GO_VPN_BACK="go_vpn_back"
    const val GO_VPN_RESULT_BACK="go_vpn_resultbackhome"
    const val GO_VPN_LIST="go_vpn_serlist"

    val localCountryList= arrayOf("Japan")

    val localVpnList=arrayListOf(
        VpnBean(
            go_s_account = "chacha20-ietf-poly1305",
            go_s_port = 100,
            go_s_password = "123456",
            go_s_coun = "Japan",
            go_s_city = "Tokyo1",
            go_s_ip = "100.223.52.0"
        ),
        VpnBean(
            go_s_account = "chacha20-ietf-poly1305",
            go_s_port = 100,
            go_s_password = "123456",
            go_s_coun = "Japan",
            go_s_city = "Tokyo2",
            go_s_ip = "100.223.52.0"
        ),
        VpnBean(
            go_s_account = "chacha20-ietf-poly1305",
            go_s_port = 100,
            go_s_password = "123456",
            go_s_coun = "Japan",
            go_s_city = "Tokyo3",
            go_s_ip = "100.223.52.0"
        ),
        VpnBean(
            go_s_account = "chacha20-ietf-poly1305",
            go_s_port = 100,
            go_s_password = "123456",
            go_s_coun = "UnitedStates",
            go_s_city = "Tokyo",
            go_s_ip = "100.223.52.78"
        ),
    )

    const val localAdConfig="""{
    "go_max_click":15,
    "go_max_show":50,
    "go_open": [
        {
            "go_source": "admob",
            "go_id": "ca-app-pub-3940256099942544/3419835294",
            "go_type": "kaiping",
            "go_sort": 1
        },

{

            "go_source": "admob",
            "go_id": "ca-app-pub-3940256099942544/1001001001",
            "go_type": "kaiping",
            "go_sort": 3
},
        {
            "go_source": "admob",
            "go_id": "ca-app-pub-3940256099942544/3419835294A",
            "go_type": "kaiping",
            "go_sort": 2
        }
    ],
    "go_home": [
        {
            "go_source": "admob",
            "go_id": "ca-app-pub-3940256099942544/2247696110",
            "go_type": "yuansheng",
            "go_sort": 2
        }
    ],
     "go_vpn_home": [
        {
            "go_source": "admob",
            "go_id": "ca-app-pub-3940256099942544/2247696110",
            "go_type": "yuansheng",
            "go_sort": 2
        }
    ],
     "go_vpn_result": [
        {
            "go_source": "admob",
            "go_id": "ca-app-pub-3940256099942544/2247696110",
            "go_type": "yuansheng",
            "go_sort": 2
        }
    ],
      "go_vpn_serlist": [
        {
            "go_source": "admob",
            "go_id": "ca-app-pub-3940256099942544/2247696110",
            "go_type": "yuansheng",
            "go_sort": 2
        }
    ],
      "go_vpn_conn": [
        {
            "go_source": "admob",
            "go_id": "ca-app-pub-3940256099942544/8691691433",
            "go_type": "chaping",
            "go_sort": 2
        }
    ],
     "go_vpn_resultbackhome": [
        {
            "go_source": "admob",
            "go_id": "ca-app-pub-3940256099942544/8691691433",
            "go_type": "chaping",
            "go_sort": 2
        }
    ],
    "go_vpn_back": [
        {
            "go_source": "admob",
            "go_id": "ca-app-pub-3940256099942544/8691691433",
            "go_type": "chaping",
            "go_sort": 2
        }
    ],
    "go_write_home": [
        {
            "go_source": "admob",
            "go_id": "ca-app-pub-3940256099942544/2247696110",
            "go_type": "yuansheng",
            "go_sort": 1
        },

 {
            "go_source": "admob",
            "go_id": "ca-app-pub-3940256099942544/1001001001",
            "go_type": "yuansheng",
            "go_sort": 3
        },
        {
            "go_source": "admob",
            "go_id": "ca-app-pub-3940256099942544/2247696110A",
            "go_type": "yuansheng",
            "go_sort": 2
        }
    ],
      "go_translate": [
        {
            "go_source": "admob",
            "go_id": "ca-app-pub-3940256099942544/8691691433",
            "go_type": "chaping",
            "go_sort": 2
        },
         {
            "go_source": "admob",
            "go_id": "ca-app-pub-3940256099942544/8691691433",
            "go_type": "chaping",
            "go_sort": 1
        },
         {
            "go_source": "admob",
            "go_id": "ca-app-pub-3940256099942544/8691691433A",
            "go_type": "chaping",
            "go_sort": 3
        }
    ]
}"""
}