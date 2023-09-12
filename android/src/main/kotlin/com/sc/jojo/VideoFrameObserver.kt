package com.sc.jojo

class VideoFrame(
    typeValue: Int,
    var width: Int,
    var height: Int,
    var yStride: Int,
    var uStride: Int,
    var vStride: Int,
    var yBuffer: ByteArray,
    var uBuffer: ByteArray,
    var vBuffer: ByteArray,
    var rotation: Int,
    var renderTimeMS: Long,
    var avSyncType: Int
) {
    enum class Type(value: Int) {
        YUV420(1),
        YUV422(16),
        RGBA(4),
        TEXTURE_OES(11),
    }

    var textureId: Int = -1

    constructor(width: Int, height: Int,
                textureId: Int,
                rotation: Int, renderTimeMS: Long, avSyncType: Int) :
                this(11,
                    width, height,
                    0, 0, 0,
                    ByteArray(0), ByteArray(0), ByteArray(0),
                    rotation, renderTimeMS, avSyncType) {
        this.textureId = textureId
    }

    lateinit var type: Type

    init {
        type = when (typeValue) {
            1 -> Type.YUV420
            16 -> Type.YUV422
            4 -> Type.RGBA
            11 -> Type.TEXTURE_OES
            else -> {
                throw IllegalArgumentException("BAD typeValue, should be 1, 16, 4")
            }
        }
    }

}

class NativeEngineHandler(private var nativeHandlerPtr: Long?,
                          private var delegate: VideoFrameObserverDelegate
) {
    private var nativeObserverPtr: Long = 0

    fun registerVideoFrameObserver(): Unit {
        if(nativeHandlerPtr?.toInt() != 0) {
           nativeRegisterVideoFrameObserver(nativeHandlerPtr!!)
        }
    }

    fun unregisterVideoFrameObserver(): Unit {
        if(nativeHandlerPtr?.toInt() != 0) {
            nativeUnregisterVideoFrameObserver(nativeHandlerPtr!!)
        }
    }

    private external fun nativeRegisterVideoFrameObserver(nativeHandlerPtr: Long): Unit
    private external fun nativeUnregisterVideoFrameObserver(nativeHandlerPtr: Long): Unit
}

abstract class VideoFrameObserverDelegate(nativeHandlerPtr: Long?) {
    private val nativeEngineHandler = NativeEngineHandler(nativeHandlerPtr, this)

    fun registerVideoFrameObserver() {
        nativeEngineHandler.registerVideoFrameObserver()
    }

    fun unregisterVideoFrameObserver() {
        nativeEngineHandler.unregisterVideoFrameObserver()
    }

    abstract fun onCaptureVideoFrame(sourceType: Int, videoFrame: VideoFrame): Boolean;

}