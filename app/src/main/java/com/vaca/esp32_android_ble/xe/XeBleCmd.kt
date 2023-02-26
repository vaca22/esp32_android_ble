package com.example.smart_xe_gimble.xe

import android.util.Log


object XeBleCmd {

//-        盗梦空间
    fun inception1(): ByteArray? {
        return pivateSpace(1, 0, 0)
    }

//--------盗梦空间
    fun inception2(): ByteArray? {
        return pivateSpace(1, 1, 15)
    }

//--------------电量查询
    fun getBatteryLevel(): ByteArray {
        return processCmd(4.toByte(), Ascii.f30354m, (-127.toByte()).toByte(), null, 0.toByte())
    }

//------------云台姿势查询
    fun getPTZposition(): ByteArray {
        return processCmd3(16.toByte(), null, 0.toByte())
    }

//版本查询2
    fun versionQuery3(): ByteArray {
        return processCmd(4.toByte(), Ascii.f30354m, 80.toByte(), null, 0.toByte())
    }

//-----------------版本查询
    fun versionQuery2(): ByteArray {
        return processCmd(2.toByte(), Ascii.f30354m, 80.toByte(), null, 0.toByte())
    }

//-------------系统状态查询
    fun getSystemStatus(): ByteArray {
        return processCmd3(81.toByte(), null, 0.toByte())
    }

//---------------轨迹模式命令1
    fun setTrajectory1(): ByteArray {
        return processCmd3(34.toByte(), byteArrayOf(0), 1.toByte())
    }

//-------------轨迹模式命令2
    fun setTrajectoryMode2(positionCount: Int, durationTime: Int, positionData: ByteArray?): ByteArray? {
        return byteArrayOf(85, 2, Ascii.f30355n, 34, 1, 1, 1, 51)
    }


    //------------轨迹模式命令
    private fun TrajectoryModeCmd(
        type: Int,
        positionCount: Int,
        durationTime: Int,
        positionData: ByteArray
    ): ByteArray {
        val length = positionData.size
        val m20020h = makeArray2(durationTime)
        var i = 4
        val i2 = length + 4
        val bArr = ByteArray(i2)
        var i3 = 0
        bArr[0] = type.toByte()
        bArr[1] = positionCount.toByte()
        bArr[2] = m20020h[0]
        bArr[3] = m20020h[1]
        while (i3 < positionData.size) {
            bArr[i] = positionData[i3]
            i3++
            i++
        }
        return processCmd(2.toByte(), Ascii.f30355n, 34.toByte(), bArr, i2.toByte())
    }

//--------------轨迹模式命令3
    fun setTrajectoryMode3(positionCount: Int, durationTime: Int, positionData: ByteArray): ByteArray? {
        return TrajectoryModeCmd(2, positionCount, durationTime, positionData)
    }

//--------------扩展模式通知
    fun extendModeNotification(): ByteArray? {
        return processCmd3(Ascii.f30362u, byteArrayOf(0), 1.toByte())
    }

//-------------姿态设置
    fun setAttitude(model: Int, position: Int): ByteArray? {
        val bArr = ByteArray(6)
        for (i in 0..5) {
            bArr[i] = 0
        }
        if (model == 0) {
            if (position != 0) {
                if (position == 1) {
                    bArr[4] = -120
                    bArr[5] = 19
                } else if (position == 2) {
                    bArr[4] = -60
                    bArr[5] = 9
                } else if (position == 3) {
                    bArr[4] = 0
                    bArr[5] = 0
                } else if (position == 4) {
                    bArr[4] = 60
                    bArr[5] = -10
                } else if (position == 5) {
                    bArr[4] = 120
                    bArr[5] = -20
                }
            }
        } else if (model == 1) {
            if (position != 0) {
                if (position == 1) {
                    bArr[4] = 16
                    bArr[5] = 39
                } else if (position == 2) {
                    bArr[4] = 76
                    bArr[5] = Ascii.f30333G
                } else if (position == 3) {
                    bArr[4] = -120
                    bArr[5] = 19
                } else if (position == 4) {
                    bArr[4] = -60
                    bArr[5] = 9
                } else if (position == 5) {
                    bArr[4] = 0
                    bArr[5] = 0
                } else if (position == 6) {
                    bArr[4] = 60
                    bArr[5] = -10
                } else if (position == 7) {
                    bArr[4] = 120
                    bArr[5] = -20
                } else if (position == 8) {
                    bArr[4] = -76
                    bArr[5] = -30
                } else if (position == 9) {
                    bArr[4] = -16
                    bArr[5] = -40
                }
            }
        } else if (model == 2) {
            if (position != 0) {
                if (position == 1) {
                    bArr[2] = 48
                    bArr[3] = -8
                    bArr[4] = -60
                    bArr[5] = 9
                } else if (position == 2) {
                    bArr[2] = 48
                    bArr[3] = -8
                } else if (position == 3) {
                    bArr[2] = 48
                    bArr[3] = -8
                    bArr[4] = 60
                    bArr[5] = -10
                } else if (position == 4) {
                    bArr[4] = 60
                    bArr[5] = -10
                } else if (position != 5) {
                    if (position == 6) {
                        bArr[4] = -60
                        bArr[5] = 9
                    } else if (position == 7) {
                        bArr[2] = -48
                        bArr[3] = 7
                        bArr[4] = -60
                        bArr[5] = 9
                    } else if (position == 8) {
                        bArr[2] = -48
                        bArr[3] = 7
                    } else if (position == 9) {
                        bArr[2] = -48
                        bArr[3] = 7
                        bArr[4] = 60
                        bArr[5] = -10
                    }
                }
            }
        } else if (model == 3 && position != 0) {
            if (position == 1) {
                bArr[2] = 48
                bArr[3] = -8
                bArr[4] = -120
                bArr[5] = 19
            } else if (position == 2) {
                bArr[2] = 48
                bArr[3] = -8
                bArr[4] = -60
                bArr[5] = 9
            } else if (position == 3) {
                bArr[2] = 48
                bArr[3] = -8
            } else if (position == 4) {
                bArr[2] = 48
                bArr[3] = -8
                bArr[4] = 60
                bArr[5] = -10
            } else if (position == 5) {
                bArr[2] = 48
                bArr[3] = -8
                bArr[4] = 120
                bArr[5] = -20
            } else if (position == 6) {
                bArr[4] = 120
                bArr[5] = -20
            } else if (position == 7) {
                bArr[4] = 60
                bArr[5] = -10
            } else if (position != 8) {
                if (position == 9) {
                    bArr[4] = -60
                    bArr[5] = 9
                } else if (position == 10) {
                    bArr[4] = -120
                    bArr[5] = 19
                } else if (position == 11) {
                    bArr[2] = -48
                    bArr[3] = 7
                    bArr[4] = -120
                    bArr[5] = 19
                } else if (position == 12) {
                    bArr[2] = -48
                    bArr[3] = 7
                    bArr[4] = -60
                    bArr[5] = 9
                } else if (position == 13) {
                    bArr[2] = -48
                    bArr[3] = 7
                } else if (position == 14) {
                    bArr[2] = -48
                    bArr[3] = 7
                    bArr[4] = 60
                    bArr[5] = -10
                } else if (position == 15) {
                    bArr[2] = -48
                    bArr[3] = 7
                    bArr[4] = 120
                    bArr[5] = -20
                }
            }
        }
        return processCmd3(17.toByte(), bArr, 6.toByte())
    }

//-------------扩展模式通知2
    fun extendModeNotification2(): ByteArray? {
        return processCmd3(Ascii.f30362u, byteArrayOf(Ascii.f30362u), 1.toByte())
    }

//--------------姿态偏移读取
    fun attitudeOffsetReadCmd(): ByteArray {
        return processCmd(2.toByte(), Ascii.f30354m, Ascii.f30366y, null, 0.toByte())
    }

//---------控制参数读取
    fun readControlSettingCmd(): ByteArray {
        return processCmd(2.toByte(), Ascii.f30354m, 26.toByte(), null, 0.toByte())
    }

//---------------------跟随参数读取
    fun readFollowSettingCmd(): ByteArray {
        return processCmd(2.toByte(), Ascii.f30354m, Ascii.f30327A, null, 0.toByte())
    }

//---------------电机零位置偏移读取
    fun motorZeroOffsetReadCmd(): ByteArray {
        return processCmd(2.toByte(), Ascii.f30354m, 39.toByte(), null, 0.toByte())
    }

 //-------------姿态偏移设置
    fun attitudeOffsetSettingCmd(rollAttitudeOffset: Int): ByteArray? {
        return processCmd(
            2.toByte(),
            Ascii.f30355n,
            Ascii.f30367z,
            byteArrayOf(
                (rollAttitudeOffset and 255).toByte(),
                (rollAttitudeOffset shr 8 and 255).toByte(),
                0,
                0
            ),
            4.toByte()
        )
    }

//---------控制参数设置
    fun controlParameterCmd(
        rollStickDir: Int,
        pitchStickDir: Int,
        headingStickDir: Int,
        rollStickSpeed: Int,
        pitchStickSpeed: Int,
        headingStickSpeed: Int
    ): ByteArray {
        return processCmd(
            2.toByte(),
            Ascii.f30355n,
            Ascii.f30331E,
            byteArrayOf(
                (rollStickDir and 255).toByte(),
                (pitchStickDir and 255).toByte(),
                (headingStickDir and 255).toByte(),
                (rollStickSpeed and 255).toByte(),
                (pitchStickSpeed and 255).toByte(),
                (headingStickSpeed and 255).toByte()
            ),
            6.toByte()
        )
    }

//-------------------------------跟随参数设置
    fun setFollowParameterCmd(headingDead: Int, pitchDead: Int, headingSpeed: Int, pitchSpeed: Int): ByteArray? {
        return processCmd(
            2.toByte(),
            Ascii.f30355n,
            Ascii.f30329C,
            byteArrayOf(
                (headingDead and 255).toByte(),
                (headingDead shr 8 and 255).toByte(),
                (pitchDead and 255).toByte(),
                (pitchDead shr 8 and 255).toByte(),
                (headingSpeed and 255).toByte(),
                (headingSpeed shr 8 and 255).toByte(),
                (pitchSpeed and 255).toByte(),
                (pitchSpeed shr 8 and 255).toByte()
            ),
            8.toByte()
        )
    }

//---电机零位置偏移设置
    fun setMoterZeroCmd(
        rollZeroPositionOffset: Int,
        headingZeroPositionOffset: Int,
        pitchZeroPositionOffset: Int
    ): ByteArray {
        val i = rollZeroPositionOffset * 100
        val i2 = pitchZeroPositionOffset * 100
        val i3 = headingZeroPositionOffset * 100
        return processCmd(
            2.toByte(),
            Ascii.f30355n,
            40.toByte(),
            byteArrayOf(
                (i and 255).toByte(),
                (i shr 8 and 255).toByte(),
                (i2 and 255).toByte(),
                (i2 shr 8 and 255).toByte(),
                (i3 and 255).toByte(),
                (i3 shr 8 and 255).toByte()
            ),
            6.toByte()
        )
    }

//-----------云台模式
    private fun workModeCmd(mode: Byte): ByteArray{
        return processCmd(
            2.toByte(),
            Ascii.f30354m,
            97.toByte(),
            byteArrayOf(0, mode, 0, 0, 0, 0),
            6.toByte()
        )
    }


    fun mode1Cmd(): ByteArray {
        return workModeCmd(0.toByte())
    }


    fun mode2Cmd(): ByteArray {
        return workModeCmd(64)
    }


    fun workModeRename(mode: Byte): ByteArray {
        return workModeCmd(mode)
    }




//--------工作模式4
    fun workMode4(): ByteArray {
        return workModeCmd(Byte.MIN_VALUE)
    }


    fun bytes2Hex(bytes: ByteArray?): String? {
        if (bytes == null) {
            return ""
        }
        val sb = StringBuilder()
        for (b in bytes) {
            var hexString = Integer.toHexString(b.toInt() and 255)
            if (hexString.length == 1) {
                hexString = "0"+ hexString
            }
            sb.append(hexString)
        }
        return sb.toString()
    }

//----------工作模式5
    fun workMode5(): ByteArray? {
        return workModeCmd((-64.toByte()).toByte())
    }





    private fun processCmd(
        pack: Byte,
        source: Byte,
        func: Byte,
       appendData: ByteArray?,
        appendDataLength: Byte
    ): ByteArray {
        val i = appendDataLength + 6 + 1
        val bArr = ByteArray(i)
        var i2 = 0
        bArr[0] = 85
        bArr[1] = pack
        bArr[2] = source
        bArr[3] = func
        bArr[4] = appendDataLength
        var i3 = 5
        bArr[5] = appendDataLength
        for (i4 in 0 until appendDataLength) {
            i3++
            bArr[i3] = appendData!![i4]
        }
        for (i5 in 1..i - 2) {
            i2 += bArr[i5].toInt()
        }
        bArr[i3 + 1] = (i2 and 255).toByte()
        val cmd= bytes2Hex(bArr)
        Log.e("fuck","send cmd:$cmd")
        return bArr
    }




    private fun processCmd3(func: Byte, appendData: ByteArray?, appendDataLength: Byte): ByteArray {
        return processCmd(2.toByte(), Ascii.f30354m, func, appendData, appendDataLength)
    }





    fun shakeHand1(): ByteArray {
        return byteArrayOf(-1, -1, 0, 2, 1, -9)
    }


    fun shakeHand2(): ByteArray {
        return byteArrayOf(-1, -1, 0, 8, 3, 71, 87, 49, 48, 0, 48, -84)
    }
    fun shakeHand3(): ByteArray {
        return byteArrayOf(-1, -1, 0, 2, 8, -79)
    }

    fun shakeHand4(): ByteArray {
        return byteArrayOf(-1, -1, 0, 8, 3, 71, 87, 49, 48, 0, 0, -127)
    }


    private fun makeArray1(value: Int): ByteArray? {
        return byteArrayOf(
            (value and 255).toByte(),
            (value shr 8 and 255).toByte(),
            (value shr 16 and 255).toByte(),
            (value shr 24 and 255).toByte()
        )
    }





    private fun makeArray2(value: Int): ByteArray {
        return byteArrayOf((value and 255).toByte(), (value shr 8 and 255).toByte())
    }







//---------盗梦空间
    private fun pivateSpace(command: Int, type: Int, speed: Int): ByteArray? {
        return processCmd(
            2.toByte(),
            Ascii.f30354m,
            49.toByte(),
            byteArrayOf(command.toByte(), type.toByte(), speed.toByte()),
            3.toByte()
        )
    }

//-------------------机架版本查询
    fun versionQueryCmd(dstAddress: Byte): ByteArray? {
        return processCmd(dstAddress, Ascii.f30354m, 118.toByte(), null, 0.toByte())
    }

//----------开关机命令
    fun powerCmd(bin: ByteArray?): ByteArray? {
        return processCmd(2.toByte(), Ascii.f30354m, 82.toByte(), bin, 1.toByte())
    }

//----------控制板重启
    fun restartCmd(bin: ByteArray): ByteArray? {
        return processCmd(2.toByte(), Ascii.f30354m, 69.toByte(), bin, bin.size.toByte())
    }


    //--debug cmd
    fun m20011q(): ByteArray? {
        return processCmd(Ascii.f30354m, Ascii.f30354m, -104, null, 0.toByte())
    }

//------------回中
    fun returnCenter(horizontal: Boolean): ByteArray {
        return processCmd3(48.toByte(), byteArrayOf(0), 1.toByte())
    }

//固件升级完成
    fun updateFirmwareCompleteCmd(dstAddr: Byte): ByteArray? {
        return processCmd(dstAddr, 0.toByte(), 116.toByte(), null, 0.toByte())
    }



    private fun processCmd2(dstAddr: Byte, code: Byte, addr: Int, bin: ByteArray): ByteArray? {
        val bArr = ByteArray(bin.size + 4)
        System.arraycopy(makeArray1(addr), 0, bArr, 0, 4)
        System.arraycopy(bin, 0, bArr, 4, bin.size)
        return processCmd(dstAddr, 0.toByte(), code, bArr, (bin.size + 4).toByte())
    }

//----------------固件升级数据A
    fun updateFirmwareData1(dstAddr: Byte, addr: Int, bin: ByteArray): ByteArray? {
        return processCmd2(dstAddr, 115.toByte(), addr, bin)
    }

    //----------------固件升级数据B
    fun updateFirmwareData2(dstAddr: Byte, addr: Int, bin: ByteArray): ByteArray? {
        return processCmd2(dstAddr, 117.toByte(), addr, bin)
    }

//固件升级准备命令
    fun updateFirmwarePrepareCmd(dstAddr: Byte): ByteArray? {
        return processCmd(dstAddr, 0.toByte(), 113.toByte(), null, 0.toByte())
    }

//固件升级开始
    fun updateFirmwareStartCmd(dstAddr: Byte): ByteArray? {
        return processCmd(dstAddr, 0.toByte(), 114.toByte(), null, 0.toByte())
    }

//-------------中心跟随命令
    fun followCenterCmd(xOffset: Int, yOffset: Int, speed: Int): ByteArray {
        val min = Math.min(Math.max(xOffset, -1000), 1000)
        val min2 = Math.min(Math.max(yOffset, -1000), 1000)
        val m20020h = makeArray2(min)
        val m20020h2 = makeArray2(min2)
        val length = m20020h.size + m20020h2.size + 1
        val bArr = ByteArray(length)
        bArr[0] = m20020h[0]
        bArr[1] = m20020h[1]
        bArr[2] = m20020h2[0]
        bArr[3] = m20020h2[1]
        bArr[4] = speed.toByte()
        return processCmd3(35.toByte(), bArr, length.toByte())
    }


    //------------盗梦空间模式5

    fun inception3(): ByteArray? {
        return pivateSpace(1, 2, 0)
    }

}