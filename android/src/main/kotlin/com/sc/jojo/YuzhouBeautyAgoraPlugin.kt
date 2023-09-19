package com.sc.jojo

import android.app.Activity
import android.graphics.Matrix
import android.util.Log
import com.cosmos.beauty.module.IMMRenderModuleManager
import com.cosmos.beauty.module.beauty.AutoBeautyType
import com.cosmos.beauty.module.beauty.IBeautyModule
import com.cosmos.beauty.module.beauty.MakeupType
import com.cosmos.beauty.module.beauty.SimpleBeautyType
import com.cosmos.beauty.module.makeup.IMakeupBeautyModule
import com.cosmos.beauty.module.sticker.IStickerModule
import com.cosmos.camera.util.ImageFrame
import com.sc.jojo.VideoFrame
import com.sc.jojo.VideoFrameObserverDelegate
import com.sc.jojo.beauty.SdkApi
import com.sc.jojo.json_load.ConfigLoader
import com.sc.jojo.sdkApi

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

import io.agora.base.TextureBufferHelper
import io.agora.rtc2.gl.EglBaseProvider
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.github.crow_misia.libyuv.I422Buffer
import io.github.crow_misia.libyuv.Nv21Buffer
import java.io.File
import java.nio.ByteBuffer
import kotlin.concurrent.thread

/** YuzhouBeautyAgoraPlugin */
class YuzhouBeautyAgoraPlugin: FlutterPlugin, ActivityAware, MethodCallHandler {
    private val TAG = "YuzhouBeautyAgoraPlugin"
    private var license: String = ""
    private lateinit var channel : MethodChannel
    private var videoFrameObserverDelegate: VideoFrameObserverDelegate? = null
    private lateinit var textureBufferHelper: TextureBufferHelper
    private lateinit var videoByteBuffer: ByteBuffer
    private lateinit var videoNV21ByteArray: ByteArray
    private var activity: Activity? = null
    private var isSdkApiInit = false
    private var turnOn = false

    val renderModuleManager: IMMRenderModuleManager?
        get() = sdkApi?.renderModuleManager
    val beautyModule: IBeautyModule?
        get() = sdkApi?.beautyModule
    val makeupModule: IMakeupBeautyModule?
        get() = sdkApi?.makeupModule
    val stickerModule: IStickerModule?
        get() = sdkApi?.stickerModule

    private val configLoader: ConfigLoader by lazy { ConfigLoader() }

    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("yuzhou_beauty_agora_plugin")
        }
    }

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "yuzhou_beauty_agora_plugin")
        channel.setMethodCallHandler(this)
    }

    fun APIInit(): Unit {
        APIDispose()

        sdkApi = SdkApi()

        APISetLicense(this.license)
    }

    fun APIDispose(): Unit {
        videoFrameObserverDelegate?.unregisterVideoFrameObserver()
        videoFrameObserverDelegate = null

        sdkApi?.unInit()
        sdkApi = null
        isSdkApiInit = false
    }

    fun APISetLicense(license: String) {
        if(sdkApi?.license == license)
            return

        this.license = license
        sdkApi?.license = license

        sdkApi?.init(AppContext) {
            this.isSdkApiInit = true
            Log.i(TAG, "sdkApi.init success")
        }

    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        when(call.method) {
            "init" -> {
                APIInit()

                return result.success(true)
            }

            "dispose" -> {
                APIDispose()

                return result.success(true)
            }

            "setLicense" -> {
                APISetLicense(call.arguments as String)

                return result.success(true)
            }

            "turnOnBeauty" -> {
                if(!isSdkApiInit)
                    return result.success(false)

                turnOn = true

                if(videoFrameObserverDelegate == null) {
                    videoFrameObserverDelegate = object: VideoFrameObserverDelegate(call.arguments as Long) {
                        override fun onCaptureVideoFrame(
                            sourceType: Int, videoFrame: VideoFrame
                        ): Boolean {

                            if(!turnOn)
                                return true

                            if (!this@YuzhouBeautyAgoraPlugin::textureBufferHelper.isInitialized) {
                                textureBufferHelper = TextureBufferHelper.create(
                                    "--",
                                    EglBaseProvider.instance().localEglBaseContext
                                )!!
                            }

                            val width = videoFrame.width
                            val height = videoFrame.height

                            if(videoFrame.type == VideoFrame.Type.TEXTURE_OES) {
                                var textureId = -1
                                textureId = textureBufferHelper.invoke {
                                    var resultTexture = -1
                                    resultTexture = renderModuleManager!!.renderOESFrame(
                                        videoFrame.textureId,
                                        width,
                                        height,
                                        0,
                                        false,
                                        false
                                    )

                                    return@invoke resultTexture
                                }

                                videoFrame.textureId = textureId
                            }
                            else {

                                val bufferY = videoFrame.yBuffer
                                val bufferU = videoFrame.uBuffer
                                val bufferV = videoFrame.vBuffer

                                if (!this@YuzhouBeautyAgoraPlugin::videoByteBuffer.isInitialized) {
                                    val chromaWidth = (width + 1) / 2;
                                    val chromaHeight = (height + 1) / 2;
                                    val minSize = width * height + chromaWidth * chromaHeight * 2

                                    videoByteBuffer = ByteBuffer.allocateDirect(minSize)
                                    videoNV21ByteArray = ByteArray(minSize)
                                }

                                //https://github.com/crow-misia/libyuv-android

                                val i422ByteBuffer = ByteBuffer.allocateDirect(
                                    bufferY.size + bufferU.size + bufferV.size
                                )

                                i422ByteBuffer.put(bufferY)
                                i422ByteBuffer.put(bufferU)
                                i422ByteBuffer.put(bufferV)
                                i422ByteBuffer.position(0)

                                val nv21Buffer = Nv21Buffer.allocate(width, height)
                                val i422Buffer = I422Buffer.wrap(i422ByteBuffer, width, height)

                                i422Buffer.convertTo(nv21Buffer)

                                var textureId = -1
                                textureId = textureBufferHelper.invoke {
                                    var resultTexture = -1
                                    resultTexture = renderModuleManager!!.renderFrame(
                                        nv21Buffer.asByteArray(),
                                        width,
                                        height,
                                        ImageFrame.MMFormat.FMT_NV21,
                                        0,
                                        false,
                                        false
                                    )

                                    return@invoke resultTexture
                                }

                                if (textureId == -1) {
                                    return true;
                                }

                                val textureBuffer = textureBufferHelper.wrapTextureBuffer(
                                    videoFrame.width,
                                    videoFrame.height,
                                    io.agora.base.VideoFrame.TextureBuffer.Type.RGB,
                                    textureId,
                                    Matrix()
                                )

                                val returnI420 = textureBuffer.toI420()

                                val arrDataY = ByteArray(returnI420.dataY.remaining())
                                returnI420.dataY.get(arrDataY)
                                arrDataY.copyInto(videoFrame.yBuffer)

                                val arrDataU = ByteArray(returnI420.dataU.remaining())
                                returnI420.dataU.get(arrDataU)
                                arrDataU.copyInto(videoFrame.uBuffer)

                                val arrDataV = ByteArray(returnI420.dataV.remaining())
                                returnI420.dataV.get(arrDataV)
                                arrDataV.copyInto(videoFrame.vBuffer)

                                returnI420.release()
                                textureBuffer.release()

                                i422Buffer.close()
                                nv21Buffer.close()

                            }

                            return true;
                        }
                    }

                    videoFrameObserverDelegate?.registerVideoFrameObserver()
                }

                return result.success(true)
            }

            "turnOffBeauty" -> {
                if(!isSdkApiInit)
                    return result.success(false)

                turnOn = false

                return result.success(true)
            }

            "setSimpleBeautyValue" -> {
                if(!isSdkApiInit  || !turnOn)
                    return result.success(false)

                val type = call.argument<String>("type")!!
                val value = call.argument<Float>("value")!!
                val simpleBeautyTypeValue = SimpleBeautyType.valueOf(type)
                Log.i(TAG, "setSimpleBeautyValue:${type}, ${simpleBeautyTypeValue}-${simpleBeautyTypeValue.ordinal}, ${value}")

                beautyModule?.setValue(simpleBeautyTypeValue, value)

                return result.success(true)
            }

            "setMakeup" -> {
                if(!isSdkApiInit || !turnOn)
                    return result.success(false)

                val path = call.argument<String>("path")!!
                val style_value = call.argument<Float>("style_value")!!
                val lut_value = call.argument<Float>("lut_value")!!
                Log.i(TAG, "setMarkup:${path}, ${style_value}, ${lut_value}")

                makeupModule?.clear()
                makeupModule?.addMakeup(path)
                makeupModule?.setValue(MakeupType.MAKEUP_STYLE, style_value)
                makeupModule?.setValue(MakeupType.MAKEUP_LUT, lut_value)

                return result.success(true)
            }

            "setSticker" -> {
                if(!isSdkApiInit || !turnOn)
                    return result.success(false)

                val path = call.argument<String>("path")!!
                Log.i(TAG, "setSticker:${path}")

                stickerModule?.clear()
                stickerModule?.addMaskModel(File(path)) {
                }

                return result.success(true)
            }

            "clearMakeup" -> {
                if(!isSdkApiInit || !turnOn)
                    return result.success(false)

                makeupModule?.clear()

                return result.success(true)
            }

            "clearSticker" -> {
                if(!isSdkApiInit || !turnOn)
                    return result.success(false)

                stickerModule?.clear()

                return result.success(true)
            }

            else -> {
                result.notImplemented()
            }
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        this.activity = binding.activity

        AppContext = binding.activity.applicationContext

        configLoader.setContext(AppContext)

        thread {
            configLoader.loadCosmosZip { copySuccess, unZipSuccess ->
                if (copySuccess && unZipSuccess) {

                }
                else {
                    Log.i("TEST", "loadCosmosZip failed")
                }
            }
        }
    }

    override fun onDetachedFromActivityForConfigChanges() {
        this.activity = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        this.activity = binding.activity
    }

    override fun onDetachedFromActivity() {
        this.activity = null
    }


}
