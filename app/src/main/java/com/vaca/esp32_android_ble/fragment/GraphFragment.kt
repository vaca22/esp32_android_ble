package com.vaca.esp32_android_ble.fragment

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import com.vaca.esp32_android_ble.PathUtil
import com.vaca.esp32_android_ble.R
import com.vaca.esp32_android_ble.ble.BleServer
import com.vaca.esp32_android_ble.ble.BleServer.er2Graph
import com.vaca.esp32_android_ble.databinding.FragmentGraphBinding

import com.viatom.littlePu.er2.view.WaveView
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class GraphFragment : Fragment() {

    private var _binding: FragmentGraphBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentGraphBinding.inflate(inflater, container, false)

        er2Graph.postValue(true)
        er2Graph.observe(viewLifecycleOwner) {
            WaveView.disp = true
            binding.waveView.invalidate()
        }

        _binding!!.da.setOnClickListener {
            Toast(requireContext()).apply {
                val layout = inflater.inflate(R.layout.toast_layout, null)
                layout.findViewById<TextView>(R.id.dada).apply {
                    text = "原始文件已保存到"+PathUtil.getPathX("")
                }
                setGravity(Gravity.CENTER, 0, 0)
                duration = Toast.LENGTH_LONG
                setView(layout)
                show()
            }
        }

        WaveView.nn.observe(viewLifecycleOwner){
            binding.peak.text="Peak Current: \n"+it
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun fuck(view: View) {

    }
}