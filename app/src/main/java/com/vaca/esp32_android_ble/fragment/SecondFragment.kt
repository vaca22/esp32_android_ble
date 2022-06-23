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
            val x=JSONObject()
            x.put("x1",binding.x1.text.toString())
            x.put("x2",binding.x2.text.toString())
            x.put("x3",binding.x3.text.toString())
            x.put("x4",binding.x4.text.toString())
            x.put("x5",binding.x5.text.toString())


            val content=x.toString()

            try {
                BleServer.worker.sendCmd(content.toByteArray())
            }catch (e:Exception){

            }

            Log.e("good","uyes"+content+"   "+content.length.toString())
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