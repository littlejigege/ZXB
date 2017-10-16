package com.qg.zxb.utils

import android.util.Log

/**
 * Created by jimji on 2017/10/15.
 */
fun Any?.log(tag: Any? = "=======") {
    Log.d(tag.toString(), this.toString())
}