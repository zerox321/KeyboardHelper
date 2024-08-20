package com.zerox.keyboard

fun interface KeyboardStateEventListener {
    fun onVisibilityChanged(isOpen: Boolean)
}