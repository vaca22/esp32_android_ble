package com.vaca.esp32_android_ble.ble

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.vaca.esp32_android_ble.R

import java.util.*

class BleViewAdapter(context: Context) : RecyclerView.Adapter<BleViewAdapter.ViewHolder>() {
    private val mBleData: MutableList<BleBean>
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var mClickListener: ItemClickListener? = null
    private val mContext: Context



    // inflates the cell layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.item_ble, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the TextView in each cell
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bleName.text = mBleData[position].name
    }

    fun addDevice(name: String?, bluetoothDevice: BluetoothDevice?) {
        mBleData.add(BleBean(name!!, bluetoothDevice!!))
        notifyItemChanged(mBleData.size-1)
    }

    // total number of cells
    override fun getItemCount(): Int {
        return mBleData.size
    }

    // allows clicks events to be caught
    fun setClickListener(itemClickListener: ItemClickListener?) {
        mClickListener = itemClickListener
    }

    interface ItemClickListener {
        fun onScanItemClick(bluetoothDevice: BluetoothDevice)
    }

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var dada:ConstraintLayout=itemView.findViewById(R.id.dada)
        var bleName: TextView = itemView.findViewById(R.id.ble_name)
        override fun onClick(view: View) {
            dada.visibility=View.VISIBLE
            //if (mClickListener != null) mClickListener!!.onScanItemClick(mBleData[adapterPosition].bluetoothDevice!!)
        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    // data is passed into the constructor
    init {
        mBleData = ArrayList()
        mContext = context
    }
}