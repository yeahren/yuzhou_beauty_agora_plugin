package com.cosmos.extension.ui

import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import com.cosmos.config_type.RenderType
import com.cosmos.config_type.standardProgressToDataProgress

class SeekbarManager(
    private val beautySeekBar: SeekBar,
    private val beautySeekBarProgressText: TextView,
    private val beautySeekBarTitle: TextView,
    private val filterSeekBar: SeekBar,
    private val filterSeekBarProgressText: TextView,
    private val filterSeekBarTitle: TextView
) {

    private var max = 100
    private var min = 0

    private lateinit var renderType: RenderType

    var beautySeekBarOnDragToRender: ((renderType: RenderType) -> Unit)? = null
    var filterSeekBarOnDragToRender: ((renderType: RenderType) -> Unit)? = null

    var changeValueInListByDragBeautySeekbar: ((Int) -> Unit)? = null
    var changeValueInListByDragFilterSeekbar: ((Int) -> Unit)? = null

    var updateRecyclerRateByDragBeautySeekbar: (() -> Unit)? = null

    fun initSeekBar() {
        beautySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                //修改上方的 Progress TextView
                beautySeekBarProgressText.updateProgressText(progress)

                if (fromUser) {
                    linkedEffectValueByDrag?.invoke(renderType)
                    //修改List里面的数值，做到SeekBar和List里面同步
                    changeValueInListByDragBeautySeekbar?.invoke(progress)
                    //修改RecyclerView下方的数字
                    updateRecyclerRateByDragBeautySeekbar?.invoke()
                    //对外的回调
                    beautySeekBarOnDragToRender?.invoke(renderType)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        filterSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                //修改上方的 Progress TextView
                filterSeekBarProgressText.updateProgressText(progress)

                if (fromUser) {
                    //修改List里面的数值，做到SeekBar和List里面同步
                    changeValueInListByDragFilterSeekbar?.invoke(progress)
                    //SeekBar的对外回调
                    filterSeekBarOnDragToRender?.invoke(renderType)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    private fun TextView.updateProgressText(progress: Int) {
        val pText = standardProgressToDataProgress(max.toFloat(), min.toFloat(), progress)
        this.text = pText.toInt().toString()
    }

    fun changeRenderType(renderType: RenderType) {
        this.renderType = renderType
    }

    fun changeMaxAndMin(max: Int, min: Int) {
        this.max = max
        this.min = min
    }

    fun changeProgress(progressArray: IntArray) {
        when (progressArray.size) {
            1 -> {
                beautySeekBar.progress = progressArray[0]
            }
            2 -> {
                beautySeekBar.progress = progressArray[0]
                filterSeekBar.progress = progressArray[1]
            }
        }
    }

    fun showSeekBar(count: Int) {
        when (count) {
            0 -> {
                beautySeekBar.visibility = View.GONE
                beautySeekBarProgressText.visibility = View.GONE
                beautySeekBarTitle.visibility = View.GONE

                filterSeekBar.visibility = View.GONE
                filterSeekBarProgressText.visibility = View.GONE
                filterSeekBarTitle.visibility = View.GONE
            }
            1 -> {
                beautySeekBar.visibility = View.VISIBLE
                beautySeekBarProgressText.visibility = View.VISIBLE
                beautySeekBarTitle.visibility = View.GONE

                filterSeekBar.visibility = View.GONE
                filterSeekBarProgressText.visibility = View.GONE
                filterSeekBarTitle.visibility = View.GONE
            }
            2 -> {
                beautySeekBar.visibility = View.VISIBLE
                beautySeekBarProgressText.visibility = View.VISIBLE
                beautySeekBarTitle.visibility = View.VISIBLE

                filterSeekBar.visibility = View.VISIBLE
                filterSeekBarProgressText.visibility = View.VISIBLE
                filterSeekBarTitle.visibility = View.VISIBLE
            }
        }
    }

    /**
     * 由于点击 导致的渲染 联动反应
     */
    var linkedEffectValueByDrag: ((renderType: RenderType) -> Unit)? = null

    internal fun resetSeekbar(progress: IntArray) {
        when (progress.size) {
            1 -> beautySeekBar.progress = progress[0]
            2 -> {
                beautySeekBar.progress = progress[0]
                filterSeekBar.progress = progress[1]
            }
        }
    }
}