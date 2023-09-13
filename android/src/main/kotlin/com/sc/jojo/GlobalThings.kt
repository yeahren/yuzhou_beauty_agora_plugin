package com.sc.jojo

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.sc.jojo.beauty.SdkApi

lateinit var AppContext: Context
var sdkApi: SdkApi? = null
const val GlobalLogTag = "SDK_Demo"
internal var LogSwitch = true
internal var DebugLogSwitch = true
internal var WarningLogSwitch = true
internal var InfoLogSwitch = true
internal fun dLog(content: String) {
    if (LogSwitch && DebugLogSwitch) {
        Log.d(GlobalLogTag, content)
    }
}

internal fun wLog(content: String) {
    if (LogSwitch && WarningLogSwitch) {
        Log.w(GlobalLogTag, content)
    }
}

internal fun iLog(content: String) {
    if (LogSwitch && InfoLogSwitch) {
        Log.i(GlobalLogTag, content)
    }
}

internal fun toast(content: String) {
    Toast.makeText(AppContext, content, Toast.LENGTH_SHORT).show()
}
