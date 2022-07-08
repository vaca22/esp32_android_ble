package com.vaca.esp32_android_ble.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.vaca.esp32_android_ble.R
import com.vaca.esp32_android_ble.ble.BleServer
import com.vaca.esp32_android_ble.ble.BleServer.er2Graph
import com.vaca.esp32_android_ble.databinding.FragmentSecondBinding
import com.viatom.littlePu.er2.view.WaveView
import java.util.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)


        BleServer.waveDataX.clear()
        BleServer.rtDataTask = WaveView.Companion.RtDataTask()
        Timer().schedule(BleServer.rtDataTask, Date(), 500)

        BleServer.drawTask = WaveView.Companion.DrawTask()
        Timer().schedule(BleServer.drawTask, Date(), 32)
        er2Graph.observe(viewLifecycleOwner, {
            binding.waveView.invalidate()
        })
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