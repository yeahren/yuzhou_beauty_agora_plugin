package com.cosmos.view_can_select

import android.content.res.Resources
import android.util.TypedValue

const val LogTag = "view_can_select"

fun Number.toPX(): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(),
        Resources.getSystem().displayMetrics
    )
}
