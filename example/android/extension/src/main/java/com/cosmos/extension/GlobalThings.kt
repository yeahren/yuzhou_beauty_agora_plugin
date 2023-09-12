package com.cosmos.extension

import android.content.Context
import android.util.Log

internal lateinit var AppContext: Context

internal var rootDir = ""

const val GlobalLogTag = "extension_log_tag"

internal var LogSwitch = true

internal var InfoLogSwitch = true
internal var DebugLogSwitch = true
internal var WarningLogSwitch = true
internal var ErrorLogSwitch = true

internal fun iLog(content: String) {
    if (LogSwitch && InfoLogSwitch) {
        Log.i(GlobalLogTag, content)
    }
}

internal fun dLog(content: String) {
    if (LogSwitch && DebugLogSwitch) {
        Log.d(GlobalLogTag, content)
    }
}


internal fun wLog(content: String) {
    if (LogSwitch && WarningLogSwitch) {
        Log.d(GlobalLogTag, content)
    }
}

internal fun ELog(content: String) {
    if (LogSwitch && ErrorLogSwitch) {
        Log.i(GlobalLogTag, content)
    }
}