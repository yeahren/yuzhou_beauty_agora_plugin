package com.sc.jojo.json_load

import android.content.Context
import com.cosmos.config_type.*
import com.google.gson.Gson
import java.io.*

class JsonParser {

    lateinit var context: Context
    private val configDir: String by lazy { "${context.applicationContext.filesDir.absolutePath}/cosmos/config" }
    private val gson by lazy {
        Gson()
    }

    private val configs: Configs by lazy { parseItemJson("$configDir/configs.json") }
    private val beautyConfigArray: Array<String> by lazy {
        configs.beautyConfigs
    }
    private val stickerConfigArray: Array<String> by lazy {
        configs.stickerConfigs
    }

    fun initBeautyTabDataList(): List<TabData> {
        val tabList = ArrayList<TabData>(beautyConfigArray.size)
        for (i in beautyConfigArray.indices) {
            when (val configFileName = beautyConfigArray[i]) {
                "beauty.json", "micro.json" -> {
                    tabList.add(initBeauty("$configDir/$configFileName"))
                }
                "oneKeyBeauty.json" -> {
                    tabList.add(initOneKeyBeauty("$configDir/$configFileName"))
                }
                "makeup.json" -> {
                    tabList.add(initMakeup("$configDir/$configFileName"))
                }
                "makeupStyle.json" -> {
                    tabList.add(initMakeupStyle("$configDir/$configFileName"))
                }
                "lookup.json" -> {
                    tabList.add(initLookup("$configDir/$configFileName"))
                }
                "body.json" -> {
                    tabList.add(initBeautyBody("$configDir/$configFileName"))
                }
            }
        }
        return tabList
    }

    fun initStickerTabDataList(): List<TabData> {
        val tabList = ArrayList<TabData>(stickerConfigArray.size)
        for (i in stickerConfigArray.indices) {
            when (val configFileName = stickerConfigArray[i]) {
                "sticker.json" -> {
                    tabList.add(initSticker("$configDir/$configFileName"))
                }
            }
        }
        return tabList
    }

    private fun initBeauty(beautyJsonFilePath: String): BeautyTabData {
        val beautyTabData = parseItemJson<BeautyTabData>(beautyJsonFilePath)
        beautyTabData.defaultSelectPosition = 0
        beautyTabData.nowSelectPosition = 0
        for (index in beautyTabData.list.indices) {
            val data = beautyTabData.list[index]
            val innerTypeJson = data.type!!["innerType"]
            val innerType = innerTypeJson?.asInt ?: 0
            val defaultValue = data.type!!["defaultValue"].asFloat
            data.renderType = BeautyType(
                data.type!!["id"].asInt,
                defaultValue,
                defaultValue,
                innerType
            )
            data.type = null
        }
        return beautyTabData
    }

    private fun initBeautyBody(beautyJsonFilePath: String): BeautyTabData {
        val beautyTabData = parseItemJson<BeautyTabData>(beautyJsonFilePath)
        beautyTabData.defaultSelectPosition = 0
        beautyTabData.nowSelectPosition = 0
        for (index in beautyTabData.list.indices) {
            val data = beautyTabData.list[index]
            val innerTypeJson = data.type!!["id"]
            val innerType = innerTypeJson?.asInt ?: 0
            val defaultValue = data.type!!["defaultValue"].asFloat
            data.renderType = BeautyBodyType(
                innerType,
                0f,
                defaultValue
            )
            data.type = null
        }
        return beautyTabData
    }

    private fun initOneKeyBeauty(oneKeyBeautyJsonFilePath: String): OneKeyBeautyTabData {
        val oneKeyBeautyTabData = parseItemJson<OneKeyBeautyTabData>(oneKeyBeautyJsonFilePath)
        oneKeyBeautyTabData.defaultSelectPosition = 1
        oneKeyBeautyTabData.nowSelectPosition = 1
        for (index in oneKeyBeautyTabData.list.indices) {
            val data = oneKeyBeautyTabData.list[index]
            data.renderType = OneKeyBeautyType(
                data.type!!["id"].asInt
            )
            data.type = null
        }
        return oneKeyBeautyTabData
    }

    private fun initMakeupStyle(makeupStyleJsonFilePath: String): MakeupStyleTabData {
        val makeupStyleTabData = parseItemJson<MakeupStyleTabData>(makeupStyleJsonFilePath)
        makeupStyleTabData.defaultSelectPosition = 0
        makeupStyleTabData.nowSelectPosition = 0
        for (index in makeupStyleTabData.list.indices) {
            val data = makeupStyleTabData.list[index]

            //整装
            val makeupJson = data.type!!["styleMakeupType"]
            val makeupPath: String
            val makeupDefaultValue: Float
            if (makeupJson == null) {
                makeupPath = ""
                makeupDefaultValue = 0f
            } else {
                makeupPath = makeupJson.asJsonObject["path"].asString
                makeupDefaultValue = makeupJson.asJsonObject["defaultValue"].asFloat
            }
            val styleMakeupType =
                StyleMakeupType(makeupPath, makeupDefaultValue, makeupDefaultValue)

            //滤镜
            val lookupJson = data.type!!["styleLookupType"]
            val lookupDefaultValue: Float =
                if (lookupJson == null) 0f else lookupJson.asJsonObject["defaultValue"].asFloat
            val styleLookupType = StyleLookupType(lookupDefaultValue, lookupDefaultValue)

            //over
            data.renderType =
                MakeupStyleType(data.type!!["id"].asInt, styleMakeupType, styleLookupType)
            data.type = null
        }
        return makeupStyleTabData
    }

    private fun initMakeup(makeupJsonFilePath: String): MakeupTabData {
        val makeupTabData = parseItemJson<MakeupTabData>(makeupJsonFilePath)
        makeupTabData.defaultSelectPosition = 0
        makeupTabData.nowSelectPosition = 0
        for (index in makeupTabData.list.indices) {
            val data = makeupTabData.list[index]
            data.renderType = MakeupType(data.type!!["id"].asInt, 0f, 0f)
            data.type = null
            for (innerIndex in data.level2List.indices) {
                val innerData = data.level2List[innerIndex]
                val pathJsonObject = innerData.type!!["path"]
                val path = if (pathJsonObject == null) "" else pathJsonObject.asString
                val defaultValueJsonObject = innerData.type!!["defaultValue"]
                val defaultValue = defaultValueJsonObject?.asFloat ?: 0f

                innerData.renderType =
                    MakeupInnerType(innerData.type!!["id"].asInt, path, defaultValue, defaultValue)
                innerData.type = null
            }
        }
        return makeupTabData
    }

    private fun initLookup(lookupJsonFilePath: String): LookupTabData {
        val lookupTabData = parseItemJson<LookupTabData>(lookupJsonFilePath)
        lookupTabData.defaultSelectPosition = 1
        lookupTabData.nowSelectPosition = 1
        for (index in lookupTabData.list.indices) {
            val data = lookupTabData.list[index]
            val lookupPathJson = data.type!!["path"]
            val lookupPath = if (lookupPathJson == null) "" else lookupPathJson.asString
            val lookupDefaultValueJson = data.type!!["defaultValue"]
            val lookupDefaultValue = lookupDefaultValueJson?.asFloat ?: 0f

            data.renderType = LookupType(
                data.type!!["id"].asInt,
                lookupPath,
                lookupDefaultValue,
                lookupDefaultValue
            )
            data.type = null
        }
        return lookupTabData
    }

    private fun initSticker(stickerJsonFilePath: String): StickerTabData {
        val stickerTabData = parseItemJson<StickerTabData>(stickerJsonFilePath)
        stickerTabData.defaultSelectPosition = 0
        stickerTabData.nowSelectPosition = 0
        for (index in stickerTabData.list.indices) {
            val data = stickerTabData.list[index]
            val stickerPathJson = data.type!!["path"]
            val stickerPath = if (stickerPathJson == null) "" else stickerPathJson.asString
            data.renderType = StickerType(data.type!!["id"].asInt, stickerPath)
        }
        return stickerTabData
    }

    private inline fun <reified T> parseItemJson(path: String): T {
        return gson.fromJson(readTextFileFromFile(path), T::class.java)
    }

    private fun readTextFileFromFile(filePath: String): String {
        val body = StringBuilder()
        val fileInputStream = FileInputStream(File(filePath))
        fileInputStream.use { fIS ->
            val inputStreamReader = InputStreamReader(fIS)
            inputStreamReader.use { iSR ->
                val bufferedReader = BufferedReader(iSR)
                bufferedReader.use { bR ->
                    var nextLine: String?
                    try {
                        while (bR.readLine().also { nextLine = it } != null) {
                            body.append(nextLine)
                            body.append('\n')
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        return body.toString()
    }
}