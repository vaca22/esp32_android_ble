package com.vaca.esp32_android_ble.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.vaca.esp32_android_ble.PathUtil
import com.vaca.esp32_android_ble.R
import com.vaca.esp32_android_ble.ble.BleServer
import com.vaca.esp32_android_ble.ble.er2.blepower.Er2BleDataWorker

import com.vaca.esp32_android_ble.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {

   lateinit var binding: FragmentSettingBinding

   companion object{
       val btColor=MutableLiveData<Int>()
   }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentSettingBinding.inflate(inflater, container, false)

        binding.start.setOnClickListener {
            Toast(requireContext()).apply {
                val layout = inflater.inflate(R.layout.toast_layout, null)
                layout.findViewById<TextView>(R.id.dada).apply {
                    text = "命令已发送"
                }
                setGravity(Gravity.CENTER, 0, 0)
                duration = Toast.LENGTH_LONG
                setView(layout)
                show()
            }
            btColor.postValue(1)

            Er2BleDataWorker.Vbias=binding.x1.text.toString()
            Er2BleDataWorker.Vlow=binding.x2.text.toString()
            Er2BleDataWorker.Vhigh=binding.x3.text.toString()
            Er2BleDataWorker.Vstep=binding.x4.text.toString()
            Er2BleDataWorker.Vpulse=binding.x5.text.toString()
            Er2BleDataWorker.Tstep=binding.x6.text.toString()
            BleServer.er2_worker.sendCmd("OKx".toByteArray())
        }

        btColor.observe(viewLifecycleOwner){
            if(it==1){
                binding.start.background=ContextCompat.getDrawable(requireContext(),R.drawable.bb2)
            }else{
                binding.start.background=ContextCompat.getDrawable(requireContext(),R.drawable.bb1)
            }
        }
        return binding.root
    }


}