package com.qg.zxb.utils

import android.bluetooth.BluetoothAdapter

/**
 * Created by jimji on 2017/10/15.
 */
fun BluetoothAdapter.enableIfNot() {
    if (!isEnabled) enable()
}