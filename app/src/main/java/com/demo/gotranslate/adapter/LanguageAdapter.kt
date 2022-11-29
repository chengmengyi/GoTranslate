package com.demo.gotranslate.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.demo.gotranslate.R
import com.demo.gotranslate.app.logGo
import com.demo.gotranslate.app.netStatus
import com.demo.gotranslate.app.showToast
import com.demo.gotranslate.app.showView
import com.demo.gotranslate.bean.LanguageBean
import com.demo.gotranslate.manager.LanguageManager
import kotlinx.android.synthetic.main.item_language.view.*

class LanguageAdapter(
    private val context:Context,
    private val clickItem:(bean:LanguageBean)->Unit,
    private val deleteItem:(bean:LanguageBean)->Unit,
):RecyclerView.Adapter<LanguageAdapter.MyView>() {

    inner class MyView (view:View):RecyclerView.ViewHolder(view){
        private val ivDownload=view.findViewById<AppCompatImageView>(R.id.iv_download)
        init {
            ivDownload.setOnClickListener {
                if (context.netStatus()==1){
                    AlertDialog.Builder(context).apply {
                        setMessage("You are not currently connected to the network")
                        setPositiveButton("sure", null)
                        show()
                    }
                }else{
                    val languageBean = LanguageManager.allList[layoutPosition]
                    if(languageBean.type==0){
                        LanguageManager.download(languageBean){
                            notifyDataSetChanged()
                        }
                    }
                    if(languageBean.type==2){
                        LanguageManager.delete(languageBean){
                            if (it){
                                notifyDataSetChanged()
                                deleteItem.invoke(languageBean)
                            }else{
                                context.showToast("delete fail")
                            }
                        }
                    }
                }
            }
            view.setOnClickListener {
                clickItem.invoke(LanguageManager.allList[layoutPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyView {
        return MyView(LayoutInflater.from(context).inflate(R.layout.item_language,parent,false))
    }

    override fun onBindViewHolder(holder: MyView, position: Int) {
        with(holder.itemView){
            val languageBean = LanguageManager.allList[position]
            iv_logo.setImageResource(languageBean.icon)
            tv_name.text=languageBean.name
            iv_download.setImageResource(
                //0未下载 1下载中 2已下载
                when(languageBean.type){
                    0->R.drawable.download
                    1->R.drawable.downloading
                    2->R.drawable.delete
                    else->R.drawable.download
                }
            )
            iv_download.showView(languageBean.code!="en")
        }
    }

    override fun getItemCount(): Int = LanguageManager.allList.size
}