package com.cosmos.extension.ui

import com.cosmos.config_type.OneKeyBeautyType
import com.cosmos.config_type.*

class LinkEffectManager {

    //*********** Level1 *************
    /**
     * 点击 一键美颜 会改变 美颜和微整形
     */
    var changeBeautyAndMicroByRenderOneKeyBeauty: ((OneKeyBeautyType) -> Map<Int, Float>?)? = null
    internal fun changeBeautyAndMicroByRenderOneKeyBeauty(
        renderType: OneKeyBeautyType,
        beautyTabData: BeautyTabData,
        microTabData: BeautyTabData
    ) {
        val map = changeBeautyAndMicroByRenderOneKeyBeauty?.invoke(renderType)
        if (map != null) {
            for (beautyData in beautyTabData.level1List()) {
                map[beautyData.getTypeId()]?.apply {
                    beautyData.setValues(floatArrayOf(this))
                }
            }
            for (microData in microTabData.level1List()) {
                map[microData.getTypeId()]?.apply {
                    microData.setValues(floatArrayOf(this))
                }
            }
        }
    }

    internal fun clearBeautyAndMicroByRenderOneKeyBeauty(
        beautyTabData: BeautyTabData,
        microTabData: BeautyTabData
    ) {
        for (beautyData in beautyTabData.level1List()) {
            beautyData.setValues(floatArrayOf(0f))
        }
        for (microData in microTabData.level1List()) {
            microData.setValues(floatArrayOf(0f))
        }
    }

    /**
     * 在切换风格妆的时候 会移除 滤镜
     */
    var removeLookupByMakeupStyle: (() -> Unit)? = null
    internal fun removeLookupByMakeupStyle(lookupTabData: LookupTabData) {
        lookupTabData.nowSelectPosition = 0
        removeLookupByMakeupStyle?.invoke()
    }

    /**
     * 点击风格妆的时候 会清空 美妆
     */
    var clearMakeupByMakeupStyle: (() -> Unit)? = null
    internal fun clearMakeupByMakeupStyle(makeupTabData: MakeupTabData) {
        for (makeupData in makeupTabData.list) {
            makeupData.setValues(floatArrayOf(0f))
            makeupData.nowSelect = 1
            for (innerData in makeupData.level2List) {
                innerData.setValues(floatArrayOf(0f))
                innerData.nowSelect = innerData.defaultSelect
            }
            makeupTabData.nowSelectPosition = makeupTabData.defaultSelectPosition
        }
        clearMakeupByMakeupStyle?.invoke()
    }

    //************ Level2 *************
    /**
     * 渲染 美妆 会移除 风格妆
     */
    var removeMakeupStyleByMakeup: (() -> Unit)? = null
    internal fun removeMakeupStyleByMakeup() {
        removeMakeupStyleByMakeup?.invoke()
    }

    //************** SeekBar *****************
    /**
     * 渲染 美颜和微整形 会清空 一键美颜
     */
    var clearOneKeyBeautyByBeautyOrMicro: (() -> Unit)? = null
    internal fun clearOneKeyBeautyByBeautyOrMicro() {
        clearOneKeyBeautyByBeautyOrMicro?.invoke()
    }

    //*********** EffectInit ******************
    /**
     * 点击 一键美颜 会改变 美颜和微整形
     */
    var changeBeautyAndMicroByRenderOneKeyBeautyInEffectInit: ((position: Int) -> Map<Int, Float>?)? =
        null

    fun changeBeautyAndMicroByRenderOneKeyBeauty(
        position: Int,
        beautyTabData: BeautyTabData,
        microTabData: BeautyTabData
    ) {
        val map = changeBeautyAndMicroByRenderOneKeyBeautyInEffectInit?.invoke(position)
        if (map != null) {
            for (beautyData in beautyTabData.level1List()) {
                map[beautyData.getTypeId()]?.apply {
                    beautyData.setValues(floatArrayOf(this))
                }
            }
            for (microData in microTabData.level1List()) {
                map[microData.getTypeId()]?.apply {
                    microData.setValues(floatArrayOf(this))
                }
            }
        }
    }
}