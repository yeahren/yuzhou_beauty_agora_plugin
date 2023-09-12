package com.cosmos.extension

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

fun fileToBitmap(path: String): Bitmap? {
    try {
        val fileInputStream = FileInputStream(File(path))
        return BitmapFactory.decodeStream(fileInputStream)
    } catch (e: FileNotFoundException) {
        wLog("文件不存在 $path")
    }
    return null
}