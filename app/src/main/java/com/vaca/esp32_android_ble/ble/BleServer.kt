package com.vaca.esp32_android_ble.ble

import android.bluetooth.BluetoothDevice
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import com.vaca.esp32_android_ble.MainApplication
import com.vaca.esp32_android_ble.ble.er2.blepower.Er2BleDataWorker

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.util.*

object BleServer {
    val dataScope = CoroutineScope(Dispatchers.IO)
    val scan = BleScanManager()
    val er2_worker: Er2BleDataWorker = Er2BleDataWorker()
    var er2ConnectFlag = false
    val waveDataX = LinkedList<Float>()
    var textInfo="";
    var textTotal=MutableLiveData<String>()
    val bleState=MutableLiveData<String>()


    val er2Graph = MutableLiveData<Boolean>()

   fun connect(b: BluetoothDevice){
       er2_worker.initWorker(MainApplication.application,b)
   }

    private val DOWNLOAD_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)



    fun copyFileToDownloads(context: Context, downloadedFile: File): Uri? {
        val resolver = context.contentResolver
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, downloadedFile.name)
                put(MediaStore.MediaColumns.MIME_TYPE, downloadedFile.extension)
                put(MediaStore.MediaColumns.SIZE, downloadedFile.usableSpace)
            }
            resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        } else {
            val authority = "${context.packageName}.provider"
            val destinyFile = File(DOWNLOAD_DIR, (downloadedFile).name)
            FileProvider.getUriForFile(context, authority, destinyFile)
        }?.also { downloadedUri ->
            resolver.openOutputStream(downloadedUri).use { outputStream ->
                val brr = ByteArray(1024)
                var len: Int
                val bufferedInputStream = BufferedInputStream(FileInputStream(downloadedFile.absoluteFile))
                while ((bufferedInputStream.read(brr, 0, brr.size).also { len = it }) != -1) {
                    outputStream?.write(brr, 0, len)
                }
                outputStream?.flush()
                bufferedInputStream.close()
            }
        }
    }

}
