package com.vaca.esp32_android_ble

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

import com.vaca.esp32_android_ble.databinding.ActivityMainBinding
import com.vaca.esp32_android_ble.fragment.ScanFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val requestVoicePermission= registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            val yes=it.get(Manifest.permission.ACCESS_FINE_LOCATION)!!
            ScanFragment.gaga.postValue(yes)
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestVoicePermission.launch( arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
        }else{
            ScanFragment.gaga.postValue(true)
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        navView.setupWithNavController(navController)
    }




}