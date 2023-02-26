package com.vaca.esp32_android_ble.fragment

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.smart_xe_gimble.xe.XeBleCmd
import com.example.smart_xe_gimble.xe.XeBleManager
import com.example.smart_xe_gimble.xe.XeBleUtils
import com.vaca.esp32_android_ble.MainApplication
import com.vaca.esp32_android_ble.bean.BleBean
import com.vaca.esp32_android_ble.esp32ble.Esp32BleScanManager
import com.vaca.esp32_android_ble.adapter.BleViewAdapter
import com.vaca.esp32_android_ble.R
import com.vaca.esp32_android_ble.esp32ble.BleServer
import com.vaca.esp32_android_ble.databinding.FragmentFirstBinding
import com.vaca.esp32_android_ble.view.JoystickView
import com.vaca.esp32_android_ble.xeble.XeBleDataManager
import no.nordicsemi.android.ble.callback.FailCallback
import no.nordicsemi.android.ble.data.Data
import java.lang.reflect.InvocationTargetException

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment(), BleViewAdapter.ItemClickListener,   Esp32BleScanManager.Scan {


    companion object{
        val haveBlePrepare=MutableLiveData<Boolean>()
    }


    private val bleList: MutableList<BleBean> = ArrayList()
    private var _binding: FragmentFirstBinding? = null
    lateinit var bleViewAdapter: BleViewAdapter
    val scan = Esp32BleScanManager()
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    fun getXeBtDevice(): String? {
        //获取蓝牙适配器
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        //得到已匹配的蓝牙设备列表
        val bondedDevices = bluetoothAdapter.bondedDevices
        if (bondedDevices != null && bondedDevices.size > 0) {
            for (bondedDevice in bondedDevices) {
                try {
                    //使用反射调用被隐藏的方法
                    val isConnectedMethod =
                        BluetoothDevice::class.java.getDeclaredMethod(
                            "isConnected"
                        )
                    isConnectedMethod.isAccessible = true
                    val isConnected =
                        isConnectedMethod.invoke(bondedDevice) as Boolean
                    if (isConnected) {
                        if(bondedDevice.name.equals("SMART_XE")){
                            Log.e("fuck","device yes")
                            XeBleUtils.bluetoothDevice = bondedDevice
                        }
                        return bondedDevice.name
                    }
                } catch (e: NoSuchMethodException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: InvocationTargetException) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        haveBlePrepare.observe(viewLifecycleOwner){
            if(it){
                initGimbal()
                initButton()
            }
        }

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
//        binding.bleTable.layoutManager = GridLayoutManager(requireContext(), 2);
//        bleViewAdapter = BleViewAdapter(requireContext())
//        binding.bleTable.adapter = bleViewAdapter
//        bleViewAdapter.setClickListener(this)



        return binding.root

    }
    
    lateinit var xeBleDataManager: XeBleDataManager
    
    fun writeData(b:ByteArray){
        xeBleDataManager.sendCmd(b)
    }
    
    fun initButton(){
        binding.button.setOnClickListener {
            writeData(XeBleCmd.getBatteryLevel())
        }
        binding.button2.setOnClickListener {
            val cmd = XeBleCmd.followCenterCmd(-2000,20,10)
            Log.e("fuck","cmd leng "+cmd.size)
            writeData(cmd)
        }
        binding.button3.setOnClickListener {
            val cmd = XeBleCmd.shakeHand2()
            Log.e("fuck","cmd leng "+cmd.size)
            writeData(cmd)
        }
        binding.button4.setOnClickListener {
            val cmd = XeBleCmd.shakeHand3()
            Log.e("fuck","cmd leng "+cmd.size)
            writeData(cmd)
        }
        binding.button5.setOnClickListener {
            val cmd = XeBleCmd.shakeHand4()
            Log.e("fuck","cmd leng "+cmd.size)
            writeData(cmd)
        }
        binding.button6.setOnClickListener {
            val cmd = XeBleCmd.mode1Cmd()
            Log.e("fuck","cmd leng "+cmd.size)
            writeData(cmd)
        }
        binding.button7.setOnClickListener {
            val cmd = XeBleCmd.attitudeOffsetReadCmd()
            Log.e("fuck","cmd leng "+cmd.size)
            writeData(cmd)
        }
        binding.button8.setOnClickListener {
            val cmd = XeBleCmd.readControlSettingCmd()
            Log.e("fuck","cmd leng "+cmd.size)
            writeData(cmd)
        }
        binding.button9.setOnClickListener {
            val cmd = XeBleCmd.getSystemStatus()
            Log.e("fuck","cmd leng "+cmd.size)
            writeData(cmd)
        }

        binding.button10.setOnClickListener {
            val cmd = XeBleCmd.returnCenter(true)
            Log.e("fuck","cmd leng "+cmd.size)
            writeData(cmd)
        }

        binding.ga1.setOnJoystickMoveListener(object: JoystickView.OnJoystickMoveListener {
            override fun onValueChanged(angle: Int, power: Int, direction: Int) {
                Log.e("fuck","$angle $power $direction")
                val tAngle = -angle+90
                val x = (power * Math.cos(Math.toRadians(tAngle.toDouble()))).toInt()
                val y = (power * Math.sin(Math.toRadians(tAngle.toDouble()))).toInt()
                Log.e("gaga","$x $y")
                val cmd = XeBleCmd.followCenterCmd(x,y,2000)
                Log.e("fuck","cmd leng "+cmd.size)
                writeData(cmd)

            }

        }, 100)

    }
    private fun initGimbal(){
        getXeBtDevice()
        xeBleDataManager = XeBleDataManager(requireActivity())
        xeBleDataManager.setNotifyListener(object:XeBleDataManager.OnNotifyListener{
            override fun onNotify(device: BluetoothDevice?, data: Data?) {
                data?.let { 
                    Log.e("fuck", it.value?.size.toString())
                }
            }

        })
        xeBleDataManager.connect(XeBleUtils.bluetoothDevice)
            .useAutoConnect(true)
            ?.timeout(10000)
            ?.retry(10, 200)
            ?.done {
                Log.i("fuck", "XE连接成功了.>>.....>>>>")
            }?.fail(object : FailCallback {
                override fun onRequestFailed(device: BluetoothDevice, status: Int) {
                    Log.i("fuck", "XE连接失败了.>>.....>>>> $status")
                }

            })
            ?.enqueue()
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