package com.vaca.esp32_android_ble

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

object DateStringUtil {
    fun timeConvert1(s: Long): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm").format(s)
    }

    fun timeConvert2(s: Long): String {
        return SimpleDateFormat("M-dd").format(s)
    }
    fun timeConvert22(s: Long): String {
        return SimpleDateFormat("MM-dd").format(s)
    }
    fun timeConvert2x1(s: Long): String {
        return SimpleDateFormat("HH").format(s)
    }

    fun timeConvert3(s: Long): String {
        return SimpleDateFormat("yyyy-MM-dd").format(s)
    }

    fun timeConvert3x1(s: Long): Long {
        val a= SimpleDateFormat("yyyy-MM-dd").format(s)
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        return sdf.parse(a).time+24*3600*1000L
    }

    fun timeConvert3x2(s: Long): Long {
        val a= SimpleDateFormat("yyyy-MM-dd").format(s)
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        return sdf.parse(a).time+24*3600*1000L
    }

    fun timeConvert4(s: Long): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(s)
    }

    fun timeConvert5(s: Long): String {
        return SimpleDateFormat("dd日 HH:mm").format(s)
    }
    fun timeConvert66(date: String): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val dd = sdf.parse(date)
        val gg=Date().time
        return gg>dd.time
    }
    fun timeConvert4ReverseX(date: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val dd = sdf.parse(date)
        val sdfx = SimpleDateFormat("yyyyMMddHHmmss")
        return sdfx.format(dd)
    }

    fun timeConvert4ReverseX2(date: String): String {
        val sdf = SimpleDateFormat("yyyyMMddHHmmss")
        val dd = sdf.parse(date)
        val sdfx = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return sdfx.format(dd)
    }

    fun timeConvert4ReverseX22(date: String): Long {
        val sdf = SimpleDateFormat("yyyyMMddHHmmss")
        val dd = sdf.parse(date)

        return dd.time
    }

    fun timeConvert4Reverse(date: String): Long {
        return Date(
            date.substring(0, 4).toInt() - 1900,
            date.substring(5, 7).toInt() - 1,
            date.substring(8, 10).toInt(),
            date.substring(11, 13).toInt(),
            date.substring(14, 16).toInt(),
            date.substring(17, 19).toInt()
        ).time
    }

    fun timeConvert4Reverse2(date: String): Long {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return sdf.parse(date).time
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertToLocalDateViaInstant(dateToConvert: Date): LocalDate? {
        return dateToConvert.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }

    fun secondToTime(second: Long): String {
        var sec = second
        sec %= 86400
        val hours = sec / 3600
        sec %= 3600
        val minutes = sec / 60
        sec %= 60
        if (hours.toInt() == 0) {
            return String.format(
                "%02d",
                minutes
            ) + ":" + String.format("%02d", sec)
        }
        return String.format("%02d", hours) + ":" + String.format(
            "%02d",
            minutes
        ) + ":" + String.format("%02d", sec)
    }
}