package com.vaca.esp32_android_ble.fragment

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vaca.esp32_android_ble.ble.BleBean
import com.vaca.esp32_android_ble.ble.BleScanManager
import com.vaca.esp32_android_ble.adapter.BleViewAdapter
import com.vaca.esp32_android_ble.R
import com.vaca.esp32_android_ble.adapter.PoctorTopAdapter

import com.vaca.esp32_android_ble.ble.BleServer
import com.vaca.esp32_android_ble.databinding.FragmentScanBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ScanFragment : Fragment(), BleViewAdapter.ItemClickListener,   BleScanManager.Scan {


    companion object{
        val gaga=MutableLiveData<Boolean>()
        var filterNamex=""
        var filterRssi=0;
    }


    private lateinit var topAdapter: PoctorTopAdapter
    private val bleList: MutableList<BleBean> = ArrayList()
    private val displayList= MutableLiveData<List<BleBean>>()
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

        topAdapter = PoctorTopAdapter(requireContext())


        topAdapter.click=object :PoctorTopAdapter.Click{
            override fun clickItem(position: Int) {
                Log.e("gaf",position.toString())

            }
        }

        binding.topView.layoutManager =
            object : LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }
            }
        binding.topView.adapter = topAdapter
        binding.bleTable.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)


        bleViewAdapter = BleViewAdapter(requireContext())

        binding.bleTable.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if(newState==1){
                    bleViewAdapter.bleLock=true
                }else if(newState==0){

                        bleViewAdapter.bleLock=false


                }

            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }
        })




        binding.bleTable.adapter = bleViewAdapter
        bleViewAdapter.setClickListener(this)

        BleServer.bleState.observe(viewLifecycleOwner){
            binding.status.text=it+" (${name})"
        }


        binding.start.setOnClickListener {

            scan.start()
            binding.start.text="扫描中"

            BleServer.dataScope.launch {
                delay(10000)
                scan.stop()
                withContext(Dispatchers.Main){
                    binding.start.text="开始扫描"
                }
            }
        }



        displayList.observe(viewLifecycleOwner){
            bleViewAdapter.addAll(it)
        }




        binding.name.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                val a=binding.name.text.toString()
                filterNamex= a.toString()
                val bleList2: ArrayList<BleBean> = ArrayList()
                for(gg in bleList){
                    if(gg.name.contains(a)){
                        bleList2.add(gg)
                    }
                }
                displayList.postValue(bleList2)

            }

        })




        binding.rssi.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                val a=binding.rssi.text.toString()
                try {
                    val b=a.toInt()
                    filterRssi=b;
                    val bleList2: ArrayList<BleBean> = ArrayList()
                    for(gg in bleList){
                        if(gg.rssi>=b){
                            bleList2.add(gg)
                        }
                    }
                    displayList.postValue(bleList2)

                }catch (e:Exception){
                    filterRssi=0;
                    displayList.postValue(bleList)
                }
            }

        })








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

        var z: Int = 0;
        for (ble in bleList) run {
            if (ble.name == bluetoothDevice.name) {
                z = 1
            }
        }
        if (z == 0) {
            try {
                bleList.add(BleBean(name, bluetoothDevice,addr,rssi))
                var canAdd=true;
                if(filterNamex.isNotEmpty()){
                    if(name.contains(filterNamex)==false){
                        canAdd=false
                    }
                }
                if(filterRssi!=0){
                    if(rssi< filterRssi){
                        canAdd=false
                    }
                }
                if(canAdd){
                    bleViewAdapter.addDevice(name, bluetoothDevice,addr,rssi)
                }

            }catch (e:Exception){

            }

        }
    }

    override fun onScanItemClick(bluetoothDevice: BluetoothDevice) {
        scan.stop()
        name=bluetoothDevice.name
        BleServer.connect(bluetoothDevice)
    }

}