package com.vaca.esp32_android_ble.adapter

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vaca.esp32_android_ble.R
import com.vaca.esp32_android_ble.ble.BleBean

import kotlin.collections.ArrayList

class BleViewAdapter(context: Context) : RecyclerView.Adapter<BleViewAdapter.ViewHolder>() {
    private val mBleData: MutableList<BleBean>
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var mClickListener: ItemClickListener? = null
    private val mContext: Context

    var bleLock=false;



    // inflates the cell layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.item_ble, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the TextView in each cell
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bleName.text = mBleData[position].name
        if(mBleData[position].span){
            holder.dada.visibility=View.VISIBLE
            holder.bleName.background=ContextCompat.getDrawable(mContext,R.drawable.bb2)
        }else{
            holder.dada.visibility=View.GONE
            holder.bleName.background=null
        }
        val gaga=mBleData[position]
        holder.rssi.text="rssi: "+gaga.rssi.toString()
        holder.info.text="信号强度："+gaga.rssi.toString()+"\n"+"MAC地址："+gaga.addr
    }

    fun addDevice(name: String?, bluetoothDevice: BluetoothDevice?,mac:String,rssi:Int) {
        mBleData.add(BleBean(name!!, bluetoothDevice!!,mac,rssi))


        notifyDataSetChanged()


    }

    fun addAll(a:List<BleBean>) {
        mBleData.clear()
        for(k in a){
            mBleData.add(k)
        }

        notifyDataSetChanged()


    }


    override fun getItemCount(): Int {
        return mBleData.size
    }

    fun setClickListener(itemClickListener: ItemClickListener?) {
        mClickListener = itemClickListener
    }

    fun setSpan(position: Int){
        if(mBleData[position].span==true){
            for(k in mBleData.indices){
                mBleData[k].span =false
            }
        }else{
            for(k in mBleData.indices){
                mBleData[k].span = k==position
            }
        }

        notifyDataSetChanged()
    }


    interface ItemClickListener {
        fun onScanItemClick(bluetoothDevice: BluetoothDevice)
    }

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var dada:ConstraintLayout=itemView.findViewById(R.id.dada)
        var bleName: TextView = itemView.findViewById(R.id.ble_name)
        val info:TextView=itemView.findViewById(R.id.info)
        val yes:TextView=itemView.findViewById(R.id.yes)
        val rssi:TextView=itemView.findViewById(R.id.rssi)
        override fun onClick(view: View) {
            setSpan(layoutPosition)
            dada.visibility=View.VISIBLE

        }

        init {
            itemView.setOnClickListener(this)
            yes.setOnClickListener {
                if (mClickListener != null) mClickListener!!.onScanItemClick(mBleData[adapterPosition].bluetoothDevice!!)
            }
        }
    }

    // data is passed into the constructor
    init {
        mBleData = ArrayList()
        mContext = context
    }
}