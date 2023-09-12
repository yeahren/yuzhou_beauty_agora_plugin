package com.cosmos.config_type

import android.os.Build
import androidx.annotation.RequiresApi
import java.lang.Exception

/**
 * 标准进度为 [0-100]
 * 这个函数可以把 数据中的进度值 进行转换
 * 返回一个 标准的进度值
 * @param max 数据最大值
 * @param min 数据最小值
 * @param dataProgress 数据进度值
 */
fun dataProgressToStandardProgress(max: Float, min: Float, dataProgress: Float): Int {
    checkDataProgress(dataProgress, min, max)
    return ((dataProgress - min) / (max - min) * 100).toInt()
}

/**
 * 标准进度为 0~100
 * 这个函数可以把 标准的进度值 进行转换
 * 返回一个 数据中的进度值
 * @param max 数据最大值
 * @param min 数据最小值
 * @param standardProgress 标准进度值 [0-100]
 */
fun standardProgressToDataProgress(max: Float, min: Float, standardProgress: Int): Float {
    checkStandardProgress(standardProgress)
    return min + (standardProgress * (max - min) / 100f)
}

private fun checkDataProgress(progress: Float, min: Float, max: Float) {
    if (progress < min) {
        throw IllegalProgressException("progress < min is not allow")
    }
    if (max < min) {
        throw IllegalProgressException("max < min is not allow")
    }
    if (max == min) {
        throw IllegalProgressException("max = min is not allow")
    }
}

private fun checkStandardProgress(progress: Int) {
    if (progress < 0) {
        throw IllegalProgressException("StandardProgress < 0 is not allow")
    }
    if (progress > 100) {
        throw IllegalProgressException("StandardProgress > 100 is not allow")
    }
}

class IllegalProgressException : Exception {
    constructor() : this("")
    constructor(message: String?) : this(message, null)
    constructor(cause: Throwable?) : this("", cause)
    constructor(message: String?, cause: Throwable?) : super(message, cause)

    @RequiresApi(Build.VERSION_CODES.N)
    constructor(
        message: String?,
        cause: Throwable?,
        enableSuppression: Boolean,
        writableStackTrace: Boolean
    ) : super(message, cause, enableSuppression, writableStackTrace)
}