package com.vaca.esp32_android_ble

import android.app.Application
import android.util.Log
import com.tencent.bugly.crashreport.CrashReport

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.io.path.Path
import kotlin.random.Random


class MainApplication : Application() {

    companion object {
        var mySn: String = "unknown"
        lateinit var application: Application
    }


    override fun onCreate() {
        super.onCreate()
        application = this
        PathUtil.initVar(this)
        CrashReport.initCrashReport(this, "8abda50a27", false);
    }


}