package com.vaca.esp32_android_ble.activity

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.vaca.esp32_android_ble.MainActivity
import com.vaca.esp32_android_ble.databinding.ActivityStartBinding

class StartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartBinding
    private var requestBluetooth =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                //granted
            } else {
                //deny
            }
        }

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                Log.e("test006", "${it.key} = ${it.value}")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val requestVoicePermission = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            if ((ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED)
            ) {
                Log.e("gaga", "gaga1")

                requestVoicePermission.launch(
                    arrayOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT
                    )
                )


            } else {
                Log.e("gaga", "gaga122")
            }
        }

        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestPermission()

        binding.aa.setOnClickListener {
            val intent = Intent(this@StartActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            this.finish()
        }

    }

    private val permissionRequestCode = 521
    private fun checkP(p: String): Boolean {
        return ContextCompat.checkSelfPermission(this, p) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        val ps: Array<String> = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (!checkP(Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this, ps, permissionRequestCode)
            return
        } else {
            initA()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            permissionRequestCode -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initA()
            } else {
                startActivity(Intent(this, MainActivity::class.java))
                this.finish()
            }
            else -> {
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun initA() {
        if (!isLocationEnabled()) {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(intent, REQUEST_LOCATION)
        } else {
            initB()
        }
    }

    fun initB() {
        val bluetoothManager =
            getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        if (bluetoothAdapter == null) {
            val enableBtIntent = Intent(
                BluetoothAdapter.ACTION_REQUEST_ENABLE
            )
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            return;
        }
        if (!(bluetoothAdapter.isEnabled)) {
            val enableBtIntent = Intent(
                BluetoothAdapter.ACTION_REQUEST_ENABLE
            )
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            return;
        }
        startActivity(Intent(this, MainActivity::class.java))
        this.finish()
    }

    private fun isLocationEnabled(): Boolean {
        val lm = getSystemService(LOCATION_SERVICE) as LocationManager
        var gps_enabled = false
        var network_enabled = false

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
        }
        return (gps_enabled || network_enabled)
    }

    private val REQUEST_LOCATION = 223
    private val REQUEST_ENABLE_BT = 224
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_LOCATION) {
            initB()
        } else if (requestCode == REQUEST_ENABLE_BT) {
            initB()
        }
    }
}