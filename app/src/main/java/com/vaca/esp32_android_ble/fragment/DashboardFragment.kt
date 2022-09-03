package com.vaca.esp32_android_ble.fragment

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.vaca.esp32_android_ble.PathUtil
import com.vaca.esp32_android_ble.R
import com.vaca.esp32_android_ble.ble.BleServer
import com.vaca.esp32_android_ble.ble.BleServer.er2Graph
import com.vaca.esp32_android_ble.ble.wt02.blepower.BleCmd
import com.vaca.esp32_android_ble.databinding.FragmentGraphBinding




class DashboardFragment : Fragment() {

    private var _binding: FragmentGraphBinding? = null


    private val binding get() = _binding!!

    companion object{
        val currentCmd=MutableLiveData<String>()
    }




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentGraphBinding.inflate(inflater, container, false)


        currentCmd.observe(viewLifecycleOwner){
            binding.cmd.text=it
        }

        binding.x1.setOnClickListener {
            BleServer.ble_worker.sendCmd(BleCmd.activate(true))
        }

        binding.x11.setOnClickListener {
            BleServer.ble_worker.sendCmd(BleCmd.activate(false))
        }
        binding.x2.setOnClickListener {
            BleServer.ble_worker.sendCmd(BleCmd.syncData())
        }

        binding.x3.setOnClickListener {
            BleServer.ble_worker.sendCmd(BleCmd.clearData())
        }

        binding.x4.setOnClickListener {
            BleServer.ble_worker.sendCmd(BleCmd.enterTest())
        }

        binding.x5.setOnClickListener {
            BleServer.ble_worker.sendCmd(BleCmd.getBat())
        }

        binding.x6.setOnClickListener {
            BleServer.ble_worker.sendCmd(BleCmd.changeMode(1))
        }

        binding.x61.setOnClickListener {
            BleServer.ble_worker.sendCmd(BleCmd.changeMode(2))
        }

        binding.x62.setOnClickListener {
            BleServer.ble_worker.sendCmd(BleCmd.changeMode(3))
        }

        binding.x63.setOnClickListener {
            BleServer.ble_worker.sendCmd(BleCmd.changeMode(4))
        }


        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}