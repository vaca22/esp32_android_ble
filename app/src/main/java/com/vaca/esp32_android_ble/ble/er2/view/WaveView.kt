package com.viatom.littlePu.er2.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat

import com.vaca.esp32_android_ble.R
import com.vaca.esp32_android_ble.ble.BleServer


import com.viatom.littlePu.er2.bean.Er2Draw


import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*


class WaveView : View {

    companion object {
        var dvy:DoubleArray?=null
        var dvx:DoubleArray?=null
        var peakCurrent=0.0

        var disp = true
        val drawSize = 500
        var deltaX=1f;
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
                    BleServer.er2Graph.postValue(true)
                } catch (e: java.lang.Exception) {
                    BleServer.waveDataX.clear()
                    gIndex = 0;
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
            style = Paint.Style.FILL

            strokeWidth = 2.0f
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawRect(0f,0f,width.toFloat(),width.toFloat(),bgPaint)
        if (disp) {
            if(dvy==null){
                return
            }
            if(dvx==null){
                return
            }
            try {
                val xmin= dvx!!.min()
                val xmax= dvx!!.max()
                val gx=xmax-xmin
                val myWidth=width;

                val ymin= dvy!!.min()
                val ymax= dvy!!.max()
                val gy=ymax-ymin
                val myHeight=width

                for(k in 0 until (dvx!!.size-1)){
                    canvas.drawLine(
                        ((dvx!![k]-xmin)/gx*myWidth).toFloat(), ((dvy!![k]-ymin)/gy*myHeight).toFloat(),
                        ((dvx!![k+1]-xmin)/gx*myWidth).toFloat(), ((dvy!![k+1]-ymin)/gy*myHeight).toFloat(),wavePaint);
                }

            }catch (e:Exception){
                return
            }

        }

    }


    private fun getColor(resource_id: Int): Int {
        return ContextCompat.getColor(context, resource_id)
    }
}