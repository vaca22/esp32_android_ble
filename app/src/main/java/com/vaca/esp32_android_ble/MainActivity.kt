package com.vaca.esp32_android_ble

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.vaca.esp32_android_ble.databinding.ActivityMainBinding
import com.vaca.esp32_android_ble.fragment.FirstFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val requestVoicePermission= registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            val yes=it.get(Manifest.permission.ACCESS_FINE_LOCATION)!!
            FirstFragment.haveBlePrepare.postValue(yes)
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestVoicePermission.launch( arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_PRIVILEGED,

            ))
        }else{
            FirstFragment.haveBlePrepare.postValue(true)
        }


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }






}