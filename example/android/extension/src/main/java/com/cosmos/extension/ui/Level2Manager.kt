package com.cosmos.extension.ui

import android.util.ArrayMap
import androidx.recyclerview.widget.RecyclerView
import com.cosmos.config_type.Data
import com.cosmos.config_type.MakeupData
import com.cosmos.config_type.RenderType
import com.cosmos.extension.iLog
import com.cosmos.extension.rv_adapter.Level2Adapter

/**
 * 美妆的 Level2
 */
class Level2Manager(
    private val level2RecyclerView: RecyclerView,
    private val dataList: List<Data> //美妆的所有 beautyData
) {
    @Volatile
    var currentLevel2Position = 0

    /**
     * Adapter
     */
    @Volatile
    var currentLevel2AdapterId = 0
    private val level2AdapterMap = ArrayMap<Int, Level2Adapter>()

    /**
     * SeekBar
     */
    var seekBarToShow: ((count: Int) -> Unit)? = null
    var seekBarProgressToChange: ((progressArray: IntArray) -> Unit)? = null
    var seekBarMaxAndMinToChange: ((MakeupData) -> Unit)? = null

    /**
     * 口红质地
     */
    var lipTextureUIToShow: ((level1Data: Data) -> Unit)? = null

    /**
     * 返回Level1
     */
    var onBackToLevel1: (() -> Unit)? = null

    /**
     * 显示 二级菜单(RecyclerView)
     */
    fun showLevel2Menu(position: Int) {
        seekBarToShow?.invoke(0)
        //通过position在secondaryMenuList里面找到要显示的list，来加载recyclerView
        loadLevel2RecyclerViewAdapter(position)
    }

    private fun loadLevel2RecyclerViewAdapter(position: Int) {
        val level2Adapter = initLevel2Adapter(position)
        level2RecyclerView.adapter = level2Adapter
    }


    var onPrepareInLevel2: ((renderType: RenderType) -> Unit)? = null
    var onRenderInLevel2: ((renderType: RenderType) -> Unit)? = null
    var onClearInLevel2: ((renderType: RenderType) -> Unit)? = null

    private fun initLevel2Adapter(position: Int): Level2Adapter {
        val data = dataList[position]
        var level2Adapter = level2AdapterMap[data.getTypeId()]
        currentLevel2AdapterId = data.getTypeId()
        if (level2Adapter == null) {
            level2Adapter = Level2Adapter(data as MakeupData)
            val subBeautyDataList = data.level2List
            level2Adapter.itemOnChoose = { level2Position ->
                iLog("L2 : itemOnChoose")
                currentLevel2Position = level2Position
                val level2Data = subBeautyDataList[level2Position]
                seekBarMaxAndMinToChange?.invoke(level2Data)
                changeLevel1RatesWhenChoose(level2Data.getStandardProgress()[0])
            }
            level2Adapter.itemShouldShowSeekBar = { level2Position ->
                iLog("L2 : itemShouldShowSeekBar")
                val level2Data = subBeautyDataList[level2Position]
                val seekbarCount = level2Data.seekBarCount()
                seekBarToShow?.invoke(level2Data.seekBarCount())
                if (seekbarCount > 0) {
                    seekBarProgressToChange?.invoke(level2Data.getStandardProgress())
                }
                lipTextureUIToShow?.invoke(level2Data)
            }
            level2Adapter.itemOnBackToLevel1 = {
                iLog("L2 : itemOnBackToLevel1")
                onBackToLevel1?.invoke()
            }
            level2Adapter.itemOnPrepare = { level2Position ->
                iLog("L2 : itemOnPrepare")
                val level2Data = subBeautyDataList[level2Position]
                if (!level2Data.isClear()) {
                    onPrepareInLevel2?.invoke(subBeautyDataList[level2Position].renderType)
                }
            }
            level2Adapter.itemOnRender = { level2Position ->
                iLog("L2 : itemOnRender")
                val level2Data = subBeautyDataList[level2Position]
                when {
                    level2Data.isClear() -> {
                        onClearInLevel2?.invoke(level2Data.renderType)
                    }
                    else -> {
                        linkedEffectValueByInLevel2?.invoke()
                        onRenderInLevel2?.invoke(level2Data.renderType)
                    }
                }
            }
            level2AdapterMap[data.getTypeId()] = level2Adapter
        }
        return level2Adapter
    }


    /**
     * 在Level2改Level1 的 RecyclerView的数据
     */
    var toChangeLevel1Rate: ((progress: Int) -> Unit)? = null
    private fun changeLevel1RatesWhenChoose(progress: Int) {
        toChangeLevel1Rate?.invoke(progress)
    }

    /**
     * Level2
     */
    var linkedEffectValueByInLevel2: (() -> Unit)? = null
}