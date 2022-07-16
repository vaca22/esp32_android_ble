package com.vaca.esp32_android_ble.fragment

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.vaca.esp32_android_ble.ble.BleBean
import com.vaca.esp32_android_ble.ble.BleScanManager
import com.vaca.esp32_android_ble.ble.BleViewAdapter
import com.vaca.esp32_android_ble.R

import com.vaca.esp32_android_ble.ble.BleServer
import com.vaca.esp32_android_ble.databinding.FragmentScanBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ScanFragment : Fragment(), BleViewAdapter.ItemClickListener,   BleScanManager.Scan {


    companion object{
        val gaga=MutableLiveData<Boolean>()
    }


    private val bleList: MutableList<BleBean> = ArrayList()
    private var _binding: FragmentScanBinding? = null
    lateinit var bleViewAdapter: BleViewAdapter
    val scan = BleScanManager()
    var name=""

    private val binding get() = _binding!!
    lateinit var myInflater: LayoutInflater
    fun bindSet2(boolean: Boolean) {
        Toast(requireContext()).apply {
            val layout = myInflater.inflate(R.layout.toast_bind_layout, null)
            if (boolean) {
                layout.findViewById<TextView>(R.id.dada).apply {
                    text = "开启成功"
                }
                layout.findViewById<ImageView>(R.id.gr).setImageResource(R.drawable.success_icon)
            } else {
                layout.findViewById<TextView>(R.id.dada).apply {
                    text = "添加失败， 已有此成员"
                }
                layout.findViewById<ImageView>(R.id.gr).setImageResource(R.drawable.failure_icon)
            }
            setGravity(Gravity.CENTER, 0, 0)
            duration = Toast.LENGTH_SHORT
            setView(layout)
            show()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        gaga.observe(viewLifecycleOwner){
            if(it){
                initScan()
            }
        }

         myInflater=inflater
        _binding =FragmentScanBinding.inflate(inflater, container, false)


        binding.bleTable.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)


        bleViewAdapter = BleViewAdapter(requireContext())
        binding.bleTable.adapter = bleViewAdapter
        bleViewAdapter.setClickListener(this)

        BleServer.bleState.observe(viewLifecycleOwner){
            binding.status.text=it+" (${name})"
        }


        _binding!!.start.setOnClickListener {
            bindSet2(true)
            scan.start()
        }
        _binding!!.stop.setOnClickListener {
            scan.stop()
        }
        return binding.root

    }

    private fun initScan() {
        scan.initScan(requireContext())
        scan.setCallBack(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




    override fun scanReturn(name: String, bluetoothDevice: BluetoothDevice,addr:String,rssi:Int) {
//        if(bluetoothDevice.name.startsWith("DuoEK")==false){
//            return
//        }

        var z: Int = 0;
        for (ble in bleList) run {
            if (ble.name == bluetoothDevice.name) {
                z = 1
            }
        }
        if (z == 0) {
            bleList.add(BleBean(name, bluetoothDevice,addr,rssi))
            binding.found.text="已发现${bleList.size}个蓝牙设备"
            bleViewAdapter.addDevice(name, bluetoothDevice,addr,rssi)
        }
    }

    override fun onScanItemClick(bluetoothDevice: BluetoothDevice) {
        scan.stop()
        name=bluetoothDevice.name
        Log.e("gaga",name)
        BleServer.connect(bluetoothDevice)
       // findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
    }

}