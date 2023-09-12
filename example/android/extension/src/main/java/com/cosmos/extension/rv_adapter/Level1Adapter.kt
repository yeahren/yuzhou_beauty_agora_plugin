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
import com.cosmos.config_type.TabData
import com.cosmos.extension.R
import com.cosmos.extension.rootDir
import com.cosmos.view_can_select.toPX
import java.io.File


internal class Level1Adapter constructor(private val tabData: TabData) :
    RecyclerView.Adapter<Level1Adapter.ViewHolder>() {

    /**
     * viewHolder
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.type2_icon)
        val iconBorder: ImageView = view.findViewById(R.id.type2_icon_border)
        val name: TextView = view.findViewById(R.id.type2_name)
        val rate: TextView = view.findViewById(R.id.rate)
    }

    var itemShouldShowInnerDataSelectUI: ((Int) -> Unit)? = null
    var itemOnChoose: ((Int) -> Unit)? = null
    var itemOnShowLevel2: ((Int) -> Unit)? = null
    var itemOnPrepare: ((Int) -> Unit)? = null
    var itemOnRender: ((Int) -> Unit)? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val nowSelectPosition = tabData.nowSelectPosition
        if (nowSelectPosition >= 0) {
            itemOnChoose?.invoke(nowSelectPosition)
            itemShouldShowInnerDataSelectUI?.invoke(nowSelectPosition)
            itemOnPrepare?.invoke(nowSelectPosition)
        }
    }


    private val listener = View.OnClickListener {
        val position = it.tag as Int
        if (position >= 0)
            if (tabData.level1List()[position].hasNextLevel()) {
                choose(position)
                itemOnShowLevel2?.invoke(position)
            } else if (position != tabData.nowSelectPosition) {
                choose(position)
                itemShouldShowInnerDataSelectUI?.invoke(position)
                itemOnPrepare?.invoke(position)
                itemOnRender?.invoke(position)
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
        val data = tabData.level1List()[position]
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
        holder.iconBorder.isSelected = tabData.nowSelectPosition == position
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
        if (data.showValue()) {
            holder.rate.text = data.getRealProgress()[0].toString()
            holder.rate.visibility = View.VISIBLE
        } else {
            holder.rate.visibility = View.INVISIBLE
        }
    }

    private fun choose(position: Int) {
        val preSelectPosition = tabData.nowSelectPosition
        tabData.nowSelectPosition = position

        notifyItemChanged(position)
        if (preSelectPosition >= 0) {
            notifyItemChanged(preSelectPosition)
        }
        itemOnChoose?.invoke(position)
    }

    fun notifyItemChange(position: Int = -1) {
        if (tabData.nowSelectPosition >= 0) {
            var changeRatePosition = position
            if (changeRatePosition < 0) {
                changeRatePosition = tabData.nowSelectPosition
            }
            notifyItemChanged(changeRatePosition)
        }
    }

    override fun getItemCount(): Int {
        return tabData.level1List().size
    }
}