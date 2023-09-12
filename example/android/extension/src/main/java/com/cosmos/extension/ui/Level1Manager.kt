package com.cosmos.extension.ui

import android.util.ArrayMap
import androidx.recyclerview.widget.RecyclerView
import com.cosmos.config_type.Data
import com.cosmos.config_type.RenderType
import com.cosmos.config_type.TabData
import com.cosmos.extension.iLog
import com.cosmos.extension.rv_adapter.Level1Adapter

class Level1Manager(
    private val level1RecyclerView: RecyclerView,
    private val tabDataList: List<TabData>
) {
    /**
     * Tab
     */
    val tabDataMap by lazy {
        ArrayMap<Int, TabData>().apply {
            for (tabData in tabDataList) {
                put(tabData.id, tabData)
            }
        }
    }

    @Volatile
    var currentLevel1Position = 0

    /**
     * Adapter
     */
    @Volatile
    var currentLevel1AdapterId = 0
    private val level1AdapterMap = ArrayMap<Int, Level1Adapter>()

    internal fun getCurrentAdapter(): Level1Adapter? = level1AdapterMap[currentLevel1AdapterId]

    /**
     * SeekBar
     */
    var seekBarToShow: ((count: Int) -> Unit)? = null
    var seekBarProgressToChange: ((progressArray: IntArray) -> Unit)? = null
    var seekBarMaxAndMinToChange: ((Data) -> Unit)? = null

    /**
     * Level2
     */
    var toShowLevel2: ((position: Int) -> Unit)? = null


    /**
     * 显示 Level1
     */
    fun showLevel1Menu(tabPosition: Int) {
        seekBarToShow?.invoke(0)
        loadLevel1RecyclerViewAdapter(tabPosition)
    }

    private fun loadLevel1RecyclerViewAdapter(tabPosition: Int) {
        val tabData = tabDataList[tabPosition]
        val level1Adapter = initLevel1Adapter(tabData.id)
        level1RecyclerView.adapter = level1Adapter
        currentLevel1AdapterId = tabData.id
    }

    var onPrepareInLevel1: ((type: RenderType) -> Unit)? = null
    var onRenderInLevel1: ((type: RenderType) -> Unit)? = null
    var onClearInLevel1: ((type: RenderType) -> Unit)? = null

    private fun initLevel1Adapter(tabId: Int): Level1Adapter? {
        var level1Adapter = level1AdapterMap[tabId]
        val tabData = tabDataMap[tabId]
        if (level1Adapter == null && tabData != null) {
            level1Adapter = Level1Adapter(tabData)

            level1Adapter.itemOnChoose = { level1Position ->
                iLog("L1 : itemOnChoose $level1Position ${tabData.level1List()[level1Position].name}")
                currentLevel1Position = level1Position
                val level1Data = tabData.level1List()[level1Position]
                seekBarMaxAndMinToChange?.invoke(level1Data)
            }
            level1Adapter.itemShouldShowInnerDataSelectUI = { level1Position ->
                iLog("L1 : itemShouldShowSeekBar")
                val level1Data = tabData.level1List()[level1Position]
                val seekBarCount = level1Data.seekBarCount()
                seekBarToShow?.invoke(seekBarCount)
                if (seekBarCount > 0) {
                    seekBarProgressToChange?.invoke(level1Data.getStandardProgress())
                }
            }
            level1Adapter.itemOnShowLevel2 = { level1Position ->
                iLog("L1 : itemOnShowLevel2")
                toShowLevel2?.invoke(level1Position)
            }
            level1Adapter.itemOnPrepare = { level1Position ->
                iLog("L1 : itemOnPrepare")
                val level1Data = tabData.level1List()[level1Position]
                val renderType = level1Data.renderType
                if (!renderType.isClear()) {
                    onPrepareInLevel1?.invoke(tabData.level1List()[level1Position].renderType)
                }
            }
            level1Adapter.itemOnRender = { level1Position ->
                iLog("L1 : itemOnRender")
                val level1Data = tabData.level1List()[level1Position]
                val renderType = level1Data.renderType
                when {
                    renderType.isClear() -> {
                        linkedEffectValueByClear?.invoke(renderType)
                        onClearInLevel1?.invoke(renderType)
                    }
                    else -> {
                        linkedEffectValueByRender?.invoke(renderType)
                        onRenderInLevel1?.invoke(renderType)
                    }
                }
            }
            level1AdapterMap[tabData.id] = level1Adapter
        }
        return level1Adapter
    }

    /**
     * 由于渲染或者清除 导致的渲染 联动反应
     */
    var linkedEffectValueByClear: ((renderType: RenderType) -> Unit)? = null
    var linkedEffectValueByRender: ((renderType: RenderType) -> Unit)? = null
}
