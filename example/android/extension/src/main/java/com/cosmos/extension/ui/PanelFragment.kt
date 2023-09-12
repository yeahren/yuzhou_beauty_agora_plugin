package com.cosmos.extension.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cosmos.config_type.TabData
import com.cosmos.extension.dLog
import com.cosmos.extension.databinding.FragmentPanelBinding
import com.cosmos.extension.rootDir

class PanelFragment : Fragment() {

    private val beautyPanelFragment = BeautyPanelFragment.newInstance()
    private val stickerPanelFragment = StickerPanelFragment.newInstance()
    private lateinit var currentFragment: Fragment


    private var _binding: FragmentPanelBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentFragment = beautyPanelFragment
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPanelBinding.inflate(inflater, container, false)
        initBeautyPanelFragment()
        initStickerPanelFragment()
        addFragment(currentFragment)
        initOnClickListener()
        return binding.root
    }

    private fun initOnClickListener() {
        binding.beauty.setOnClickListener {
            replaceFragment(beautyPanelFragment)
            showPanel()
        }
        binding.sticker.setOnClickListener {
            replaceFragment(stickerPanelFragment)
            showPanel()
        }
    }

    fun hidePanel() {
        _binding?.let {
            binding.panel.visibility = View.GONE
            binding.beauty.visibility = View.VISIBLE
            binding.sticker.visibility = View.VISIBLE
        }
    }

    private fun showPanel() {
        binding.panel.visibility = View.VISIBLE
        binding.beauty.visibility = View.GONE
        binding.sticker.visibility = View.GONE
    }

    var renderCompareOnTouchDownListener: (() -> Unit)? = null
    var renderCompareOnTouchUpListener: (() -> Unit)? = null
    lateinit var beautyTabDataList: (() -> List<TabData>)

    var initBeautyPanelFragment: ((BeautyPanelFragment) -> Unit)? = null
    private fun initBeautyPanelFragment() {
        beautyPanelFragment.renderCompareOnTouchDownListener = {
            renderCompareOnTouchDownListener?.invoke()
        }
        beautyPanelFragment.renderCompareOnTouchUpListener = {
            renderCompareOnTouchUpListener?.invoke()
        }
        beautyPanelFragment.tabDataList = beautyTabDataList.invoke()
        initBeautyPanelFragment?.invoke(beautyPanelFragment)
    }

    var initStickerPanelFragment: ((StickerPanelFragment) -> Unit)? = null
    lateinit var stickerTabDataList: (() -> List<TabData>)
    private fun initStickerPanelFragment() {
        stickerPanelFragment.renderCompareOnTouchDownListener = {
            renderCompareOnTouchDownListener?.invoke()
        }
        stickerPanelFragment.renderCompareOnTouchUpListener = {
            renderCompareOnTouchUpListener?.invoke()
        }
        stickerPanelFragment.tabDataList = stickerTabDataList.invoke()
        initStickerPanelFragment?.invoke(stickerPanelFragment)
    }

    private fun addFragment(fragment: Fragment) {
        val fragmentManager = childFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.add(binding.panel.id, fragment)
        transaction.commit()
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = childFragmentManager
        val transaction = fragmentManager.beginTransaction()
        if (fragment.isAdded) {
            transaction.hide(currentFragment)
            transaction.show(fragment)
        } else {
            transaction.hide(currentFragment)
            transaction.add(binding.panel.id, fragment)
        }
        currentFragment = fragment
        transaction.commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        dLog("PanelFragment onDestroy")
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(rootDirPath: String) = PanelFragment().apply {
            rootDir = rootDirPath
        }
    }
}