package com.vaca.esp32_android_ble.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.vaca.esp32_android_ble.R
import com.vaca.esp32_android_ble.ble.BleServer
import com.vaca.esp32_android_ble.databinding.FragmentSecondBinding
import org.json.JSONObject
import java.lang.Exception

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


        bleStu.observe(viewLifecycleOwner){
            binding.bleStatus.text=it
        }

        binding.upload.setOnClickListener {
           val aa=binding.x1.text.toString().toInt();
            val cc=ByteArray(3){
                50.toByte()
            }

            cc[1]= (aa%256).toByte();
            cc[2]= (aa/256).toByte();

            try {
                BleServer.worker.sendCmd(cc)
            }catch (e:Exception){

            }

        }

        binding.open.setOnClickListener{
            val cc=ByteArray(2){
                48.toByte()
            }
            cc[1]=48.toByte();
            try {
                BleServer.worker.sendCmd(cc)
            }catch (e:Exception){

            }
        }


        binding.close.setOnClickListener{
            val cc=ByteArray(2){
                48.toByte()
            }
            cc[1]=49.toByte();
            try {
                BleServer.worker.sendCmd(cc)
            }catch (e:Exception){

            }
        }

        binding.pause.setOnClickListener{
            val cc=ByteArray(2){
                48.toByte()
            }
            cc[1]=50.toByte();
            try {
                BleServer.worker.sendCmd(cc)
            }catch (e:Exception){

            }
        }


        binding.mode1.setOnClickListener{
            val cc=ByteArray(2){
                49.toByte()
            }
            cc[1]=49.toByte();
            try {
                BleServer.worker.sendCmd(cc)
            }catch (e:Exception){

            }
        }


        binding.mode2.setOnClickListener {
            val cc=ByteArray(2){
                49.toByte()
            }
            cc[1]=48.toByte();
            try {
                BleServer.worker.sendCmd(cc)
            }catch (e:Exception){

            }
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
}