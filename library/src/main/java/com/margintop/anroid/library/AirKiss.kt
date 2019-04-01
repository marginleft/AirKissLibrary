package com.margintop.anroid.library

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.Looper
import kotlin.concurrent.thread

/**
 * @author margintop
 * @date 2019/4/1
 */
private val handle by lazy {
    Handler(Looper.getMainLooper())
}

fun getCurrentSSID(context: Context): String? {
    val applicationContext = context.applicationContext
    var ssid: String? = null
    (applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager)
        ?.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        ?.apply {
            if (isConnected) {
                (applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager)
                    ?.connectionInfo
                    ?.ssid
                    ?.let {
                        ssid = it.replace("^\"|\"$".toRegex(), "")
                    }
            }
        }
    return ssid
}

fun autoLink(ssid: String, psw: String, callback: Callback): Closeable {
    val socketManager = SocketManager()
    thread {
        socketManager.sendPackage(AirKissEncoder(ssid, psw).getEncodedData(), {
            // 发送成功

        }, {
            // 发送失败
            onFailure(callback, it)
        })
    }
    thread {
        socketManager.receivePackage({
            // 接收成功
            onSuccess(callback)
        }, {
            // 接收失败
            onFailure(callback, it)
        })
    }
    return socketManager
}

private fun onSuccess(callback: Callback) {
    handle.post {
        callback.onSuccess()
    }
}

private fun onFailure(callback: Callback, message: String?) {
    handle.post {
        callback.onFailure(message)
    }
}
