package com.example.smart_xe_gimble.xe

object XeResponse {

    fun getResponseBattery(b:ByteArray):Float{
        val bat=b[7].toUByte().toInt()*256+b[6].toUByte().toInt()
        return bat/10.0f
    }

}