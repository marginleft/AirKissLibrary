package com.margintop.anroid.library

import kotlin.random.Random

/**
 * @author margintop
 * @date 2019/4/1
 */
class AirKissEncoder(ssid: String, password: String) {

    private var mCurrentLength = 0;
    private val mEncodedData by lazy {
        IntArray(2 shl 14)
    }
    private val mRandomChar by lazy {
        Random.nextInt(0x7F)
    }

    init {
        var times = 5;
        while (times-- > 0) {
            leadingPart()
            magicCode(ssid, password)
            for (i in 0 until 15) {
                prefixCode(password)

                val data = password + mRandomChar + ssid
                var content = ByteArray(4)
                var index = 0
                while (index < data.length / 4) {
                    System.arraycopy(data.toByteArray(), index * 4, content, 0, content.size)
                    sequence(index, content)
                    ++index
                }
                if (data.length % 4 != 0) {
                    content = ByteArray(data.length % 4)
                    System.arraycopy(data.toByteArray(), index * 4, content, 0, content.size)
                    sequence(index, content)
                }
            }
        }
    }

    fun getEncodedData(): IntArray {
        return mEncodedData.copyOf(mCurrentLength)
    }

    private fun leadingPart() {
        for (i in 0 until 50) {
            for (j in 1..4) {
                appendEncodedData(j)
            }
        }
    }

    private fun magicCode(ssid: String, password: String) {
        val length = ssid.length + password.length + 1;
        val magicCode = IntArray(4)
        magicCode[0] = 0x00 or (length.ushr(4) and 0xF)
        if (magicCode[0] == 0)
            magicCode[0] = 0x08
        magicCode[1] = 0x10 or (length and 0xF)
        val crc8 = crc8(ssid)
        magicCode[2] = 0x20 or (crc8.ushr(4) and 0xF)
        magicCode[3] = 0x30 or (crc8 and 0xF)
        for (i in 0..19) {
            for (j in 0..3)
                appendEncodedData(magicCode[j])
        }
    }

    private fun prefixCode(password: String) {
        val length = password.length
        val prefixCode = IntArray(4)
        prefixCode[0] = 0x40 or (length.ushr(4) and 0xF)
        prefixCode[1] = 0x50 or (length and 0xF)
        val crc8 = crc8(byteArrayOf(length.toByte()))
        prefixCode[2] = 0x60 or (crc8.ushr(4) and 0xF)
        prefixCode[3] = 0x70 or (crc8 and 0xF)
        for (j in 0..3) {
            appendEncodedData(prefixCode[j])
        }
    }

    private fun sequence(index: Int, data: ByteArray) {
        val content = ByteArray(data.size + 1)
        content[0] = (index and 0xFF).toByte()
        System.arraycopy(data, 0, content, 1, data.size)
        val crc8 = crc8(content)
        appendEncodedData(0x80 or crc8)
        appendEncodedData(0x80 or index)
        for (tempData in data) {
            appendEncodedData(tempData.toInt() or 0x100)
        }
    }

    private fun appendEncodedData(value: Int) {
        mEncodedData[mCurrentLength++] = value
    }
}