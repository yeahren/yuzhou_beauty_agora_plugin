package com.cosmos.config_type

open class RenderType(open var id: Int) {
    fun getLevel1Id(): Int = if (isInLevel2()) id / 100 else id

    /**
     * 判断是不是在二级菜单
     */
    fun isInLevel2() = id > 99999

    /**
     * 判断是不是清空
     */
    open fun isClear() = when {
        isInLevel2() -> {
            id.rem(100) == 1
        }
        else -> {
            id.rem(100) == 0
        }
    }

    open fun getValues(): FloatArray = floatArrayOf(0f)
    open fun getDefaultValues(): FloatArray = floatArrayOf(0f)
    open fun setValuesForBeauty(value: Float) {}
    open fun setValuesForStyleLookup(value: Float) {}

    open fun prepareInLevel1() {}
    open fun renderInLevel1() {}
    open fun clearInLevel1() {}

    open fun prepareInLevel2() {}
    open fun renderInLevel2() {}
    open fun clearInLevel2() {}

    open fun renderForParam1ByDrag() {}
    open fun renderForParam2ByDrag() {}

    fun level1Type() = id.div(100)
}

/**
 * 一键美颜
 */
var renderOneKeyBeautyByClick: ((typeId: Int) -> Unit)? = null
var clearOneKeyBeautyByClick: (() -> Unit)? = null

class OneKeyBeautyType(override var id: Int) : RenderType(id) {
    override fun renderInLevel1() {
        renderOneKeyBeautyByClick?.invoke(id)
    }

    override fun clearInLevel1() {
        clearOneKeyBeautyByClick?.invoke()
    }
}

/**
 * 美颜
 */
var renderBeautyTypeByDrag: ((typeId: Int, value: Float) -> Unit)? = null
var prepareBeautyByClick: ((innerType: Int) -> Unit)? = null

class BeautyType(
    override var id: Int,
    var value: Float,
    val defaultValue: Float,
    var innerType: Int
) : RenderType(id) {
    override fun getValues(): FloatArray = floatArrayOf(value)
    override fun getDefaultValues(): FloatArray = floatArrayOf(defaultValue)
    override fun setValuesForBeauty(value: Float) {
        this.value = value
    }

    override fun prepareInLevel1() {
        if (innerType > 0) {
            prepareBeautyByClick?.invoke(innerType)
        }
    }

    override fun renderForParam1ByDrag() {
        renderBeautyTypeByDrag?.invoke(id, value)
    }
}

/**
 * 风格妆
 */
var renderMakeupStyleByClick: ((StyleMakeupType, StyleLookupType) -> Unit)? = null
var clearMakeupStyleByClick: (() -> Unit)? = null
var renderMakeupStyleMakeupByDrag: ((StyleMakeupType) -> Unit)? = null
var renderMakeupStyleLookupByDrag: ((StyleLookupType) -> Unit)? = null

class MakeupStyleType(
    override var id: Int,
    var styleMakeupType: StyleMakeupType,
    var styleLookupType: StyleLookupType
) : RenderType(id) {
    override fun getValues(): FloatArray =
        floatArrayOf(styleMakeupType.value, styleLookupType.value)

    override fun getDefaultValues(): FloatArray =
        floatArrayOf(styleMakeupType.defaultValue, styleLookupType.defaultValue)

    override fun setValuesForBeauty(value: Float) {
        styleMakeupType.value = value
    }

    override fun setValuesForStyleLookup(value: Float) {
        styleLookupType.value = value
    }

    override fun renderInLevel1() {
        renderMakeupStyleByClick?.invoke(styleMakeupType, styleLookupType)
    }

    override fun clearInLevel1() {
        clearMakeupStyleByClick?.invoke()
    }

    override fun renderForParam1ByDrag() {
        renderMakeupStyleMakeupByDrag?.invoke(styleMakeupType)
    }

    override fun renderForParam2ByDrag() {
        renderMakeupStyleLookupByDrag?.invoke(styleLookupType)
    }
}

class StyleMakeupType(var path: String, var value: Float, var defaultValue: Float)
class StyleLookupType(var value: Float, var defaultValue: Float)

/**
 * 美妆
 */

var prepareMakeupTypeByClick: ((typeId: Int, path: String) -> Unit)? = null
var renderMakeupTypeByClick: ((typeId: Int, value: Float) -> Unit)? = null
var clearMakeupTypeByClick: ((typeId: Int) -> Unit)? = null

var renderMakeupTypeByDrag: ((typeId: Int, value: Float) -> Unit)? = null

class MakeupType(override var id: Int, var value: Float, val defaultValue: Float) : RenderType(id) {
    override fun getValues(): FloatArray = floatArrayOf(value)
    override fun getDefaultValues(): FloatArray = floatArrayOf(defaultValue)
    override fun setValuesForBeauty(value: Float) {
        this.value = value
    }
}

class MakeupInnerType(
    override var id: Int,
    var path: String,
    var value: Float,
    val defaultValue: Float
) : RenderType(id) {
    override fun getValues(): FloatArray = floatArrayOf(value)
    override fun getDefaultValues(): FloatArray = floatArrayOf(defaultValue)
    override fun setValuesForBeauty(value: Float) {
        this.value = value
    }

    override fun renderForParam1ByDrag() {
        renderMakeupTypeByDrag?.invoke(getLevel1Id(), value)
    }

    override fun prepareInLevel2() {
        prepareMakeupTypeByClick?.invoke(getLevel1Id(), path)
    }

    override fun renderInLevel2() {
        renderMakeupTypeByClick?.invoke(getLevel1Id(), value)
    }

    override fun clearInLevel2() {
        clearMakeupTypeByClick?.invoke(getLevel1Id())
    }
}

/**
 * 滤镜
 */
var renderLookupByClick: ((path: String, value: Float) -> Unit)? = null
var clearLookupByClick: (() -> Unit)? = null

var renderLookupByDrag: ((value: Float) -> Unit)? = null

class LookupType(
    override var id: Int,
    var path: String,
    var value: Float,
    var defaultValue: Float
) : RenderType(id) {
    override fun getValues(): FloatArray = floatArrayOf(value)
    override fun getDefaultValues(): FloatArray = floatArrayOf(defaultValue)
    override fun setValuesForBeauty(value: Float) {
        this.value = value
    }

    override fun renderInLevel1() {
        renderLookupByClick?.invoke(path, value)
    }

    override fun clearInLevel1() {
        clearLookupByClick?.invoke()
    }

    override fun renderForParam1ByDrag() {
        renderLookupByDrag?.invoke(value)
    }
}

/**
 * 贴纸
 */
var renderStickerByClick: ((path: String) -> Unit)? = null
var clearStickerByClick: (() -> Unit)? = null

class StickerType(
    override var id: Int,
    var path: String
) : RenderType(id) {
    override fun renderInLevel1() {
        renderStickerByClick?.invoke(path)
    }

    override fun clearInLevel1() {
        clearStickerByClick?.invoke()
    }
}

/**
 * 贴纸
 */
var onClearBeautyBodyClickListener: ((innerType: Int, value: Float) -> Unit)? = null

class BeautyBodyType(
    var innerType: Int,
    var value: Float,
    val defaultValue: Float,
) : RenderType(innerType) {

    override fun renderForParam1ByDrag() {
        onClearBeautyBodyClickListener?.invoke(innerType, value)
    }

    override fun setValuesForBeauty(value: Float) {
        this.value = value
    }

    override fun getValues(): FloatArray = floatArrayOf(value)
    override fun isClear() = false
}