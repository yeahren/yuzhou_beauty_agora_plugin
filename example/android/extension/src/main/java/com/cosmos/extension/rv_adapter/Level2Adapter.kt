package com.cosmos.extension.rv_adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.cosmos.config_type.MakeupData
import com.cosmos.extension.R
import com.cosmos.extension.rootDir
import com.cosmos.view_can_select.toPX
import java.io.File

/**
 * 单选有返回的Adapter
 */
class Level2Adapter(private val level1Data: MakeupData) :
    RecyclerView.Adapter<Level2Adapter.ViewHolder>() {

    /**
     * viewHolder
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.type2_icon)
        val iconBorder: ImageView = view.findViewById(R.id.type2_icon_border)
        val name: TextView = view.findViewById(R.id.type2_name)
        val rate: TextView = view.findViewById(R.id.rate)
    }


    var itemShouldShowSeekBar: ((Int) -> Unit)? = null
    var itemOnChoose: ((Int) -> Unit)? = null
    var itemOnBackToLevel1: (() -> Unit)? = null
    var itemOnPrepare: ((Int) -> Unit)? = null
    var itemOnRender: ((Int) -> Unit)? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        itemShouldShowSeekBar?.invoke(level1Data.nowSelect)
        itemOnPrepare?.invoke(level1Data.nowSelect)
        itemOnChoose?.invoke(level1Data.nowSelect)
    }

    private val listener = View.OnClickListener {
        val position = it.tag as Int
        if (level1Data.level2List[position].isBackBtn()) {
            itemOnBackToLevel1?.invoke()
        } else
            if (position != level1Data.nowSelect) {
                itemShouldShowSeekBar?.invoke(position)
                choose(position)
                itemOnRender?.invoke(position)
                itemOnPrepare?.invoke(position)
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_beauty, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.itemView.setOnClickListener(listener)
        return viewHolder
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = holder.adapterPosition
        val data = level1Data.level2List[position]
        when (data.bgType()) {
            bg_type_icon -> holder.icon.setPadding(
                10.toPX().toInt(),
                10.toPX().toInt(),
                10.toPX().toInt(),
                10.toPX().toInt()
            )
            else -> {
                holder.icon.setPadding(
                    0,
                    0,
                    0,
                    0
                )
            }
        }
        when (data.shape()) {
            shape_rect -> holder.iconBorder.setBackgroundResource(R.drawable.icon_selector_rect)
            else -> holder.iconBorder.setBackgroundResource(R.drawable.icon_selector_circle)
        }
        holder.iconBorder.isSelected = level1Data.nowSelect == position
        var glideBuilder: RequestBuilder<Drawable>? = null
        when (holder.iconBorder.isSelected) {
            true -> {
                if (data.srcSelectedPath != "")
                    glideBuilder = Glide.with(holder.itemView)
                        .load(File("$rootDir/${data.srcSelectedPath}"))
                        .centerInside()
            }
            else -> {
                if (data.srcNotSelectedPath != "")
                    glideBuilder = Glide.with(holder.itemView)
                        .load(File("$rootDir/${data.srcNotSelectedPath}"))
                        .centerInside()
            }
        }
        when (data.shape()) {
            shape_rect -> glideBuilder?.transform(RoundedCorners(8.toPX().toInt()))
                ?.into(holder.icon)
            else -> glideBuilder?.into(holder.icon)
        }
        holder.name.text = data.name
        holder.rate.visibility = View.INVISIBLE
    }

    private fun choose(position: Int) {
        val preSelectPosition = level1Data.nowSelect
        level1Data.nowSelect = position

        notifyItemChanged(position)
        notifyItemChanged(preSelectPosition)
        itemOnChoose?.invoke(position)
    }

    fun notifyItemChange(position: Int = -1) {
        var changeRatePosition = position
        if (changeRatePosition < 0) {
            changeRatePosition = level1Data.nowSelect
        }
        notifyItemChanged(changeRatePosition)
    }

    override fun getItemCount(): Int = level1Data.level2List.size
}