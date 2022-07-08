package com.viatom.littlePu.er2.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

import com.vaca.esp32_android_ble.R
import com.vaca.esp32_android_ble.ble.BleServer


import com.viatom.littlePu.er2.bean.Er2Draw


import kotlinx.coroutines.launch
import java.util.*
import java.util.Locale.filter


class WaveView : View {

    companion object {
        var disp = false
        val drawSize = 500
        val data = IntArray(drawSize) {
            0
        }
        var currentHead = 0
        val headLen = 3
        var currentTail = 0


        val g = FloatArray(4)
        var gIndex = 0;
        var currentUpdateIndex = 0

        fun reset() {
            for (k in 0 until drawSize) {
                data[k] = 0
            }
            gIndex = 0
            disp = false
            currentUpdateIndex = 0
            currentHead = 0
            currentTail = 0
        }

        fun poss(it: Er2Draw) {
            for (k in 0 until 4) {
                data[currentUpdateIndex] = (it.data[k] * WaveViewBackGround.co).toInt()
                currentUpdateIndex++
                if (currentUpdateIndex >= 500) {
                    currentUpdateIndex -= 500
                }
            }

            currentHead = currentUpdateIndex - 1
            var t = currentUpdateIndex + headLen
            if (t > drawSize - 1) {
                t -= drawSize
            }
            currentTail = t
        }


        class DrawTask() : TimerTask() {
            override fun run() {
                if (!BleServer.er2ConnectFlag) {
                    return
                }
                try {
                    do {
                        val gx = BleServer.waveDataX.poll()
                        if (gx == null) {
                            return
                        } else {
                            g[gIndex] = gx
                        }
                        gIndex++
                    } while (gIndex < 4);
                    gIndex = 0;
                    poss(Er2Draw(g))
                } catch (e: java.lang.Exception) {
                    BleServer.waveDataX.clear()
                    gIndex = 0;
                }


            }
        }


        class RtDataTask() : TimerTask() {
            override fun run() {
                if (!BleServer.er2ConnectFlag) {
                    return
                }


                BleServer.dataScope.launch {



                    val x = BleServer.ER_2_BLE_DATA_WORKER.getData()

                    x?.wave?.wFs?.let {
                        for (k in it) {
                            val doubleArray = doubleArrayOf(k.toDouble())
                            doubleArray?.let { ga ->
                                if (ga.isNotEmpty()) {

                                    for (j in ga) {
                                        val xcv = j.toFloat()
                                        if (xcv > 2.2f) {
                                            BleServer.waveDataX.offer(2.2f)
                                        } else if (xcv < -2.2f) {
                                            BleServer.waveDataX.offer(-2.2f)
                                        } else {
                                            BleServer.waveDataX.offer(xcv)
                                        }

                                    }
                                }
                            }
                        }
                    }


                }
            }
        }


    }


    private val wavePaint = Paint()
    private val bgPaint = Paint()


    var n1 = 0
    var n2 = 0

    private fun judgePoint(k: Int): Int {
        if (currentHead < currentTail) {
            if ((k > currentHead) && (k <= currentTail)) {
                return 0
            } else {
                return 1
            }
        } else {
            if ((k > currentHead) || (k < currentTail)) {
                return 0
            } else {
                return 1
            }
        }
    }


    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init()
    }

    private fun init() {
        wavePaint.apply {
            color = getColor(R.color.wave_color)
            style = Paint.Style.STROKE
            strokeWidth = 5.0f
        }

        bgPaint.apply {
            color = getColor(R.color.gray)
            style = Paint.Style.STROKE
            strokeWidth = 2.0f
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawARGB(0, 0, 0, 0)
        if (disp) {
            var wavePath = Path()
            for ((index, h) in data.withIndex()) {
                n2 = judgePoint(index)
                if ((n2 == 1) && (index == data.size - 1)) {
                    canvas.drawPath(wavePath, wavePaint)
                    n1 = 0
                    break
                }
                if (n2 != n1) {
                    if (n1 > n2) {
                        canvas.drawPath(wavePath, wavePaint)
                        n1 = 0
                    } else {
                        wavePath = Path()
                        wavePath.moveTo(
                            2.56f * index.toFloat(),
                            height / 2 - h.toFloat()
                        )
                        n1 = 1
                    }
                } else {
                    wavePath.lineTo(
                        2.56f * index.toFloat(),
                        height / 2 - h.toFloat()
                    )
                }

            }
        }

    }


    private fun getColor(resource_id: Int): Int {
        return ContextCompat.getColor(context, resource_id)
    }
}