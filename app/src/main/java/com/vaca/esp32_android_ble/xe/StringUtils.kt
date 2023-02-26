package com.example.smart_xe_gimble.xe

object StringUtils {
    private fun concatenateString(str: String, str2: String): String {
        return str + str2
    }

    fun byteArray2Hex(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (b in bytes) {
            var hexString = Integer.toHexString(b.toInt() and 255)
            if (hexString.length == 1) {
                hexString = concatenateString("0", hexString)
            }
            sb.append(hexString)
        }
        return sb.toString()
    }

}