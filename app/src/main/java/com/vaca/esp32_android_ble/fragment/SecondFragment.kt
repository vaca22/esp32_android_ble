package com.vaca.esp32_android_ble.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import com.vaca.esp32_android_ble.esp32ble.BleServer
import com.vaca.esp32_android_ble.databinding.FragmentSecondBinding
import java.util.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object{
        val bleStu=MutableLiveData<String>()
    }

    override fun onStop() {
        BleServer.worker.disconnect()
        super.onStop()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
      //  Timer().schedule(carRunTask, Date(),50)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val carRunTask=RtDataTask()

    inner class RtDataTask() : TimerTask() {
        override fun run() {
            val bb=ByteArray(12){
                0x32.toByte()
            }

            var cc=binding.seekBar1.progress;
            Log.e("fuck",cc.toString())
            bb[0]=(cc%256).toByte()
            bb[1]=(cc/256).toByte()

            cc=binding.seekBar2.progress;
            bb[2]=(cc%256).toByte()
            bb[3]=(cc/256).toByte()


            cc=binding.seekBar3.progress;
            bb[4]=(cc%256).toByte()
            bb[5]=(cc/256).toByte()


            cc=binding.seekBar4.progress;
            bb[6]=(cc%256).toByte()
            bb[7]=(cc/256).toByte()


            cc=binding.seekBar5.progress;
            bb[8]=(cc%256).toByte()
            bb[9]=(cc/256).toByte()

            cc=binding.seekBar5.progress;
            bb[10]=(cc%256).toByte()
            bb[11]=(cc/256).toByte()

            BleServer.worker.sendCmd(bb)
        }
    }
}