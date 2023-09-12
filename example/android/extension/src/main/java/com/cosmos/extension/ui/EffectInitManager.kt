package com.cosmos.extension.ui

class EffectInitManager {
    var initEffect: ((oneKeyPosition: Int, lookupPosition: Int) -> Unit)? = null
    var linkEffect: ((oneKeyPosition: Int) -> Unit)? = null
    fun startInitEffect() {
        initEffect?.let {
            val oneKeyPosition = 1
            val lookupPosition = 1
            linkEffect?.invoke(oneKeyPosition)
            it.invoke(oneKeyPosition, lookupPosition)
        }
    }
}