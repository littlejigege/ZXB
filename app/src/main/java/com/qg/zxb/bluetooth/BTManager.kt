package com.qg.zxb.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import com.mobile.utils.showToast
import com.mobile.utils.toast
import com.qg.zxb.utils.log
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.lang.StringBuilder
import java.lang.reflect.Method
import com.qg.zxb.ui.MainActivity
import android.os.Bundle


/**
 * Created by jimji on 2017/10/15.
 */
class BTManager(device: BluetoothDevice) {
    private val mSocket: BluetoothSocket by lazy {
        val method: Method = device::class.java.getMethod("createRfcommSocket", Int::class.javaPrimitiveType)
        method.invoke(device, 1) as BluetoothSocket
    }
    private val mInStream by lazy { mSocket.inputStream }
    private val mOutStream by lazy { mSocket.outputStream }
    //消息线程,连接设备后开启
    private val msgThread = Thread({
        var readMessage: StringBuilder = StringBuilder()
        while (true) {
            val buffer = ByteArray(1024)
            val bytes: Int
            val availableBytes = mInStream.available()
            if (availableBytes > 0) {
                Thread.sleep(500)
                bytes = mInStream.read(buffer)
                readMessage.append(String(buffer, 0, bytes))
                if (mInStream.available() == 0) {
                    msgListener!!.onMsg(readMessage.toString())
                    readMessage = StringBuilder()
                }
            }
//            val bytes: ByteArray = kotlin.ByteArray(1024)
//            mInStream.read(bytes)
//            strBuilder.append(String(bytes))
//            if (mInStream.available() == 0) {
//                if (msgListener != null) {
//                    msgListener!!.onMsg(strBuilder.toString())
//                }
//                strBuilder = StringBuilder()
//            }

//            val list = mInStream.reader().forEachLine {
//                if (msgListener != null) {
//                    msgListener!!.onMsg(it)
//                }
//            }

        }
    })
    //连接监听器，用于外界获得连接状态
    var connectListener: ConnectListener? = null
    //消息监听
    var msgListener: MsgListener? = null
    private var isConnected = false
        get() = if (field) true else false


    fun connect() {
        Thread({
            isConnected = if (_connect()) {
                if (connectListener != null) {
                    connectListener!!.onSucceed()
                }
                startListenMsg()//开始监听消息
                true
            } else {
                if (connectListener != null) {
                    connectListener!!.onFailed()
                }
                false
            }
        }).start()
    }

    /**纯链接*/
    private fun _connect(): Boolean {
        try {
            mSocket.connect()
        } catch (e: Exception) {
            try {
                mSocket.close()
            } catch (e: Exception) {
                return false
            }
            return false
        }
        return true
    }

    private fun startListenMsg() = msgThread.start()
    fun write(msg: String): Boolean {
        try {
            mOutStream.write(msg.toByteArray())
        } catch (e: Exception) {
            return false
        }
        return true
    }

    fun shutDown() {
        //TODO
    }

}