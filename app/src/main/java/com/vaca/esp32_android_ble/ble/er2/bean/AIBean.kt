package com.vaca.esp32_android_ble.ble.er2.bean

data class AIBean(
    val devSN: String,
    val devType: String,
    val duration: String,
    var fileUrl: String,
    var measureTime: String,
    var member: String = "",
    var txtFileUrl: String = ""
)