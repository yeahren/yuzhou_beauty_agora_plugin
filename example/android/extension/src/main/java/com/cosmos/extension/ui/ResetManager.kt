package com.cosmos.extension.ui

import com.cosmos.config_type.*

class ResetManager {
    var changeBeautyAndMicroByRenderOneKeyBeauty: ((OneKeyBeautyType, BeautyTabData, BeautyTabData) -> Unit)? =
        null
    var resetOneKeyBeauty: ((OneKeyBeautyType) -> Unit)? = null
    internal fun resetOneKeyBeauty(
        oneKeyBeautyTabData: OneKeyBeautyTabData,
        beautyTabData: BeautyTabData,
        microTabData: BeautyTabData
    ) {
        changeBeautyAndMicroByRenderOneKeyBeauty?.invoke(
            oneKeyBeautyTabData.list[oneKeyBeautyTabData.defaultSelectPosition].renderType as OneKeyBeautyType,
            beautyTabData,
            microTabData
        )
        oneKeyBeautyTabData.nowSelectPosition = oneKeyBeautyTabData.defaultSelectPosition
        resetOneKeyBeauty?.invoke(oneKeyBeautyTabData.list[oneKeyBeautyTabData.defaultSelectPosition].renderType as OneKeyBeautyType)
    }

    var resetBeauty: ((BeautyType) -> Unit)? = null
    var clearOneKeyBeautyByBeautyOrMicro: (() -> Unit)? = null
    internal fun resetBeauty(beautyTabData: BeautyTabData) {
        clearOneKeyBeautyByBeautyOrMicro?.invoke()
        for (beautyData in beautyTabData.list) {
            beautyData.setValues(beautyData.getDefaultValues())
            resetBeauty?.invoke(beautyData.renderType as BeautyType)
        }
        beautyTabData.nowSelectPosition = beautyTabData.defaultSelectPosition
    }

    var resetMicro: ((BeautyType) -> Unit)? = null
    internal fun resetMicro(mircoTabData: BeautyTabData) {
        clearOneKeyBeautyByBeautyOrMicro?.invoke()
        for (microData in mircoTabData.list) {
            microData.setValues(microData.getDefaultValues())
            resetBeauty?.invoke(microData.renderType as BeautyType)
        }
        mircoTabData.nowSelectPosition = mircoTabData.defaultSelectPosition
    }

    var resetMakeupStyle: (() -> Unit)? = null
    var clearMakeupByMakeupStyle: ((MakeupTabData) -> Unit)? = null
    internal fun resetMakeupStyle(
        makeupStyleTabData: MakeupStyleTabData,
        makeupTabData: MakeupTabData
    ) {
        clearMakeupByMakeupStyle?.invoke(makeupTabData)
        for (makeupStyleData in makeupStyleTabData.list) {
            makeupStyleData.setValues(makeupStyleData.getDefaultValues())
        }
        makeupStyleTabData.nowSelectPosition = makeupStyleTabData.defaultSelectPosition
        resetMakeupStyle?.invoke()
    }

    var resetMakeup: (() -> Unit)? = null
    var removeMakeupStyleByMakeup: (() -> Unit)? = null
    var resetLipTexture: ((makeupData: MakeupData) -> Unit)? = null
    internal fun resetMakeup(makeupTabData: MakeupTabData) {
        removeMakeupStyleByMakeup?.invoke()
        for (makeupData in makeupTabData.list) {
            makeupData.setValues(makeupData.getDefaultValues())
            makeupData.nowSelect = makeupData.defaultSelect
            resetLipTexture?.invoke(makeupData)
            for (innerData in makeupData.level2List) {
                innerData.setValues(innerData.getDefaultValues())
            }
            makeupTabData.nowSelectPosition = makeupTabData.defaultSelectPosition
        }
        resetMakeup?.invoke()
    }

    var resetLookup: ((LookupType) -> Unit)? = null
    internal fun resetLookup(lookupTabData: LookupTabData) {
        for (lookupData in lookupTabData.list) {
            lookupData.setValues(lookupData.getDefaultValues())
            resetLookup?.invoke(lookupData.renderType as LookupType)
        }
        lookupTabData.nowSelectPosition = lookupTabData.defaultSelectPosition
    }

    var resetSticker: (() -> Unit)? = null
    internal fun resetSticker(stickerTabData: StickerTabData) {
        for (stickerData in stickerTabData.list) {
            stickerData.setValues(stickerData.getDefaultValues())
        }
        stickerTabData.nowSelectPosition = stickerTabData.defaultSelectPosition
        resetSticker?.invoke()
    }

    var resetBeautyBody: (() -> Unit)? = null
    internal fun resetBeautyBody(stickerTabData: BeautyTabData) {
        for (stickerData in stickerTabData.list) {
            stickerData.setValues(stickerData.getDefaultValues())
        }
        stickerTabData.nowSelectPosition = stickerTabData.defaultSelectPosition
        resetBeautyBody?.invoke()
    }
}