package com.sc.jojo.beauty

import android.content.Context
import com.cosmos.baseutil.toast.Toaster
import com.cosmos.beauty.CosmosBeautySDK
import com.cosmos.beauty.module.IMMRenderModuleManager
import com.cosmos.beauty.module.beauty.IBeautyBodyModule
import com.cosmos.beauty.module.beauty.IBeautyModule
import com.cosmos.beauty.module.lookup.ILookupModule
import com.cosmos.beauty.module.makeup.IMakeupBeautyModule
import com.cosmos.beauty.module.sticker.DetectRect
import com.cosmos.beauty.module.sticker.IStickerModule
import com.sc.jojo.json_load.ConfigLoader
import com.cosmoscv.SingleFaceInfo

class SdkApi {
    /**
     * 美颜SDK 相关
     */
    var renderModuleManager: IMMRenderModuleManager? = null
    var lookupModule: ILookupModule? = null
    var makeupModule: IMakeupBeautyModule? = null
    var beautyModule: IBeautyModule? = null
    var stickerModule: IStickerModule? = null
    var beautyBodyModule: IBeautyBodyModule? = null

    @Volatile
    var authSuccess = false
        set(value) {
            field = value
            if (value)
                initRenderManager()
        }

    var callback: (() -> Unit)? = null
    fun init(context: Context, callback: () -> Unit) {
        this.callback = callback
        initSDK(context)
    }

    var license = ""

    private fun initSDK(context: Context) {
        val result = CosmosBeautySDK.init(
            context, license, ConfigLoader.MMCV_ROOT.absolutePath
        )
        if (!result.isSucceed) {
            Toaster.show("授权失败 ${result.msg}")
        } else {
            authSuccess = true
        }
    }

    private fun initRenderManager() {
        renderModuleManager = CosmosBeautySDK.createRenderModuleManager()
        renderModuleManager?.prepare(
            true,
            iDetectGestureCallback,
            iDetectFaceCallback
        )
        initModules()
    }

    private val iDetectGestureCallback = object : IMMRenderModuleManager.IDetectGestureCallback {
        override fun onDetectGesture(type: String, detect: DetectRect) {
        }

        override fun onGestureMiss() {
        }
    }

    private val iDetectFaceCallback = object : IMMRenderModuleManager.IDetectFaceCallback {
        override fun onDetectFace(faceCount: Int) {
        }

        override fun onDetectFaceFeatures(faceFeatures: Array<SingleFaceInfo>?) {
        }
    }

    /**
     * 初始化Module
     */
    private fun initModules() {
        initBeautyModule()
        initMakeupModule()
        initLookupModule()
        initStickerModule()
        initBeautyBodyModule()
        callback?.invoke()
    }

    private fun initBeautyBodyModule() {
        beautyBodyModule = CosmosBeautySDK.createBeautyBodyModule()
        renderModuleManager?.registerModule(beautyBodyModule!!)
    }

    private fun initBeautyModule() {
        beautyModule = CosmosBeautySDK.createBeautyModule()
        renderModuleManager?.registerModule(beautyModule!!)
    }

    private fun initMakeupModule() {
        makeupModule = CosmosBeautySDK.createMakeupBeautyModule()
        renderModuleManager?.registerModule(makeupModule!!)
    }

    private fun initLookupModule() {
        lookupModule = CosmosBeautySDK.createLoopupModule()
        renderModuleManager?.registerModule(lookupModule!!)
    }

    private fun initStickerModule() {
        stickerModule = CosmosBeautySDK.createStickerModule()
        renderModuleManager?.registerModule(stickerModule!!)
    }
}