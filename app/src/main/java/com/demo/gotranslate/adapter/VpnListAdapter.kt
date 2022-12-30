package com.demo.gotranslate.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.demo.gotranslate.R
import com.demo.gotranslate.app.getVpnLogo
import com.demo.gotranslate.bean.VpnBean
import kotlinx.android.synthetic.main.item_vpn_list.view.*

class VpnListAdapter(
    private val context: Context,
    private val list:ArrayList<VpnBean>,
    private val click:(bean:VpnBean)->Unit
):RecyclerView.Adapter<VpnListAdapter.VpnView>() {

    inner class VpnView(view:View):RecyclerView.ViewHolder(view){
        init {
            view.setOnClickListener { click.invoke(list[layoutPosition]) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VpnView {
        return VpnView(LayoutInflater.from(context).inflate(R.layout.item_vpn_list,parent,false))
    }

    override fun onBindViewHolder(holder: VpnView, position: Int) {
        with(holder.itemView){
            val vpnBean = list[position]
            if(position==0){
                tv_vpn_name.text=vpnBean.go_s_city
                iv_vpn_logo.setImageResource(R.drawable.fast)
            }else{
                tv_vpn_name.text=vpnBean.go_s_coun+"--"+vpnBean.go_s_city
                iv_vpn_logo.setImageResource(getVpnLogo(vpnBean.go_s_coun))
            }
        }
    }

    override fun getItemCount(): Int = list.size
}