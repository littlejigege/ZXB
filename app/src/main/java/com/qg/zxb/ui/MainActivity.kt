package com.qg.zxb.ui

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.qg.zxb.utils.enableIfNot
import com.qg.zxb.utils.log
import android.content.IntentFilter
import android.view.View
import android.widget.Button
import com.mobile.utils.*
import com.qg.zxb.R
import com.qg.zxb.bluetooth.BTManager
import com.qg.zxb.bluetooth.ConnectListener
import com.qg.zxb.bluetooth.MsgListener
import com.qg.zxb.model.Msg
import com.qg.zxb.model.MsgAdapter
import com.qg.zxb.sensor.DateGetter
import com.qg.zxb.viewmodel.MainViewModel
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), View.OnClickListener, View.OnLongClickListener {


    private val connectingView by lazy {
        QMUITipDialog.Builder(this@MainActivity)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("正在连接")
                .create()
    }
    private val searchingView by lazy {
        QMUITipDialog.Builder(this@MainActivity)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("正在扫描")
                .create()
    }
    private lateinit var btManager: BTManager
    private val viewModel by lazy { MainViewModel(this) }
    private lateinit var msgAdapter: MsgAdapter
    private val dateGetter by lazy { DateGetter.with(this, viewModel) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initButtons()
        initView()
        viewModel.connectToHC05()//连接设备
    }


    fun showSearchingView() = searchingView.show()
    fun hideSearchingView() = searchingView.hide()
    fun showConnectingView() = connectingView.show()
    fun hideConnectingView() = connectingView.hide()

    fun onMessage(msg: String) {
        if (msg.isEmpty()) return
        viewModel.msgList.add(Msg(1, msg))
        msgAdapter.notifyDataSetChanged()
        listView.smoothScrollToPosition(msgAdapter.count - 1)
    }

    private fun initButtons() {
        btSend.onClick(100) {
            if (editText.value.isEmpty()) {
                showToast("不能发送空消息")
                return@onClick
            } else {
                hideInputMethod()
                sendAndAddToscren(editText.value)
                inUiThread { editText.setText("") }
            }
        }
        bt11.text = Preference.get("config", "bt11.name" to "1") as String
        bt12.text = Preference.get("config", "bt12.name" to "2") as String
        bt13.text = Preference.get("config", "bt13.name" to "3") as String
        bt21.text = Preference.get("config", "bt21.name" to "4") as String
        bt22.text = Preference.get("config", "bt22.name" to "5") as String
        bt23.text = Preference.get("config", "bt23.name" to "6") as String
        bt31.text = Preference.get("config", "bt31.name" to "7") as String
        bt32.text = Preference.get("config", "bt32.name" to "8") as String
        bt33.text = Preference.get("config", "bt33.name" to "9") as String
        bt41.text = Preference.get("config", "bt41.name" to "←") as String
        bt42.text = Preference.get("config", "bt42.name" to "0") as String
        bt43.text = "开始"
        //bt43.text = Preference.get("config", "bt43.name" to "→") as String
        bt11.setOnClickListener(this)
        bt12.setOnClickListener(this)
        bt13.setOnClickListener(this)
        bt21.setOnClickListener(this)
        bt22.setOnClickListener(this)
        bt23.setOnClickListener(this)
        bt31.setOnClickListener(this)
        bt32.setOnClickListener(this)
        bt33.setOnClickListener(this)
        bt41.setOnClickListener(this)
        bt42.setOnClickListener(this)
        bt43.setOnClickListener(this)
        bt11.setOnLongClickListener(this)
        bt12.setOnLongClickListener(this)
        bt13.setOnLongClickListener(this)
        bt21.setOnLongClickListener(this)
        bt22.setOnLongClickListener(this)
        bt23.setOnLongClickListener(this)
        bt31.setOnLongClickListener(this)
        bt32.setOnLongClickListener(this)
        bt33.setOnLongClickListener(this)
        bt41.setOnLongClickListener(this)
        bt42.setOnLongClickListener(this)
        //  bt43.setOnLongClickListener(this)

    }

    private fun initView() {
        msgAdapter = MsgAdapter(this, R.layout.msg_item, viewModel.msgList)
        listView.adapter = msgAdapter
    }

    override fun onClick(v: View) {
        when (v) {
            bt11 -> {
                sendAndAddToscren(Preference.get("config", "bt11.text" to "1") as String)
            }
            bt12 -> {
                sendAndAddToscren(Preference.get("config", "bt12.text" to "2") as String)
            }
            bt13 -> {
                sendAndAddToscren(Preference.get("config", "bt13.text" to "3") as String)
            }
            bt21 -> {
                sendAndAddToscren(Preference.get("config", "bt21.text" to "4") as String)
            }
            bt22 -> {
                sendAndAddToscren(Preference.get("config", "bt22.text" to "5") as String)
            }
            bt23 -> {
                sendAndAddToscren(Preference.get("config", "bt23.text" to "6") as String)
            }
            bt31 -> {
                sendAndAddToscren(Preference.get("config", "bt31.text" to "7") as String)
            }
            bt32 -> {
                sendAndAddToscren(Preference.get("config", "bt32.text" to "8") as String)
            }
            bt33 -> {
                sendAndAddToscren(Preference.get("config", "bt33.text" to "9") as String)
            }
            bt41 -> {
                sendAndAddToscren(Preference.get("config", "bt41.text" to "←") as String)
            }
            bt42 -> {
                sendAndAddToscren(Preference.get("config", "bt42.text" to "0") as String)
            }
            bt43 -> {
                if (bt43.text == "开始") {
                    dateGetter.start()
                    bt43.text = "停止"
                } else {
                    dateGetter.stop()
                    bt43.text ="开始"
                }
                //sendAndAddToscren(Preference.get("config", "bt43.text" to "→") as String)
            }
        }
    }

    override fun onLongClick(v: View?): Boolean {
        when (v) {
            bt11 -> {
                showDialog(bt11, "bt11")
            }
            bt12 -> {
                showDialog(bt12, "bt12")
            }
            bt13 -> {
                showDialog(bt13, "bt13")
            }
            bt21 -> {
                showDialog(bt21, "bt21")
            }
            bt22 -> {
                showDialog(bt22, "bt22")
            }
            bt23 -> {
                showDialog(bt23, "bt23")
            }
            bt31 -> {
                showDialog(bt31, "bt31")
            }
            bt32 -> {
                showDialog(bt32, "bt32")
            }
            bt33 -> {
                showDialog(bt33, "bt33")
            }
            bt41 -> {
                showDialog(bt41, "bt41")
            }
            bt42 -> {
                showDialog(bt42, "bt42")
            }
            bt43 -> {
                showDialog(bt43, "bt43")
            }
        }
        return true
    }

    private fun sendAndAddToscren(text: String) {
        if (viewModel.sendText(text)) {
            viewModel.msgList.add(Msg(0, text + "(succeed)"))
            inUiThread {
                msgAdapter.notifyDataSetChanged()
                listView.smoothScrollToPosition(msgAdapter.count - 1)
            }
        } else {
            viewModel.msgList.add(Msg(0, text + "(failed)"))
            inUiThread {
                msgAdapter.notifyDataSetChanged()
                listView.smoothScrollToPosition(msgAdapter.count - 1)
            }
        }
    }

    private fun showDialog(button: Button, buttonStr: String) {
        var buttonName = Preference.get("config", buttonStr to button.text) as String
        var buttonText = ""
        val builder = QMUIDialog.EditTextDialogBuilder(this)
        builder.setPlaceholder("功能键名#功能")
                .setTitle(buttonName)
                .addAction("取消", { dialog, _ -> dialog.dismiss() })
                .addAction("确定", { dialog, _ ->
                    if (builder.editText.value.isEmpty() || builder.editText.value.split("#").size < 2) {
                        "输入不合法".toast()
                        return@addAction
                    }
                    buttonName = builder.editText.value.split("#")[0]
                    buttonText = builder.editText.value.split("#")[1]
                    Preference.save("config") {
                        "$buttonStr.name" - buttonName
                        "$buttonStr.text" - buttonText
                    }
                    button.text = buttonName
                    dialog.dismiss()
                })
        builder.show()
    }

    override fun onDestroy() {
        dateGetter.stop()
        super.onDestroy()
    }

}
