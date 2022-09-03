package com.vaca.esp32_android_ble.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vaca.esp32_android_ble.R


class PoctorTopAdapter(var context: Context) :
    RecyclerView.Adapter<PoctorTopAdapter.ViewHolder>() {


    var currentSelect = 0;

    interface Click {
        fun clickItem(position: Int)
    }

    var click: Click? = null

    private val mData: MutableList<String> = ArrayList()
    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    val name = listOf<String>("手动模式", "自动模式")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.item_poctor_top, parent, false)
        return ViewHolder(view)
    }

    fun addAll(userBean: ArrayList<String>) {
        mData.clear()
        for (k in userBean) {
            mData.add(k)
        }
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
         holder.buttonText.text=name[position]
        if (position == currentSelect) {
            holder.buttonText.background = ContextCompat.getDrawable(context, R.drawable.poctor_top_bg)
        } else {
            holder.buttonText.background=null
        }
    }


    override fun getItemCount(): Int {
        return 2
    }

    inner class ViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val buttonText: TextView =itemView.findViewById(R.id.button_text)

        init {

            itemView.setOnClickListener {
                currentSelect=layoutPosition
                click?.clickItem(layoutPosition)
                notifyDataSetChanged()
            }
        }
    }


}