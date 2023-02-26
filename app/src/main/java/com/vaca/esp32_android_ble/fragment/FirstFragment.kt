package com.vaca.esp32_android_ble.fragment

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.example.smart_xe_gimble.xe.XeBleCmd
import com.example.smart_xe_gimble.xe.XeBleManager.dataScope
import com.example.smart_xe_gimble.xe.XeBleUtils
import com.vaca.esp32_android_ble.MainApplication
import com.vaca.esp32_android_ble.bean.BleBean
import com.vaca.esp32_android_ble.esp32ble.Esp32BleScanManager
import com.vaca.esp32_android_ble.adapter.BleViewAdapter
import com.vaca.esp32_android_ble.R
import com.vaca.esp32_android_ble.esp32ble.BleServer
import com.vaca.esp32_android_ble.databinding.FragmentFirstBinding
import com.vaca.esp32_android_ble.esp32ble.Esp32BleDataManager
import com.vaca.esp32_android_ble.view.JoystickView
import com.vaca.esp32_android_ble.xeble.XeBleDataManager
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import no.nordicsemi.android.ble.callback.FailCallback
import no.nordicsemi.android.ble.data.Data
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.*
import org.webrtc.audio.JavaAudioDeviceModule
import java.lang.reflect.InvocationTargetException
import java.net.URISyntaxException
import java.nio.ByteBuffer
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment(), BleViewAdapter.ItemClickListener,   Esp32BleScanManager.Scan {

    lateinit var mEglBase: EglBase
    companion object{
        val haveBlePrepare=MutableLiveData<Boolean>()
    }


    private val bleList: MutableList<BleBean> = ArrayList()
    private var _binding: FragmentFirstBinding? = null
    lateinit var bleViewAdapter: BleViewAdapter
    val scan = Esp32BleScanManager()
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("MissingPermission")
    fun getXeBtDevice(): String? {
        //获取蓝牙适配器
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        val esp32Device=bluetoothAdapter.getRemoteDevice("C8:C9:A3:F9:33:8A")
        if(esp32Device!=null){
            esp32BleDataManager= Esp32BleDataManager(requireActivity())
            esp32BleDataManager?.connect(esp32Device)
                ?.useAutoConnect(true)
                ?.timeout(10000)
                ?.retry(10, 200)
                ?.done {
                    Log.i("fuck", "esp32连接成功了.>>.....>>>>")
                }?.fail(object : FailCallback {
                    override fun onRequestFailed(device: BluetoothDevice, status: Int) {

                    }

                })
                ?.enqueue()
            Log.e("fuck","esp32Device yes")
        }else{
            Log.e("fuck","esp32Device no")
        }
        //得到已匹配的蓝牙设备列表
        val bondedDevices = bluetoothAdapter.bondedDevices
        if (bondedDevices != null && bondedDevices.size > 0) {
            for (bondedDevice in bondedDevices) {
                try {
                    //使用反射调用被隐藏的方法
                    val isConnectedMethod =
                        BluetoothDevice::class.java.getDeclaredMethod(
                            "isConnected"
                        )
                    isConnectedMethod.isAccessible = true
                    val isConnected =
                        isConnectedMethod.invoke(bondedDevice) as Boolean
                    if (isConnected) {
                        if(bondedDevice.name.equals("SMART_XE")){
                            Log.e("fuck","device yes")
                            XeBleUtils.bluetoothDevice = bondedDevice
                        }
                        return bondedDevice.name
                    }
                } catch (e: NoSuchMethodException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: InvocationTargetException) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }

// C8:C9:A3:F9:33:8A
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timer().schedule(carRunTask, Date(),50)

        haveBlePrepare.observe(viewLifecycleOwner){
            if(it){
                initGimbal()
                initButton()
                initWebrtc()
            }
        }

        _binding = FragmentFirstBinding.inflate(inflater, container, false)





        return binding.root

    }
    
    lateinit var xeBleDataManager: XeBleDataManager
    var esp32BleDataManager: Esp32BleDataManager?=null
    
    fun writeData(b:ByteArray){
        xeBleDataManager.sendCmd(b)
    }
    
    fun initButton(){

        binding.button10.setOnClickListener {
            val cmd = XeBleCmd.returnCenter(true)
            writeData(cmd)
        }

        binding.ga1.setOnJoystickMoveListener(object: JoystickView.OnJoystickMoveListener {
            override fun onValueChanged(angle: Int, power: Int, direction: Int) {
                val tAngle = -angle+90
                val x = (power * Math.cos(Math.toRadians(tAngle.toDouble()))).toInt()
                val y = (power * Math.sin(Math.toRadians(tAngle.toDouble()))).toInt()
                val cmd = XeBleCmd.followCenterCmd(x,y,200)
                writeData(cmd)
            }
        }, 100)

        binding.leftControl.setOnJoystickMoveListener(object: JoystickView.OnJoystickMoveListener {
            override fun onValueChanged(angle: Int, power: Int, direction: Int) {
                val tAngle = -angle+90
                val x = (power * Math.cos(Math.toRadians(tAngle.toDouble()))).toInt()
                val y = (power * Math.sin(Math.toRadians(tAngle.toDouble()))).toInt()
                channel1=(1000+y*1.5).toInt()
            }
        }, 100)

        binding.rightControl.setOnJoystickMoveListener(object: JoystickView.OnJoystickMoveListener {
            override fun onValueChanged(angle: Int, power: Int, direction: Int) {
                val tAngle = -angle+90
                val x = (power * Math.cos(Math.toRadians(tAngle.toDouble()))).toInt()
                val y = (power * Math.sin(Math.toRadians(tAngle.toDouble()))).toInt()
                channel2=1000+x*5
            }
        }, 100)

    }
    private fun initGimbal(){
        getXeBtDevice()
        xeBleDataManager = XeBleDataManager(requireActivity())
        xeBleDataManager.setNotifyListener(object:XeBleDataManager.OnNotifyListener{
            override fun onNotify(device: BluetoothDevice?, data: Data?) {
                data?.let { 
                    Log.e("fuck", it.value?.size.toString())
                }
            }

        })
        xeBleDataManager.connect(XeBleUtils.bluetoothDevice)
            .useAutoConnect(true)
            ?.timeout(10000)
            ?.retry(10, 200)
            ?.done {
                Log.i("fuck", "XE连接成功了.>>.....>>>>")
                dataScope.launch {
                    delay(500)
                    val cmd = XeBleCmd.mode1Cmd()
                    writeData(cmd)
                    delay(100)
                    val cmd2 = XeBleCmd.returnCenter(true)
                    writeData(cmd2)
                }
            }?.fail(object : FailCallback {
                override fun onRequestFailed(device: BluetoothDevice, status: Int) {
                    Log.i("fuck", "XE连接失败了.>>.....>>>> $status")
                }

            })
            ?.enqueue()
    }

    private fun initScan() {
        scan.initScan(requireContext())
        scan.setCallBack(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




    override fun scanReturn(name: String, bluetoothDevice: BluetoothDevice) {
        if(name.contains("lgh")==false){
            return
        }

        var z: Int = 0;
        for (ble in bleList) run {
            if (ble.name == bluetoothDevice.name) {
                z = 1
            }
        }
        if (z == 0) {
            bleList.add(BleBean(name, bluetoothDevice))
            bleViewAdapter.addDevice(name, bluetoothDevice)
        }
    }

    override fun onScanItemClick(bluetoothDevice: BluetoothDevice?) {
        scan.stop()
        BleServer.worker.initWorker(MainApplication.application,bluetoothDevice)
        findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
    }


    var channel1=1000;
    var channel2=1000;
    private val carRunTask=RtDataTask()

    inner class RtDataTask() : TimerTask() {
        override fun run() {
            val bb=ByteArray(12){
                0x32.toByte()
            }

            var control1k2k=channel1
            bb[0]=(control1k2k%256).toByte()
            bb[1]=(control1k2k/256).toByte()

            control1k2k=channel2
            bb[2]=(control1k2k%256).toByte()
            bb[3]=(control1k2k/256).toByte()


            control1k2k=1000;
            bb[4]=(control1k2k%256).toByte()
            bb[5]=(control1k2k/256).toByte()


            control1k2k=1000;
            bb[6]=(control1k2k%256).toByte()
            bb[7]=(control1k2k/256).toByte()


            control1k2k=1000;
            bb[8]=(control1k2k%256).toByte()
            bb[9]=(control1k2k/256).toByte()

            control1k2k=1000;
            bb[10]=(control1k2k%256).toByte()
            bb[11]=(control1k2k/256).toByte()

            esp32BleDataManager?.sendCmd(bb)
        }
    }

    override fun onDestroy() {
        carRunTask.cancel()
        super.onDestroy()
    }
    lateinit var mMediaStream: MediaStream
    lateinit var mPeerConnectionFactory: PeerConnectionFactory
    private var mVideoCapturer: CameraVideoCapturer? = null
    lateinit var mVideoTrack: VideoTrack
    lateinit var mAudioTrack: AudioTrack
    private var mSocket: Socket? = null
    lateinit var pcConstraints: MediaConstraints
    lateinit var sdpConstraints: MediaConstraints
    lateinit var iceServers: LinkedList<PeerConnection.IceServer>
    lateinit var audioSource: AudioSource
    lateinit var mAudioManager: AudioManager
    private var isOffer = false
    var mPeer: Peer? = null
    fun initWebrtc(){
        mEglBase = EglBase.create()
//        val localView=binding.localVideoView
//        localView!!.init(mEglBase.eglBaseContext, null)
//        localView!!.keepScreenOn = true
//        localView!!.setMirror(true)
//        localView!!.setZOrderMediaOverlay(true)
//        localView!!.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
//        localView!!.setEnableHardwareScaler(false)

        mAudioManager = (requireActivity().getSystemService(AppCompatActivity.AUDIO_SERVICE) as AudioManager)
        mAudioManager.mode = AudioManager.MODE_IN_CALL
        mAudioManager.isSpeakerphoneOn = false

        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(MainApplication.application)
                .createInitializationOptions()
        )

        val encoderFactory: VideoEncoderFactory
        val decoderFactory: VideoDecoderFactory

        encoderFactory = DefaultVideoEncoderFactory(
            mEglBase.eglBaseContext,
            true,
            true
        )
        decoderFactory = DefaultVideoDecoderFactory(mEglBase.eglBaseContext)

        val options = PeerConnectionFactory.Options()

        mPeerConnectionFactory = PeerConnectionFactory.builder()
            .setOptions(options)
            .setAudioDeviceModule(
                JavaAudioDeviceModule.builder(MainApplication.application).createAudioDeviceModule()
            )
            .setVideoEncoderFactory(encoderFactory)
            .setVideoDecoderFactory(decoderFactory)
            .createPeerConnectionFactory()!!
        iceServers = LinkedList()
        iceServers.add(PeerConnection.IceServer.builder("stun:galic.fit:9669").createIceServer())
        iceServers.add(PeerConnection.IceServer.builder("turn:galic.fit:9669").setUsername("vaca").setPassword("123456").createIceServer())
        pcConstraints = MediaConstraints()
        audioSource = mPeerConnectionFactory.createAudioSource(MediaConstraints())
        mAudioTrack = mPeerConnectionFactory.createAudioTrack("audiotrack", audioSource)
        mMediaStream = mPeerConnectionFactory.createLocalMediaStream("localstream")
        mMediaStream.addTrack(mAudioTrack)


        //创建需要传入设备的名称
        val captureAndroid = createVideoCapture(requireContext())!!
        val videoSource =
            mPeerConnectionFactory.createVideoSource(captureAndroid.isScreencast())

        videoSource.adaptOutputFormat(1280, 720, 30)
        // 视频
        val surfaceTextureHelper = SurfaceTextureHelper.create(
            "CaptureThread",
            mEglBase.getEglBaseContext()
        )
        captureAndroid.initialize(
            surfaceTextureHelper,
            requireActivity(),
            videoSource.capturerObserver
        )
        captureAndroid.startCapture(1280, 720, 30)

        val _localVideoTrack =
            mPeerConnectionFactory.createVideoTrack(
                "ARDAMSv0",
                videoSource
            )
        _localVideoTrack.setEnabled(true)
//        _localVideoTrack.addSink(localView)
        mMediaStream.addTrack(_localVideoTrack)
        //连接服务器
        try {
            mSocket = IO.socket("http://vaca.vip:3223/")
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
        mSocket!!.on("SomeOneOnline") {
            isOffer = true
            if (mPeer == null) {
                mPeer = Peer()
            }
            mPeer!!.peerConnection.createOffer(mPeer, sdpConstraints)
        }.on("IceInfo") { args ->
            try {
                val jsonObject = JSONObject(args[0].toString())
                var candidate: IceCandidate? = null
                candidate = IceCandidate(
                    jsonObject.getString("id"),
                    jsonObject.getInt("label"),
                    jsonObject.getString("candidate")
                )
                mPeer!!.peerConnection.addIceCandidate(candidate)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }.on("SdpInfo") { args ->
            if (mPeer == null) {
                mPeer = Peer()
            }
            try {
                val jsonObject = JSONObject(args[0].toString())
                val description = SessionDescription(
                    SessionDescription.Type.fromCanonicalForm(jsonObject.getString("type")),
                    jsonObject.getString("description")
                )
                mPeer!!.peerConnection.setRemoteDescription(mPeer, description)
                if (!isOffer) {
                    mPeer!!.peerConnection.createAnswer(mPeer, sdpConstraints)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        mSocket!!.connect()
    }

    inner class Peer : PeerConnection.Observer, SdpObserver, DataChannel.Observer {
        var peerConnection: PeerConnection
        lateinit var controlCarDataChannel: DataChannel
        lateinit var carInfoDataChannel: DataChannel
        init {
            sdpConstraints = MediaConstraints()
            sdpConstraints.mandatory.add(
                MediaConstraints.KeyValuePair(
                    "OfferToReceiveAudio",
                    "true"
                )
            )
            sdpConstraints.mandatory.add(
                MediaConstraints.KeyValuePair(
                    "OfferToReceiveVideo",
                    "true"
                )
            )
            peerConnection =
                mPeerConnectionFactory.createPeerConnection(iceServers, pcConstraints, this)!!
            peerConnection.addStream(mMediaStream)

            val init = DataChannel.Init()
            init.negotiated = false
            init.ordered = false
            val carInfoDataformat = DataChannel.Init()
            carInfoDataformat.negotiated = false
            carInfoDataformat.ordered = true
            controlCarDataChannel = peerConnection.createDataChannel("fuck", init)
            carInfoDataChannel = peerConnection.createDataChannel("fuck2", carInfoDataformat)
            Log.e("fuck","create data channel")
        }

        // PeerConnection.Observer
        override fun onSignalingChange(signalingState: PeerConnection.SignalingState) {}
        override fun onIceConnectionChange(iceConnectionState: PeerConnection.IceConnectionState) {
            if (iceConnectionState == PeerConnection.IceConnectionState.DISCONNECTED) {

                mPeer = null
                isOffer = false

            }
        }

        override fun onIceConnectionReceivingChange(b: Boolean) {}
        override fun onIceGatheringChange(iceGatheringState: PeerConnection.IceGatheringState) {}
        override fun onIceCandidate(iceCandidate: IceCandidate) {
            try {
                val jsonObject = JSONObject()
                jsonObject.put("label", iceCandidate.sdpMLineIndex)
                jsonObject.put("id", iceCandidate.sdpMid)
                jsonObject.put("candidate", iceCandidate.sdp)
                mSocket!!.emit("IceInfo", jsonObject.toString())
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        override fun onIceCandidatesRemoved(iceCandidates: Array<IceCandidate>) {}
        override fun onAddStream(mediaStream: MediaStream) {
//            remoteVideoTrack = mediaStream.videoTracks[0]
//            remoteVideoTrack.addSink(remoteView)
        }

        override fun onRemoveStream(mediaStream: MediaStream) {}
        override fun onDataChannel(dataChannel: DataChannel) {
            Log.e("fuck","onDataChannel")
            dataChannel.registerObserver(this)
        }
        override fun onRenegotiationNeeded() {}
        override fun onAddTrack(rtpReceiver: RtpReceiver, mediaStreams: Array<MediaStream>) {}

        //    SdpObserver
        override fun onCreateSuccess(sessionDescription: SessionDescription) {
            peerConnection.setLocalDescription(this, sessionDescription)
            val jsonObject = JSONObject()
            try {
                jsonObject.put("type", sessionDescription.type.canonicalForm())
                jsonObject.put("description", sessionDescription.description)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            mSocket!!.emit("SdpInfo", jsonObject.toString())
        }

        override fun onSetSuccess() {}
        override fun onCreateFailure(s: String) {}
        override fun onSetFailure(s: String) {}
        override fun onBufferedAmountChange(p0: Long) {

        }

        override fun onStateChange() {

        }

        override fun onMessage(p0: DataChannel.Buffer?) {
            val data: ByteBuffer = p0?.data ?: return
            val bytes = ByteArray(data.capacity())
            data.get(bytes)
            Log.e("fuck",String(bytes))
        }


    }
    private fun createVideoCapture(context: Context): CameraVideoCapturer? {
        val enumerator: CameraEnumerator
        enumerator = if (Camera2Enumerator.isSupported(context)) {
            Camera2Enumerator(context)
        } else {
            Camera1Enumerator(true)
        }
        val deviceNames = enumerator.deviceNames
        for (deviceName in deviceNames) {
            if (enumerator.isBackFacing(deviceName)) {
                val videoCapturer = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }
        for (deviceName in deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                val videoCapturer = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }
        return null
    }
}