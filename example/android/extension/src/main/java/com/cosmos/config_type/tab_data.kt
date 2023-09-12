package com.cosmos.config_type

import com.cosmos.config_type.*

/**
 * 解析config Json 之后的Bean类
 * 其中 path 一律写相对于 cosmos.zip 解压出来之后 在data/files文件夹下的相对目录
 * 例子：data/files/cosmos/icon/a.png => path = "cosmos/icon/a.png
 */
open class TabData {
    val id: Int = 0
    var name: String = ""
    var defaultSelectPosition: Int = -1
    var nowSelectPosition: Int = -1

    open fun level1List(): List<Data> = listOf()
}

class OneKeyBeautyTabData : TabData() {
    var list: List<OneKeyBeautyData> = listOf()
    override fun level1List(): List<Data> {
        return list
    }
}

class BeautyTabData : TabData() {
    var list: List<BeautyData> = listOf()
    override fun level1List(): List<Data> {
        return list
    }
}

class MakeupStyleTabData : TabData() {
    var list: List<MakeupStyleData> = listOf()
    override fun level1List(): List<Data> {
        return list
    }
}

class MakeupTabData : TabData() {
    var list: List<MakeupData> = listOf()
    override fun level1List(): List<Data> {
        return list
    }
}

class LookupTabData : TabData() {
    var list: List<LookupData> = listOf()
    override fun level1List(): List<Data> {
        return list
    }
}

class StickerTabData : TabData() {
    var list: List<StickerData> = listOf()
    override fun level1List(): List<Data> {
        return list
    }
}



