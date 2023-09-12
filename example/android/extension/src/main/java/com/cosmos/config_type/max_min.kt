package com.cosmos.config_type

data class Range(val max: Float, val min: Float)

/**
 * 定义了特殊的类型对应的range
 * 3002 对应 削脸 详情见 #com.cosmos.config_loader.bean.type
 */
private val Beauty_Range_Map = mapOf(
    Pair(3002, Range(1f, -1f)),
    Pair(3004, Range(1f, -1f)),
    Pair(3005, Range(1f, -1f)),
    Pair(3008, Range(1f, -1f)),
    Pair(3009, Range(1f, -1f)),
    Pair(3010, Range(1f, -1f)),
    Pair(3011, Range(1f, -1f)),
    Pair(3012, Range(1f, -1f)),
    Pair(3013, Range(1f, -1f)),
    Pair(3014, Range(1f, -1f)),
    Pair(3016, Range(1f, -1f)),
    Pair(3017, Range(1f, -1f)),
    Pair(8000, Range(1f, 0f)),
    Pair(8001, Range(1f, 0f))
)

fun getDataRange(typeId: Int): Range {
    var range = Beauty_Range_Map[typeId]
    if (range == null) {
        range = Range(1f, 0f)
    }
    return range
}