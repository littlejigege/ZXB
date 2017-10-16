package com.qg.zxb.model

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import com.mobile.utils.gone
import com.mobile.utils.visiable
import com.qg.zxb.R
import kotlinx.android.synthetic.main.msg_item.view.*

/**
 * Created by jimji on 2017/10/16.
 */
class MsgAdapter(ctx: Context, val layoutId: Int, datas: List<Msg>) : ArrayAdapter<Msg>(ctx, layoutId, datas) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: ViewHolder
        if (convertView == null) {
            holder = ViewHolder(LayoutInflater.from(context).inflate(layoutId, parent, false))
            holder.view.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }
        if (getItem(position).from == 0) {
            holder.view.msgFromHC.gone()
            holder.view.msgFromMe.visiable()
            holder.view.msgFromMe.text = getItem(position).content
        } else {
            holder.view.msgFromMe.gone()
            holder.view.msgFromHC.visiable()
            holder.view.msgFromHC.text = getItem(position).content
        }
        return holder.view
    }

    inner class ViewHolder(var view: View)


}