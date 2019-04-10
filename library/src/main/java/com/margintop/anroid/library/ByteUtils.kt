package com.margintop.anroid.library

/**
 * @author margintop
 * @date 2019/4/1
 */
fun crc8(data: String) = crc8(data.toByteArray())

fun crc8(data: ByteArray): Int {
    var len = data.size
    var i = 0
    var crc = 0x00
    while (len-- > 0) {
        var extract = data[i++]
        for (tempI in 8 downTo 1) {
            var sum = (crc and 0xFF) xor (extract.toInt() and 0xFF)
            sum = (sum and 0xFF) and 0x01
            crc = (crc and 0xFF) ushr 1
            if (sum != 0) {
                crc = (crc and 0xFF) xor 0x8C
            }
            extract = ((extract.toInt() and 0xFF) ushr 1).toByte()
        }
    }
    return crc and 0xFF
}