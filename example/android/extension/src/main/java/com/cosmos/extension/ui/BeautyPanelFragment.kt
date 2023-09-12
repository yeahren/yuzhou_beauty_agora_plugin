package com.cosmos.extension.ui

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cosmos.config_type.*
import com.cosmos.config_type.TabId.Companion.beautyTabId
import com.cosmos.config_type.TabId.Companion.bodyBeautyTabId
import com.cosmos.config_type.TabId.Companion.lookupTabId
import com.cosmos.config_type.TabId.Companion.makeupStyleTabId
import com.cosmos.config_type.TabId.Companion.makeupTabId
import com.cosmos.config_type.TabId.Companion.microTabId
import com.cosmos.config_type.TabId.Companion.oneKeyBeautyTabId
import com.cosmos.config_type.type.makeup_lip
import com.cosmos.extension.R
import com.cosmos.extension.dLog
import com.cosmos.extension.databinding.FragmentBeautyPanelBinding
import com.cosmos.extension.rv_adapter.TabAdapter

class BeautyPanelFragment : Fragment() {
    /**
     * 全局变量
     */
    @Volatile
    private var currentTabPosition = 0

    @Volatile
    private var isInLevel2 = false

    /**
     * UI 相关
     */
    internal lateinit var tabDataList:List<TabData>
    private val seekbarManager by lazy {
        SeekbarManager(
            binding.beautySeekbar,
            binding.beautySeekbarProgress,
            binding.beautySeekbarTitle,
            binding.filterSeekbar,
            binding.filterSeekbarProgress,
            binding.filterSeekbarTitle
        )
    }

    private val level1Manager by lazy { Level1Manager(binding.dataRecyclerView, tabDataList) }
    private val level2Manager by lazy {
        Level2Manager(
            binding.dataRecyclerView,
            level1Manager.tabDataMap[makeupTabId]!!.level1List()
        )
    }

    private val effectInitManager by lazy {
        EffectInitManager()
    }

    private val linkEffectManager by lazy {
        LinkEffectManager()
    }

    private val resetManager by lazy {
        ResetManager()
    }

    private var _binding: FragmentBeautyPanelBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBeautyPanelBinding.inflate(inflater, container, false)
        initView(binding.root)
        return binding.root
    }

    private fun initView(view: View) {
        initReset()
        initLinkEffect()
        seekbarManager.initSeekBar()
        initSeekbar()
        initOnClickListener()
        initDataRecyclerViewLayoutManager(view.context)
        initLevel1()
        initLevel2()
        initTabRecyclerView(view.context)
    }

    /**
     * 初始化 LayoutManager
     */
    private fun initDataRecyclerViewLayoutManager(context: Context) {
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.dataRecyclerView.layoutManager = layoutManager
    }

    internal var renderCompareOnTouchDownListener: (() -> Unit)? = null
    internal var renderCompareOnTouchUpListener: (() -> Unit)? = null

    @SuppressLint("ClickableViewAccessibility")
    private fun initOnClickListener() {
        binding.reset.setOnClickListener {
            when (tabDataList[currentTabPosition].id) {
                oneKeyBeautyTabId -> {
                    resetManager.resetOneKeyBeauty(
                        level1Manager.tabDataMap[oneKeyBeautyTabId] as OneKeyBeautyTabData,
                        level1Manager.tabDataMap[beautyTabId] as BeautyTabData,
                        level1Manager.tabDataMap[microTabId] as BeautyTabData
                    )
                }
                beautyTabId -> {
                    val beautyTabData = level1Manager.tabDataMap[beautyTabId] as BeautyTabData
                    val beautyData = beautyTabData.level1List()[beautyTabData.defaultSelectPosition]
                    resetManager.resetBeauty(beautyTabData)
                    seekbarManager.showSeekBar(beautyData.seekBarCount())
                    changeSeekBarMaxAndMin(beautyData)
                    seekbarManager.resetSeekbar(beautyData.getStandardProgress())
                }
                microTabId -> {
                    val microTabData = level1Manager.tabDataMap[microTabId] as BeautyTabData
                    val microData = microTabData.level1List()[microTabData.defaultSelectPosition]
                    resetManager.resetMicro(microTabData)
                    seekbarManager.showSeekBar(microData.seekBarCount())
                    changeSeekBarMaxAndMin(microData)
                    seekbarManager.resetSeekbar(microData.getStandardProgress())
                }
                makeupStyleTabId -> {
                    val makeupStyleTabData =
                        level1Manager.tabDataMap[makeupStyleTabId] as MakeupStyleTabData
                    val makeupStyleData =
                        makeupStyleTabData.level1List()[makeupStyleTabData.defaultSelectPosition]
                    resetManager.resetMakeupStyle(
                        makeupStyleTabData,
                        level1Manager.tabDataMap[makeupTabId] as MakeupTabData
                    )
                    changeSeekBarMaxAndMin(makeupStyleData)
                    seekbarManager.showSeekBar(makeupStyleData.seekBarCount())
                }
                makeupTabId -> {
                    resetManager.resetMakeup(level1Manager.tabDataMap[makeupTabId] as MakeupTabData)
                    level1Manager.showLevel1Menu(currentTabPosition)
                }
                bodyBeautyTabId -> {
                    resetManager.resetBeautyBody(level1Manager.tabDataMap[bodyBeautyTabId] as BeautyTabData)
                    level1Manager.showLevel1Menu(currentTabPosition)
                }
                lookupTabId -> {
                    val lookupTabData = level1Manager.tabDataMap[lookupTabId] as LookupTabData
                    val lookupData = lookupTabData.level1List()[lookupTabData.defaultSelectPosition]
                    resetManager.resetLookup(lookupTabData)
                    seekbarManager.showSeekBar(lookupData.seekBarCount())
                    changeSeekBarMaxAndMin(lookupData)
                    seekbarManager.resetSeekbar(lookupData.getStandardProgress())
                }
            }
            updateRecyclerView()
        }

        binding.renderCompare.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    renderCompareOnTouchDownListener?.invoke()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    renderCompareOnTouchUpListener?.invoke()
                }
            }
            true
        }

        binding.tvTexture1.setOnClickListener { onTextureClick((it as TextView).text.toString()) }
        binding.tvTexture2.setOnClickListener { onTextureClick((it as TextView).text.toString()) }
        binding.tvTexture3.setOnClickListener { onTextureClick((it as TextView).text.toString()) }
        binding.llCurrent.setOnClickListener { onCurrentClick(binding.tvTextureCurrent.text.toString()) }
    }

    private fun onTextureClick(currentTexture: String) {
        onCurrentClick(currentTexture)
        currentTextureTypeId = lipTextureMap[currentTexture]!!
        binding.tvTextureCurrent.text = currentTexture
    }

    @Volatile
    var currentTextureTypeId: Int = 1
    private val textureContent = arrayOf("默认", "水润", "雾面", "镜面", "亮闪")
    private val lipTextureMap = mapOf(
        "水润" to 1,
        "雾面" to 2,
        "镜面" to 3,
        "亮闪" to 4
    )
    var showTextureMore = false

    var lipTextTureToChange: ((textureTypeId: Int) -> Unit)? = null
    private fun onCurrentClick(currentTexture: String) {
        showTextureMore = !showTextureMore
        if (showTextureMore) {
            binding.tvTexture1.visibility = View.VISIBLE
            binding.tvTexture1.text = textureContent[1]
            binding.tvTexture2.visibility = View.VISIBLE
            binding.tvTexture2.text = textureContent[2]
            binding.tvTexture3.visibility = View.VISIBLE
            binding.tvTexture3.text = textureContent[3]
            binding.tvTextureCurrent.text = textureContent[4]

            binding.tvTexture1.isSelected = currentTextureTypeId == 1
            binding.tvTexture2.isSelected = currentTextureTypeId == 2
            binding.tvTexture3.isSelected = currentTextureTypeId == 3
            binding.llCurrent.isSelected = currentTextureTypeId == 4
            binding.llCurrent.background =
                context?.let { ContextCompat.getDrawable(it, R.drawable.texture_select_bottom) }
        } else {
            binding.tvTexture1.visibility = View.GONE
            binding.tvTexture2.visibility = View.GONE
            binding.tvTexture3.visibility = View.GONE
            binding.llCurrent.background =
                context?.let { ContextCompat.getDrawable(it, R.drawable.texture_select_bg) }
        }
        currentTextureTypeId = lipTextureMap[currentTexture]!!
        textureMoreAnim()
        lipTextTureToChange?.invoke(currentTextureTypeId)
    }

    var moreAnimator: ValueAnimator = ValueAnimator()
    private fun textureMoreAnim() {
        moreAnimator.apply {
            setTarget(binding.ivMore)
            repeatCount = 0
            duration = 200
            addUpdateListener { valueAnimator ->
                binding.ivMore.rotation = valueAnimator.animatedValue as Float
            }
        }
        if (showTextureMore) {
            moreAnimator.setFloatValues(0f, 90f)
        } else {
            moreAnimator.setFloatValues(90f, 0f)
        }
        moreAnimator.start()
    }

    private fun initTabRecyclerView(context: Context) {
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.tabRecyclerView.layoutManager = layoutManager
        val adapter = TabAdapter(tabDataList, currentTabPosition)
        adapter.onItemChooseListener = { tabPosition ->
            currentTabPosition = tabPosition
            level1Manager.showLevel1Menu(tabPosition)
            isInLevel2 = false
            hideLipTextureUI()
        }
        binding.tabRecyclerView.adapter = adapter
        effectInit()
    }

    /**
     * 在初始化 reset 以及 选择一键美颜后，联动美颜和微整形
     */
    var changeBeautyAndMicroByRenderOneKeyBeauty: ((OneKeyBeautyType) -> Map<Int, Float>)? = null
    var initEffect: ((OneKeyBeautyType, LookupType) -> Unit)? = null
    private fun effectInit() {
        val oneKeyBeautyDataList = level1Manager.tabDataMap[oneKeyBeautyTabId]!!.level1List()
        val lookupDataList = level1Manager.tabDataMap[lookupTabId]!!.level1List()
        effectInitManager.initEffect = { oneKeyPosition, lookupPosition ->
            val oneKeyBeautyType = oneKeyBeautyDataList[oneKeyPosition].renderType
            val lookupType = lookupDataList[lookupPosition].renderType
            initEffect?.invoke(oneKeyBeautyType as OneKeyBeautyType, lookupType as LookupType)
        }
        effectInitManager.linkEffect = {
            linkEffectManager.changeBeautyAndMicroByRenderOneKeyBeauty(
                it,
                level1Manager.tabDataMap[beautyTabId] as BeautyTabData,
                level1Manager.tabDataMap[microTabId] as BeautyTabData
            )
        }
        effectInitManager.startInitEffect()
    }

    /**
     * level1
     */
    var prepareInLevel1: ((RenderType) -> Unit)? = null
    var renderInLevel1: ((RenderType) -> Unit)? = null
    var clearInLevel1: ((RenderType) -> Unit)? = null
    var removeLookupByMakeupStyle: (() -> Unit)? = null
    var clearMakeupByMakeupStyle: (() -> Unit)? = null
    private fun initLevel1() {
        level1Manager.seekBarMaxAndMinToChange = {
            changeSeekBarMaxAndMin(it)
        }
        level1Manager.seekBarToShow = { seekbarManager.showSeekBar(it) }
        level1Manager.seekBarProgressToChange = { seekbarManager.changeProgress(it) }
        level1Manager.toShowLevel2 = {
            level2Manager.showLevel2Menu(it)
            isInLevel2 = true
        }
        level1Manager.onPrepareInLevel1 = {
            prepareInLevel1?.invoke(it)
            seekbarManager.changeRenderType(it)
        }
        level1Manager.onRenderInLevel1 = { renderInLevel1?.invoke(it) }
        level1Manager.onClearInLevel1 = { clearInLevel1?.invoke(it) }

        level1Manager.linkedEffectValueByClear = {
            when (it) {
                is OneKeyBeautyType -> {
                    val beautyTabData = level1Manager.tabDataMap[beautyTabId]
                    val microTabData = level1Manager.tabDataMap[microTabId]
                    linkEffectManager.clearBeautyAndMicroByRenderOneKeyBeauty(
                        beautyTabData as BeautyTabData,
                        microTabData as BeautyTabData
                    )
                }
                is MakeupStyleType -> {
                    val makeupTabData = level1Manager.tabDataMap[makeupTabId]
                    linkEffectManager.clearMakeupByMakeupStyle(makeupTabData as MakeupTabData)
                }
            }
        }

        level1Manager.linkedEffectValueByRender = {
            when (it) {
                is OneKeyBeautyType -> {
                    val beautyTabData = level1Manager.tabDataMap[beautyTabId]
                    val microTabData = level1Manager.tabDataMap[microTabId]
                    linkEffectManager.changeBeautyAndMicroByRenderOneKeyBeauty(
                        it,
                        beautyTabData as BeautyTabData,
                        microTabData as BeautyTabData
                    )
                }
                is MakeupStyleType -> {
                    val lookupTabData = level1Manager.tabDataMap[lookupTabId]
                    val makeupTabData = level1Manager.tabDataMap[makeupTabId]
                    linkEffectManager.removeLookupByMakeupStyle(lookupTabData as LookupTabData)
                    linkEffectManager.clearMakeupByMakeupStyle(makeupTabData as MakeupTabData)
                }
            }
        }
    }


    /**
     * level2
     */
    var prepareInLevel2: ((RenderType) -> Unit)? = null
    var renderInLevel2: ((RenderType) -> Unit)? = null
    var clearInLevel2: ((RenderType) -> Unit)? = null
    var removeMakeupStyleByMakeup: (() -> Unit)? = null
    private fun initLevel2() {
        level2Manager.seekBarMaxAndMinToChange = {
            changeSeekBarMaxAndMin(it)
        }
        level2Manager.lipTextureUIToShow = {
            if (it.getLevel1Id() == makeup_lip && !it.isClear() && !it.isBackBtn()) {
                showLipTextureUI()
            } else {
                hideLipTextureUI()
            }
        }
        level2Manager.seekBarToShow = { seekbarManager.showSeekBar(it) }
        level2Manager.seekBarProgressToChange = { seekbarManager.changeProgress(it) }
        level2Manager.onBackToLevel1 = {
            level1Manager.showLevel1Menu(currentTabPosition)
            isInLevel2 = false
        }
        level2Manager.onPrepareInLevel2 = {
            prepareInLevel2?.invoke(it)
            seekbarManager.changeRenderType(it)
        }
        level2Manager.onRenderInLevel2 = { renderInLevel2?.invoke(it) }
        level2Manager.onClearInLevel2 = { clearInLevel2?.invoke(it) }
        level2Manager.toChangeLevel1Rate = { changeBeautyValueInList(it) }

        level2Manager.linkedEffectValueByInLevel2 = {
            linkEffectManager.removeMakeupStyleByMakeup()
        }
    }

    private fun changeSeekBarMaxAndMin(data: Data) {
        val range = getDataRange(data.getTypeId())
        val max = (range.max * 100).toInt()
        val min = (range.min * 100).toInt()
        seekbarManager.changeMaxAndMin(max, min)
    }

    private fun showLipTextureUI() {
        binding.llTextureLayout.visibility = View.VISIBLE
    }

    private fun hideLipTextureUI() {
        showTextureMore = false
        binding.llTextureLayout.visibility = View.GONE
        binding.tvTexture1.visibility = View.GONE
        binding.tvTexture2.visibility = View.GONE
        binding.tvTexture3.visibility = View.GONE
        binding.llCurrent.background =
            context?.let { ContextCompat.getDrawable(it, R.drawable.texture_select_bg) }
    }

    /**
     * seekbar
     */
    var beautySeekBarRender: ((RenderType) -> Unit)? = null
    var filterSeekBarRender: ((RenderType) -> Unit)? = null
    private fun initSeekbar() {
        seekbarManager.beautySeekBarOnDragToRender = {
            beautySeekBarRender?.invoke(it)
        }
        seekbarManager.filterSeekBarOnDragToRender = { filterSeekBarRender?.invoke(it) }
        seekbarManager.changeValueInListByDragBeautySeekbar = { changeBeautyValueInList(it) }
        seekbarManager.changeValueInListByDragFilterSeekbar = { changeFilterValueInList(it) }
        seekbarManager.updateRecyclerRateByDragBeautySeekbar = { updateRecyclerViewRate() }
        seekbarManager.linkedEffectValueByDrag = {
            when (it) {
                is BeautyType -> {
                    linkEffectManager.clearOneKeyBeautyByBeautyOrMicro()
                }
            }
        }
    }

    /**
     * 把 RecyclerView 下方的 数字进行修改
     */
    private fun updateRecyclerViewRate() {
        val level1Adapter = level1Manager.getCurrentAdapter()
        level1Adapter?.notifyItemChange()
    }

    /**
     * 在 Seekbar 拖动的时候
     * 改变 美颜 强度
     */
    private fun changeBeautyValueInList(progress: Int) {
        val level1position = level1Manager.currentLevel1Position
        val level2position = level2Manager.currentLevel2Position
        if (!isInLevel2) {
            val level1Data = tabDataList[currentTabPosition].level1List()[level1position]
            level1Data.setValuesByProgressForBeauty(progress)
        }
        //二级菜单 需要将当前以及上一级菜单value改了
        else {
            val level1Data = tabDataList[currentTabPosition].level1List()[level1position]
            (level1Data as MakeupData).setValuesByProgressForBeauty(progress)
            val level2Data = level1Data.level2List[level2position]
            level2Data.setValuesByProgressForBeauty(progress)
        }
    }

    /**
     * 在 Seekbar 拖动的时候
     * 去修改 风格妆的 滤镜强度 在List 里面
     */
    private fun changeFilterValueInList(progress: Int) {
        val level1Data =
            tabDataList[currentTabPosition].level1List()[level1Manager.currentLevel1Position]
        level1Data.setValuesByProgressForStyleLookup(progress)
    }

    private fun initLinkEffect() {
        linkEffectManager.clearOneKeyBeautyByBeautyOrMicro = {
            level1Manager.tabDataMap[oneKeyBeautyTabId]?.nowSelectPosition = -1
        }
        linkEffectManager.removeMakeupStyleByMakeup = {
            if (level1Manager.tabDataMap[makeupStyleTabId]?.nowSelectPosition != 0) {
                removeMakeupStyleByMakeup?.invoke()
            }
            level1Manager.tabDataMap[makeupStyleTabId]?.nowSelectPosition = 0
        }
        linkEffectManager.changeBeautyAndMicroByRenderOneKeyBeauty =
            { changeBeautyAndMicroByRenderOneKeyBeauty?.invoke(it) }
        linkEffectManager.removeLookupByMakeupStyle = { removeLookupByMakeupStyle?.invoke() }
        linkEffectManager.clearMakeupByMakeupStyle = { clearMakeupByMakeupStyle?.invoke() }
        linkEffectManager.changeBeautyAndMicroByRenderOneKeyBeautyInEffectInit = { oneKeyPosition ->
            val oneKeyBeautyType =
                level1Manager.tabDataMap[oneKeyBeautyTabId]!!.level1List()[oneKeyPosition].renderType
            changeBeautyAndMicroByRenderOneKeyBeauty?.invoke(oneKeyBeautyType as OneKeyBeautyType)
        }
    }

    /**
     * 重置
     */
    var resetOneKeyBeauty: ((OneKeyBeautyType) -> Unit)? = null
    var resetBeauty: ((BeautyType) -> Unit)? = null
    var resetMakeupStyle: (() -> Unit)? = null
    var resetMakeupInner: (() -> Unit)? = null
    var resetLookup: ((LookupType) -> Unit)? = null
    private fun initReset() {
        resetManager.resetOneKeyBeauty = { resetOneKeyBeauty?.invoke(it) }
        resetManager.resetBeauty = { resetBeauty?.invoke(it) }
        resetManager.resetMicro = { resetBeauty?.invoke(it) }
        resetManager.resetMakeupStyle = { resetMakeupStyle?.invoke() }
        resetManager.resetMakeup = {
            resetMakeupInner?.invoke()
            hideLipTextureUI()
        }
        resetManager.resetLipTexture = {
            if (it.getLevel1Id() == makeup_lip) {
                binding.tvTextureCurrent.text = textureContent[4]
                currentTextureTypeId = 4
                binding.llCurrent.background =
                    context?.let { ContextCompat.getDrawable(it, R.drawable.texture_select_bg) }
            }
        }
        resetManager.resetLookup = { resetLookup?.invoke(it) }

        resetManager.changeBeautyAndMicroByRenderOneKeyBeauty =
            { oneKeyBeautyType, beautyTabData, microTabData ->
                linkEffectManager.changeBeautyAndMicroByRenderOneKeyBeauty(
                    oneKeyBeautyType,
                    beautyTabData,
                    microTabData
                )
            }
        resetManager.clearOneKeyBeautyByBeautyOrMicro = {
            linkEffectManager.clearOneKeyBeautyByBeautyOrMicro()
        }
        resetManager.clearMakeupByMakeupStyle = {
            linkEffectManager.clearMakeupByMakeupStyle(it)
        }
        resetManager.removeMakeupStyleByMakeup = {
            linkEffectManager.removeMakeupStyleByMakeup()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView() {
        level1Manager.getCurrentAdapter()?.notifyDataSetChanged()
    }

    override fun onDestroy() {
        dLog("BeautyPanelFragment onDestroy")
        super.onDestroy()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = BeautyPanelFragment()
    }
}