package com.vaca.esp32_android_ble.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.vaca.esp32_android_ble.ble.BleServer
import com.vaca.esp32_android_ble.ble.er2.blepower.Er2BleDataWorker

import com.vaca.esp32_android_ble.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {

   lateinit var binding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentSettingBinding.inflate(inflater, container, false)

        binding.start.setOnClickListener {
            Er2BleDataWorker.Vbias=binding.x1.text.toString()
            Er2BleDataWorker.Vlow=binding.x2.text.toString()
            Er2BleDataWorker.Vhigh=binding.x3.text.toString()
            Er2BleDataWorker.Vstep=binding.x4.text.toString()
            Er2BleDataWorker.Vpulse=binding.x5.text.toString()
            Er2BleDataWorker.Tstep=binding.x6.text.toString()
            BleServer.er2_worker.sendCmd("OKx".toByteArray())
        }
        return binding.root
    }


}