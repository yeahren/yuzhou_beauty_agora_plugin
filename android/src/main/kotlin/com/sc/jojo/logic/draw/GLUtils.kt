package com.sc.jojo.logic.draw

import android.graphics.Bitmap
import android.graphics.Matrix
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLES30
import android.opengl.GLUtils
import java.nio.*
import javax.microedition.khronos.opengles.GL10

object GLUtils {
    const val FLOAT_SIZE = 4
    const val INT_SIZE = 4
    const val SHORT_SIZE = 2

    fun generateOESTexure(): Int {
        val texture = IntArray(1)
        GLES20.glGenTextures(1, texture, 0)
        GLES20.glBindTexture(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            texture[0]
        )
        GLES20.glTexParameterf(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GL10.GL_TEXTURE_MAG_FILTER,
            GL10.GL_LINEAR.toFloat()
        )
        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GL10.GL_TEXTURE_WRAP_T,
            GL10.GL_CLAMP_TO_EDGE
        )
        return texture[0]
    }

    fun doSnapScreen(realWidth: Int, realHeight: Int): Bitmap? {
        return doSnapScreen(0, 0, 0, realWidth, realHeight)
    }

    fun doSnapScreen(x: Int, y: Int, realWidth: Int, realHeight: Int): Bitmap? {
        return doSnapScreen(0, x, y, realWidth, realHeight)
    }

    fun doSnapScreen(degress: Int, x: Int, y: Int, width: Int, height: Int): Bitmap? {
        return if (height > 0 && width > 0) {
            val imc2Buf = IntBuffer.allocate(width * height)
            GLES20.glReadPixels(
                x,
                y,
                width,
                height,
                GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE,
                imc2Buf
            )
            var curBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            curBmp.copyPixelsFromBuffer(imc2Buf)
            val matrix = Matrix()
            matrix.postScale(1f, -1f) //镜像垂直翻转
            matrix.postRotate(degress.toFloat())
            curBmp = Bitmap.createBitmap(curBmp, 0, 0, width, height, matrix, true)
            curBmp
        } else {
            null
        }
    }

    fun bmpToTexture1(bmp: Bitmap): Int {
        val texture = IntArray(1)
        GLES30.glGenTextures(1, texture, 0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture[0])
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)

        bmpBufferDataToTexture(bmp)
        bmp.recycle()
        return texture[0]
    }

    fun bmpToTexture2(bmp: Bitmap): Int {
        val texture = IntArray(1)
        GLES30.glGenTextures(1, texture, 0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture[0])
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)

        bmpBufferDataToTexture1(bmp)
        bmp.recycle()
        return texture[0]
    }

    private fun bmpBufferDataToTexture(bitmap: Bitmap) {
        var textureBuffer = ByteBuffer.allocateDirect(bitmap.byteCount)
                .order(ByteOrder.nativeOrder())
        bitmap.copyPixelsToBuffer(textureBuffer)
        textureBuffer.position(0)

        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D,
                0, GLES30.GL_RGBA, bitmap.width, bitmap.height, 0,
                GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, textureBuffer)
    }

    private fun bmpBufferDataToTexture1(bitmap: Bitmap) = GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0)

    fun byteToTexture(byteArray: ByteArray,width: Int,height: Int,colorType:Int): Int {
        val texture = IntArray(1)
        GLES30.glGenTextures(1, texture, 0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture[0])
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)
        var textureBuffer = ByteBuffer.allocateDirect(byteArray.size)
            .order(ByteOrder.nativeOrder()).put(byteArray).position(0)

        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D,
            0, colorType, width, height, 0,
            colorType, GLES30.GL_UNSIGNED_BYTE, textureBuffer)
        return texture[0];
    }


    fun genFloatBufferData(data: FloatArray): FloatBuffer {
        return ByteBuffer.allocateDirect(data.size * FLOAT_SIZE)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(data)
                .position(0) as FloatBuffer
    }

    fun genShortBufferData(data: ShortArray): ShortBuffer {
        return ByteBuffer.allocateDirect(data.size * SHORT_SIZE)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
                .put(data)
                .position(0) as ShortBuffer
    }

    fun genIntBufferData(data: IntArray): IntBuffer {
        return ByteBuffer.allocateDirect(data.size * INT_SIZE)
                .order(ByteOrder.nativeOrder())
                .asIntBuffer()
                .put(data)
                .position(0) as IntBuffer
    }
}