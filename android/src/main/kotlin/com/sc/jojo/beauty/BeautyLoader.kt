package com.sc.jojo.beauty

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.ArrayMap
import com.cosmos.beauty.filter.BeautyType
import com.cosmos.beauty.module.beauty.MakeupType
import com.cosmos.beauty.module.IMMRenderModuleManager
import com.cosmos.beauty.module.beauty.AutoBeautyType
import com.cosmos.beauty.module.beauty.IBeautyBodyModule
import com.cosmos.beauty.module.beauty.IBeautyModule
import com.cosmos.beauty.module.lookup.ILookupModule
import com.cosmos.beauty.module.makeup.IMakeupBeautyModule
import com.cosmos.beauty.module.sticker.IStickerModule
import com.sc.jojo.dLog
import com.sc.jojo.json_load.ConfigLoader
import com.sc.jojo.json_load.JsonParser
import com.sc.jojo.toast
import com.sc.jojo.wLog
import com.cosmos.config_type.*
import com.cosmos.extension.ui.PanelFragment
import java.io.File
import kotlin.concurrent.thread

class BeautyLoader {
    private lateinit var context: Context
    private val sdkApi: SdkApi = SdkApi()
    val lookupModule: ILookupModule?
        get() = sdkApi.lookupModule
    val makeupModule: IMakeupBeautyModule?
        get() = sdkApi.makeupModule
    val beautyModule: IBeautyModule?
        get() = sdkApi.beautyModule
    val stickerModule: IStickerModule?
        get() = sdkApi.stickerModule
    val bodyModule: IBeautyBodyModule?
        get() = sdkApi.beautyBodyModule
    val renderModuleManager: IMMRenderModuleManager?
        get() = sdkApi.renderModuleManager

    lateinit var panelFragment: PanelFragment
    var toShowFragment: (() -> Unit)? = null

    internal var render = true
    private lateinit var beautyTabDataList: List<TabData>
    private lateinit var stickerTabDataList: List<TabData>
    private val configLoader: ConfigLoader by lazy { ConfigLoader() }
    private val jsonParser: JsonParser by lazy { JsonParser() }


    @Volatile
    var uiReady = false

    fun start() {
        thread {
            initTypeBehavior()
            loadConfig {
                if (it) {
                    initFragment()
                    sdkApi.init(context) {
                        Handler(Looper.getMainLooper()).post {
                            toShowFragment?.invoke()
                        }
                    }
                    Handler(Looper.getMainLooper()).post {
                        toShowFragment?.invoke()
                    }
                }
            }
        }
    }


    fun setContext(context: Context) {
        this.context = context
        configLoader.setContext(context)
        jsonParser.context = context
        panelFragment = PanelFragment.newInstance(configLoader.rootDir)
    }

    private fun loadConfig(loadSuccess: (Boolean) -> Unit) {
        thread {
            configLoader.loadCosmosZip { copySuccess, unZipSuccess ->
                if (copySuccess && unZipSuccess) {
                    beautyTabDataList = jsonParser.initBeautyTabDataList()
                    stickerTabDataList = jsonParser.initStickerTabDataList()
                    Handler(Looper.getMainLooper()).post {
                        loadSuccess(true)
                    }
                } else {
                    Handler(Looper.getMainLooper()).post {
                        loadSuccess(false)
                    }
                }
            }
        }
    }

    private fun initFragment() {
        panelFragment.renderCompareOnTouchDownListener = { render = false }
        panelFragment.renderCompareOnTouchUpListener = { render = true }
        panelFragment.beautyTabDataList = { beautyTabDataList }
        panelFragment.stickerTabDataList = { stickerTabDataList }
        panelFragment.initBeautyPanelFragment = { beautyPanelFragment ->
            beautyPanelFragment.changeBeautyAndMicroByRenderOneKeyBeauty = { oneKeyBeautyType ->
                val resultMap = ArrayMap<Int, Float>()
                OneKeyBeautyTypeMap[oneKeyBeautyType.id]?.let { typeId ->
                    beautyModule?.getAutoValues(typeId)?.forEach { entry ->
                        resultMap[BeautyTypeReverseMap[entry.key]] = entry.value
                    }
                }
                resultMap
            }
            beautyPanelFragment.initEffect = { oneKeyBeautyType, lookupType ->
                renderOneKeyBeauty(oneKeyBeautyType)
                renderLookupDirectly(lookupType)
            }
            beautyPanelFragment.prepareInLevel1 = { it.prepareInLevel1() }
            beautyPanelFragment.renderInLevel1 = { it.renderInLevel1() }
            beautyPanelFragment.clearInLevel1 = { it.clearInLevel1() }
            beautyPanelFragment.removeLookupByMakeupStyle = { lookupModule?.clear() }
            beautyPanelFragment.clearMakeupByMakeupStyle = { makeupModule?.clear() }
            beautyPanelFragment.prepareInLevel2 = { it.prepareInLevel2() }
            beautyPanelFragment.renderInLevel2 = { it.renderInLevel2() }
            beautyPanelFragment.clearInLevel2 = { it.clearInLevel2() }
            beautyPanelFragment.removeMakeupStyleByMakeup = {
                makeupModule?.clear()
            }
            beautyPanelFragment.beautySeekBarRender = {
                it.renderForParam1ByDrag()
            }
            beautyPanelFragment.filterSeekBarRender = { it.renderForParam2ByDrag() }
            beautyPanelFragment.resetOneKeyBeauty = {
                OneKeyBeautyTypeMap[it.id]?.let { type ->
                    beautyModule?.setAutoBeauty(type)
                }
            }
            beautyPanelFragment.resetBeauty = {
                BeautyInnerTypeMap[it.innerType]?.let { beautyType ->
                    when (beautyType) {
                        is BeautyType.WHITETYPE -> beautyModule?.setWhiteType(beautyType)
                        is BeautyType.RUDDYTYPE -> beautyModule?.setRuddyType(beautyType)
                        else -> {
                        }
                    }
                }
                MakeupTypeMap[it.id]?.let { simpleBeautyType ->
                    makeupModule?.setValue(simpleBeautyType, it.value)
                }
            }
            beautyPanelFragment.resetMakeupStyle = {
                makeupModule?.clear()
            }
            beautyPanelFragment.resetMakeupInner = {
                makeupModule?.clear()
            }
            beautyPanelFragment.resetLookup = {
                lookupModule?.clear()
                lookupModule?.setEffect(it.path.toAbPath())
                lookupModule?.setIntensity(it.value)
            }
            beautyPanelFragment.lipTextTureToChange = {
                MakeupLipTextureMap[it]?.let { lipTexture ->
                    makeupModule?.changeLipTextureType(lipTexture)
                }
            }
        }
        panelFragment.initStickerPanelFragment = {
            it.renderInLevel1 = { renderType ->
                renderType.renderInLevel1()
            }
            it.clearInLevel1 = { renderType ->
                renderType.clearInLevel1()
            }
            it.resetSticker = {
                stickerModule?.clear()
            }
        }
    }

    private fun initTypeBehavior() {
        /**
         * 一键美颜
         */
        renderOneKeyBeautyByClick = {
            OneKeyBeautyTypeMap[it]?.let { type ->
                beautyModule?.setAutoBeauty(type)
            }
        }
        clearOneKeyBeautyByClick = {
            beautyModule?.setAutoBeauty(AutoBeautyType.AUTOBEAUTY_NULL)
        }
        /**
         * 美颜
         */
        prepareBeautyByClick = { innerType ->
            BeautyInnerTypeMap[innerType]?.let { beautyType ->
                when (beautyType) {
                    is BeautyType.WHITETYPE -> beautyModule?.setWhiteType(beautyType)
                    is BeautyType.RUDDYTYPE -> beautyModule?.setRuddyType(beautyType)
                }
            }
        }
        renderBeautyTypeByDrag = { type, value ->
            MakeupTypeMap[type]?.let {
                makeupModule?.setValue(it, value)
            }
            BeautyTypeMap[type]?.let {
                beautyModule?.setValue(it, value)
            }
        }
        /**
         * 风格妆
         */
        renderMakeupStyleByClick = { styleMakeupType, styleLookupType ->
            makeupModule?.clear()
            makeupModule?.addMakeup(styleMakeupType.path.toAbPath())
            makeupModule?.setValue(MakeupType.MAKEUP_STYLE, styleMakeupType.value)
            makeupModule?.setValue(MakeupType.MAKEUP_LUT, styleLookupType.value)
        }
        clearMakeupStyleByClick = {
            makeupModule?.clear()
        }
        renderMakeupStyleMakeupByDrag = { makeupType ->
            makeupModule?.setValue(MakeupType.MAKEUP_STYLE, makeupType.value)
        }
        renderMakeupStyleLookupByDrag = { lookupType ->
            makeupModule?.setValue(MakeupType.MAKEUP_LUT, lookupType.value)
        }

        /**
         * 美妆
         */
        prepareMakeupTypeByClick = { type, path ->
            MakeupTypeMap[type]?.let { simpleBeautyType ->
                wLog("prepareMakeupTypeByClick add makeup ${simpleBeautyType.name}")
                makeupModule?.addMakeup(path.toAbPath())
            }
        }
        renderMakeupTypeByClick = { type, value ->
            MakeupTypeMap[type]?.let { simpleBeautyType ->
                wLog("renderMakeupTypeByClick makeup ${simpleBeautyType.name} setValue")
                makeupModule?.setValue(simpleBeautyType, value)
            }
        }
        clearMakeupTypeByClick = { type ->
            MakeupTypeMap[type]?.let { simpleBeautyType ->
                wLog("clearMakeupTypeByClick remove makeup ${simpleBeautyType.name}")
                makeupModule?.removeMakeup(simpleBeautyType)
            }
        }
        renderMakeupTypeByDrag = { typeId, value ->
            MakeupTypeMap[typeId]?.let { simpleBeautyType ->
                wLog("renderMakeupTypeByDrag makeup ${simpleBeautyType.name} setValue")
                makeupModule?.setValue(simpleBeautyType, value)
            }
        }
        /**
         * 滤镜
         */
        renderLookupByClick = { path, value ->
            lookupModule?.clear()
            lookupModule?.setEffect(path.toAbPath())
            lookupModule?.setIntensity(value)
        }
        clearLookupByClick = {
            lookupModule?.clear()
        }
        renderLookupByDrag = { value ->
            lookupModule?.setIntensity(value)
        }
        /**
         * 贴纸
         */
        renderStickerByClick = { path ->
            dLog("贴纸 $path")
            stickerModule?.clear()
            stickerModule?.addMaskModel(File(path.toAbPath())) {
                dLog("${it == null}")
                toast("${it.name} add ${if (it == null) "failed" else "success"}")
            }
        }
        clearStickerByClick = {
            stickerModule?.clear()
        }

        /**
         * 美体
         */
        onClearBeautyBodyClickListener = { innerType, value ->
            bodyModule?.setValue(BeautyBodyStyleLookupTypeMap[innerType]!!, value)
        }
    }

    private fun renderOneKeyBeauty(oneKeyBeautyType: OneKeyBeautyType) {
        OneKeyBeautyTypeMap[oneKeyBeautyType.id]?.let {
            beautyModule?.setAutoBeauty(it)
        }
    }

    private fun renderLookupDirectly(lookupType: LookupType) {
        lookupModule?.setEffect(lookupType.path.toAbPath())
        lookupModule?.setIntensity(lookupType.value)
    }

    private fun String.toAbPath() = "${configLoader.rootDir}/$this"
}