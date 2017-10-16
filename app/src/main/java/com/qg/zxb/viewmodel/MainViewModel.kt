package com.qg.zxb.viewmodel

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.ListView
import com.mobile.utils.inUiThread
import com.mobile.utils.showToast
import com.qg.zxb.bluetooth.BTManager
import com.qg.zxb.bluetooth.ConnectListener
import com.qg.zxb.bluetooth.MsgListener
import com.qg.zxb.model.Msg
import com.qg.zxb.ui.MainActivity
import com.qg.zxb.utils.log

/**
 * Created by jimji on 2017/10/16.
 */
class MainViewModel(val act: MainActivity) {
    val msgList = mutableListOf<Msg>()
    private val bluetoothAdapter by lazy { BluetoothAdapter.getDefaultAdapter() }
    private val HC05 = "HC-05"
    private lateinit var btManager: BTManager

    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (BluetoothDevice.ACTION_FOUND == intent.action) {
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                if (device.name == "HC-05") {
                    act.hideSearchingView()
                    btManager = BTManager(device)
                    setupListener()
                    act.showConnectingView()
                    btManager.connect()
                }
            }
        }
    }

    fun connectToHC05() {
        for (device in bluetoothAdapter.bondedDevices) {
            if (device.name == HC05) {
                btManager = BTManager(device)
                setupReceiver()
                setupListener()
                act.showConnectingView()
                btManager.connect()
                return
            }
        }
        act.showSearchingView()
        setupReceiver()
        bluetoothAdapter.startDiscovery()
    }

    private fun setupListener() {
        btManager.connectListener = object : ConnectListener {
            override fun onSucceed() {
                inUiThread { act.hideConnectingView() }
                showToast("设备已连接")
                removeReiver()
            }

            override fun onFailed() {
                inUiThread { act.hideConnectingView() }
                showToast("连接失败")

            }
        }
        btManager.msgListener = object : MsgListener {
            override fun onMsg(msg: String) {
                inUiThread { act.onMessage(msg) }

            }
        }

    }

    private fun setupReceiver() {
        val filter = IntentFilter()
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        act.registerReceiver(receiver, filter)
    }

    private fun removeReiver() {
        act.unregisterReceiver(receiver)
    }

    fun sendText(text: String): Boolean = btManager.write(text)

}