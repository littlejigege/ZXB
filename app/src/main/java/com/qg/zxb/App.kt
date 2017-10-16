package com.qg.zxb

import android.app.Application
import com.mobile.utils.Utils

/**
 * Created by jimji on 2017/10/15.
 */
class App: Application() {
    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
    }
}