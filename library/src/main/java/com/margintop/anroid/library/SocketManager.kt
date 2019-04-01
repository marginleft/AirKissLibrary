package com.margintop.anroid.library

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

/**
 * @author margintop
 * @date 2019/4/1
 */
internal class SocketManager : Closeable {

    private var mSendSocket: DatagramSocket? = null
    private var mUdpServerSocket: DatagramSocket? = null

    fun sendPackage(data: IntArray, success: () -> Unit, error: (String?) -> Unit) {
        try {
            mSendSocket = DatagramSocket().apply {
                broadcast = true
            }
            for (i in data.indices) {
                val pkg = DatagramPacket(
                    ByteArray(1500),
                    data[i],
                    InetAddress.getByName("255.255.255.255"),
                    10000
                )
                mSendSocket?.send(pkg)
                Thread.sleep(10)
            }
            success()
        } catch (e: Exception) {
            e.printStackTrace()
            error(e.message)
        } finally {
            mSendSocket?.close()
            mSendSocket?.disconnect()
        }
    }

    fun receivePackage(success: () -> Unit, error: (String?) -> Unit) {
        try {
            mUdpServerSocket = DatagramSocket(12476).apply {
                soTimeout = 5 * 60 * 1000
            }
            val buffer = ByteArray(15000)
            val packet = DatagramPacket(buffer, buffer.size)
            while (true) {
                mUdpServerSocket?.receive(packet)
                val udpData = packet.data?.let {
                    // 根据自身业务逻辑处理返回的数据
                    String(it)
                }
                println("received: " + udpData)
                success()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            error(e.message)
        } finally {
            mUdpServerSocket?.close()
            mUdpServerSocket?.disconnect()
        }
    }

    override fun close() {
        mSendSocket?.close()
        mSendSocket?.disconnect()
        mUdpServerSocket?.close()
        mUdpServerSocket?.disconnect()
    }
}