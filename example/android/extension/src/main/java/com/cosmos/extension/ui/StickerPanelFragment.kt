package com.cosmos.extension.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cosmos.config_type.RenderType
import com.cosmos.config_type.*
import com.cosmos.config_type.TabId.Companion.stickerTabId
import com.cosmos.extension.databinding.FragmentStickerPanelBinding
import com.cosmos.extension.rv_adapter.TabAdapter

class StickerPanelFragment : Fragment() {

    @Volatile
    private var currentTabPosition = 0


    internal lateinit var tabDataList:List<TabData>
    private val level1Manager by lazy { Level1Manager(binding.dataRecyclerView, tabDataList) }

    private val resetManager by lazy { ResetManager() }


    private var _binding: FragmentStickerPanelBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStickerPanelBinding.inflate(inflater, container, false)
        initView(binding.root)
        return binding.root
    }

    private fun initView(view: View) {
        initReset()
        initOnClickListener()
        initDataRecyclerViewLayoutManager(view.context)
        initLevel1()
        initTabRecyclerView(view.context)
    }

    private fun initTabRecyclerView(context: Context) {
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.tabRecyclerView.layoutManager = layoutManager
        val adapter = TabAdapter(tabDataList, currentTabPosition)
        adapter.onItemChooseListener = { tabPosition ->
            currentTabPosition = tabPosition
            level1Manager.showLevel1Menu(tabPosition)
        }
        binding.tabRecyclerView.adapter = adapter
    }

    internal var renderCompareOnTouchDownListener: (() -> Unit)? = null
    internal var renderCompareOnTouchUpListener: (() -> Unit)? = null

    @SuppressLint("ClickableViewAccessibility")
    private fun initOnClickListener() {
        binding.reset.setOnClickListener {
            if (tabDataList[currentTabPosition].id == stickerTabId) {
                resetManager.resetSticker(level1Manager.tabDataMap[stickerTabId] as StickerTabData)
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
    }


    var renderInLevel1: ((RenderType) -> Unit)? = null
    var clearInLevel1: ((RenderType) -> Unit)? = null
    private fun initLevel1() {
        level1Manager.onRenderInLevel1 = { renderInLevel1?.invoke(it) }
        level1Manager.onClearInLevel1 = { clearInLevel1?.invoke(it) }
    }

    private fun initDataRecyclerViewLayoutManager(context: Context) {
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.dataRecyclerView.layoutManager = layoutManager
    }

    /**
     * 重置
     */
    var resetSticker: (() -> Unit)? = null
    private fun initReset() {
        resetManager.resetSticker = { resetSticker?.invoke() }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView() {
        level1Manager.getCurrentAdapter()?.notifyDataSetChanged()
    }

    companion object {
        @JvmStatic
        fun newInstance() = StickerPanelFragment()
    }
}