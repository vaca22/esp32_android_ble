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
import com.vaca.esp32_android_ble.MainApplication
import com.vaca.esp32_android_ble.bean.BleBean
import com.vaca.esp32_android_ble.ble.BleScanManager
import com.vaca.esp32_android_ble.adapter.BleViewAdapter
import com.vaca.esp32_android_ble.R
import com.vaca.esp32_android_ble.ble.BleServer
import com.vaca.esp32_android_ble.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment(), BleViewAdapter.ItemClickListener,   BleScanManager.Scan {


    companion object{
        val gaga=MutableLiveData<Boolean>()
    }


    private val bleList: MutableList<BleBean> = ArrayList()
    private var _binding: FragmentFirstBinding? = null
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

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        binding.bleTable.layoutManager = GridLayoutManager(requireContext(), 2);
        bleViewAdapter = BleViewAdapter(requireContext())
        binding.bleTable.adapter = bleViewAdapter
        bleViewAdapter.setClickListener(this)



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




    override fun scanReturn(name: String, bluetoothDevice: BluetoothDevice) {
        if(name.contains("lgh")==false){
            return
        }

        var z: Int = 0;
        for (ble in bleList) run {
            if (ble.name == bluetoothDevice.name) {
                z = 1
            }
        }
        if (z == 0) {
            bleList.add(BleBean(name, bluetoothDevice))
            bleViewAdapter.addDevice(name, bluetoothDevice)
        }
    }

    override fun onScanItemClick(bluetoothDevice: BluetoothDevice?) {
        scan.stop()
        BleServer.worker.initWorker(MainApplication.application,bluetoothDevice)
        findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
    }

}