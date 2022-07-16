package com.vaca.esp32_android_ble.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

import com.vaca.esp32_android_ble.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {

   lateinit var binding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }


}