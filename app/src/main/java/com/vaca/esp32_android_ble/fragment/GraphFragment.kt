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



/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class GraphFragment : Fragment() {

    private var _binding: FragmentGraphBinding? = null


    private val binding get() = _binding!!




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentGraphBinding.inflate(inflater, container, false)



        binding.x1.setOnClickListener {

        }

        binding.x11.setOnClickListener {

        }
        binding.x2.setOnClickListener {

        }

        binding.x3.setOnClickListener {

        }

        binding.x4.setOnClickListener {

        }

        binding.x5.setOnClickListener {

        }

        binding.x6.setOnClickListener {

        }

        binding.x61.setOnClickListener {

        }

        binding.x62.setOnClickListener {

        }

        binding.x63.setOnClickListener {

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