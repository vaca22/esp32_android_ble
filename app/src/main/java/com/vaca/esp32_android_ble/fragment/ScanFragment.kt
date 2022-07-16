package com.vaca.esp32_android_ble.fragment

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        gaga.observe(viewLifecycleOwner){
            if(it){
                initScan()
            }
        }

        _binding =FragmentScanBinding.inflate(inflater, container, false)


        binding.bleTable.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)


        bleViewAdapter = BleViewAdapter(requireContext())
        binding.bleTable.adapter = bleViewAdapter
        bleViewAdapter.setClickListener(this)

        BleServer.bleState.observe(viewLifecycleOwner){
            //binding.state.text=it
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
        BleServer.connect(bluetoothDevice)
       // findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
    }

}