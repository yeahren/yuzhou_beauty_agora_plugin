package com.cosmos.view_can_select

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.cosmos.extension.R

class TextViewCanSelect : androidx.appcompat.widget.AppCompatTextView {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(
        context,
        attrs,
        android.R.attr.textViewStyle
    )

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.TextViewCanSelect)
        isSelect = typeArray.getBoolean(R.styleable.TextViewCanSelect_isSelect, false)
        typeArray.recycle()
        setTextColor(ContextCompat.getColor(context, R.color.white_transparent))
    }

    var isSelect = false
        set(value) {
            if (field == value) {
                return
            }
            field = value
            updateSelectState()
            invalidate()
        }

    private fun updateSelectState() {
        if (isSelect) {
            setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            setTextColor(ContextCompat.getColor(context, R.color.white_transparent))
        }
    }

    private var realWidth = 0
    private var realHeight = 0

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        realHeight = h
        realWidth = w
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            if (isSelect) {
                it.drawUnderLine()
            }
        }
    }

    private fun Canvas.drawUnderLine() {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 0.3.toPX()
        paint.color = ContextCompat.getColor(context, R.color.white)
        drawLine(0f, realHeight - 1f, realWidth.toFloat(), realHeight - 1f, paint)
    }

}