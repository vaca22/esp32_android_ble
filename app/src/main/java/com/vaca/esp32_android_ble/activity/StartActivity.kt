package com.vaca.esp32_android_ble.activity

import android.content.Intent
import android.os.Bundle
import android.view.Window
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity

import com.vaca.esp32_android_ble.databinding.ActivityStartBinding
import kotlinx.coroutines.*

class StartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MainScope().launch {
            delay(2000)
           // startActivity(Intent(this@StartActivity, MainActivity::class.java))
        }

    }
}