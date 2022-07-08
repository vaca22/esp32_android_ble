package com.vaca.esp32_android_ble.fragment

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.vaca.esp32_android_ble.BleBean
import com.vaca.esp32_android_ble.BleScanManager
import com.vaca.esp32_android_ble.BleViewAdapter
import com.vaca.esp32_android_ble.R
import com.vaca.esp32_android_ble.bean.HavePermission
import com.vaca.esp32_android_ble.ble.BleServer
import com.vaca.esp32_android_ble.databinding.FragmentFirstBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

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
        if(bluetoothDevice.name.startsWith("DuoEK")==false){
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

    override fun onScanItemClick(bluetoothDevice: BluetoothDevice) {
        scan.stop()
        BleServer.connect(bluetoothDevice)
        findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
    }

}