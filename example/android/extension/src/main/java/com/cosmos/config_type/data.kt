package com.cosmos.config_type

import com.google.gson.JsonObject

open class Data {
    var name: String = ""
    var renderType: RenderType = RenderType(-1)//不在json里面配置，通过 type:JsonObject 分析出来
    var srcSelectedPath: String = ""
    var srcNotSelectedPath: String = ""

    fun getTypeId(): Int = renderType.id
    fun getLevel1Id(): Int = if (isInLevel2()) getTypeId() / 100 else getTypeId()

    fun isInLevel2() = getTypeId() > 99999

    fun isBackBtn(): Boolean = when {
        isInLevel2() -> {
            getTypeId().rem(100) == 0
        }
        else -> false
    }

    fun isClear() = when {
        isInLevel2() -> {
            getTypeId().rem(100) == 1
        }
        else -> {
            getTypeId().rem(100) == 0
        }
    }

    fun hasNextLevel(): Boolean = renderType is MakeupType

    open fun seekBarCount(): Int = 0
    open fun showValue(): Boolean = false
    open fun bgType(): String = ""
    open fun shape(): String = ""
    open fun getStandardProgress(): IntArray = intArrayOf(0)
    open fun getRealProgress(): IntArray = intArrayOf(0)
    open fun getValues(): IntArray = intArrayOf(0)
    open fun getDefaultValues(): FloatArray = floatArrayOf(0f)
    open fun setValues(values: FloatArray) {}
    open fun setValuesByProgressForBeauty(progress: Int) {}
    open fun setValuesByProgressForStyleLookup(progress: Int) {}
}

class OneKeyBeautyData : Data() {
    var type: JsonObject? = null
    var seekBarCount: Int = 0
    var showValue: Boolean = false
    private val shape: String get() = "circle"
    private val bgType: String get() = if (isClear()) "icon" else "photo"
    override fun seekBarCount(): Int = seekBarCount
    override fun showValue(): Boolean = showValue
    override fun bgType(): String = bgType
    override fun shape(): String = shape
}


class BeautyData : Data() {
    var type: JsonObject? = null
    var seekBarCount: Int = 1
    var showValue: Boolean = true
    private val shape: String get() = "circle"
    private val bgType: String get() = "icon"
    override fun seekBarCount(): Int = seekBarCount
    override fun showValue(): Boolean = showValue
    override fun bgType(): String = bgType
    override fun shape(): String = shape
    override fun getStandardProgress(): IntArray {
        val range = getDataRange(getTypeId())
        return intArrayOf(
            dataProgressToStandardProgress(
                range.max,
                range.min,
                renderType.getValues()[0]
            )
        )
    }

    override fun getRealProgress(): IntArray = intArrayOf((renderType.getValues()[0] * 100).toInt())
    override fun getDefaultValues(): FloatArray = renderType.getDefaultValues()
    override fun setValuesByProgressForBeauty(progress: Int) {
        val range = getDataRange(renderType.id)
        val value = standardProgressToDataProgress(range.max, range.min, progress)
        renderType.setValuesForBeauty(value)
    }

    override fun setValues(values: FloatArray) {
        renderType.setValuesForBeauty(values[0])
    }
}

class MakeupStyleData : Data() {
    var type: JsonObject? = null
    private val seekBarCount: Int get() = if (isClear()) 0 else 2
    var showValue: Boolean = false
    private val shape: String get() = "circle"
    private val bgType: String get() = if (isClear()) "icon" else "photo"
    override fun seekBarCount(): Int = seekBarCount
    override fun showValue(): Boolean = showValue
    override fun bgType(): String = bgType
    override fun shape(): String = shape
    override fun getStandardProgress(): IntArray {
        val range = getDataRange(getTypeId())
        return intArrayOf(
            dataProgressToStandardProgress(
                range.max,
                range.min,
                renderType.getValues()[0]
            ),
            dataProgressToStandardProgress(
                range.max,
                range.min,
                renderType.getValues()[1]
            )
        )
    }

    override fun getRealProgress(): IntArray =
        intArrayOf(
            (renderType.getValues()[0] * 100).toInt(),
            (renderType.getValues()[1] * 100).toInt()
        )

    override fun getDefaultValues(): FloatArray = renderType.getDefaultValues()
    override fun setValuesByProgressForBeauty(progress: Int) {
        val range = getDataRange(renderType.id)
        val makeupValue = standardProgressToDataProgress(range.max, range.min, progress)
        renderType.setValuesForBeauty(makeupValue)
    }

    override fun setValuesByProgressForStyleLookup(progress: Int) {
        val range = getDataRange(renderType.id)
        val lookupValue = standardProgressToDataProgress(range.max, range.min, progress)
        renderType.setValuesForStyleLookup(lookupValue)
    }

    override fun setValues(values: FloatArray) {
        renderType.setValuesForBeauty(values[0])
        renderType.setValuesForStyleLookup(values[1])
    }
}

class MakeupData : Data() {
    var type: JsonObject? = null
    private val seekBarCount: Int get() = if (isInLevel2() && !isBackBtn() && !isClear()) 1 else 0
    private val showValue: Boolean get() = !isInLevel2()
    private val shape: String get() = "circle"
    private val bgType: String get() = if (isClear() || isBackBtn() || !isInLevel2()) "icon" else "photo"
    var nowSelect = 1
    var defaultSelect = 1
    var level2List: List<MakeupData> = listOf()
    override fun seekBarCount(): Int = seekBarCount
    override fun showValue(): Boolean = showValue
    override fun bgType(): String = bgType
    override fun shape(): String = shape
    override fun getStandardProgress(): IntArray {
        val range = getDataRange(getTypeId())
        return intArrayOf(
            dataProgressToStandardProgress(
                range.max,
                range.min,
                renderType.getValues()[0]
            )
        )
    }

    override fun getRealProgress(): IntArray = intArrayOf((renderType.getValues()[0] * 100).toInt())
    override fun getDefaultValues(): FloatArray = renderType.getDefaultValues()
    override fun setValuesByProgressForBeauty(progress: Int) {
        val range = getDataRange(renderType.id)
        val value = standardProgressToDataProgress(range.max, range.min, progress)
        renderType.setValuesForBeauty(value)
    }

    override fun setValues(values: FloatArray) {
        renderType.setValuesForBeauty(values[0])
    }
}

class LookupData : Data() {
    var type: JsonObject? = null
    private val seekBarCount: Int get() = if (isClear()) 0 else 1
    var showValue: Boolean = false
    private val shape: String get() = "rect"
    private val bgType: String get() = if (isClear()) "icon" else "photo"
    override fun seekBarCount(): Int = seekBarCount
    override fun showValue(): Boolean = showValue
    override fun bgType(): String = bgType
    override fun shape(): String = shape
    override fun getStandardProgress(): IntArray {
        val range = getDataRange(getTypeId())
        return intArrayOf(
            dataProgressToStandardProgress(
                range.max,
                range.min,
                renderType.getValues()[0]
            )
        )
    }

    override fun getRealProgress(): IntArray = intArrayOf((renderType.getValues()[0] * 100).toInt())
    override fun getDefaultValues(): FloatArray = renderType.getDefaultValues()
    override fun setValuesByProgressForBeauty(progress: Int) {
        val range = getDataRange(renderType.id)
        val value = standardProgressToDataProgress(range.max, range.min, progress)
        renderType.setValuesForBeauty(value)
    }

    override fun setValues(values: FloatArray) {
        renderType.setValuesForBeauty(values[0])
    }
}

class StickerData : Data() {
    var type: JsonObject? = null
    var seekBarCount: Int = 0
    var showValue: Boolean = false
    private val shape: String get() = "rect"
    private val bgType: String get() = if (isClear()) "icon" else "photo"
    override fun seekBarCount(): Int = seekBarCount
    override fun showValue(): Boolean = showValue
    override fun bgType(): String = bgType
    override fun shape(): String = shape
}
