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
import androidx.navigation.fragment.findNavController
import com.vaca.esp32_android_ble.PathUtil
import com.vaca.esp32_android_ble.R
import com.vaca.esp32_android_ble.ble.BleServer
import com.vaca.esp32_android_ble.ble.BleServer.er2Graph
import com.vaca.esp32_android_ble.databinding.FragmentGraphBinding

import com.viatom.littlePu.er2.view.WaveView
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class GraphFragment : Fragment() {

    private var _binding: FragmentGraphBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentGraphBinding.inflate(inflater, container, false)

        er2Graph.postValue(true)
        er2Graph.observe(viewLifecycleOwner) {
            WaveView.disp = true
            binding.waveView.invalidate()
        }

        _binding!!.da.setOnClickListener {
            Toast(requireContext()).apply {
                val layout = inflater.inflate(R.layout.toast_layout, null)
                layout.findViewById<TextView>(R.id.dada).apply {
                    text = "原始文件已保存到Download文件夹"
                }
                setGravity(Gravity.CENTER, 0, 0)
                duration = Toast.LENGTH_LONG
                setView(layout)
                show()
            }
        }

        WaveView.nn.observe(viewLifecycleOwner){
            binding.peak.text="Peak Current: \n"+it
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

    fun fuck(view: View) {

    }
}