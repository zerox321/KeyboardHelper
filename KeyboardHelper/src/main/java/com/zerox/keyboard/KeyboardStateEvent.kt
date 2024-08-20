package com.zerox.keyboard

import android.app.Activity
import android.view.ViewTreeObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import java.lang.ref.WeakReference


object KeyboardStateEvent {

    /**
     * Set keyboard visibility event listener.
     * This automatically removes the registered event listener when the lifecycle owner is destroyed.
     * This function is intended to be used by fragments so the listener is removed when the fragment
     * is no longer displayed, preventing fragment leaks and crashes.
     *
     * @param activity The Activity on which the keyboard changes are to be detected.
     * @param lifecycleOwner The owner of the lifecycle whose destruction causes the event to be
     * automatically unregistered. Typically a fragment.
     * @param listener The event listener.
     */
    @Suppress("unused")
    @JvmStatic
    fun setEventListener(
        activity: Activity, lifecycleOwner: LifecycleOwner, listener: KeyboardStateEventListener
    ) {
        val unRegistrar = registerEventListener(activity, listener)

        lifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                unRegistrar.unregister()
            }
        })
    }



    /**
     * Set keyboard visibility change event listener.
     *
     * @param activity The Activity.
     * @param listener The KeyboardStateEventListener.
     * @return An UnregisterEvent that can be used to unregister the listener.
     */
    private fun registerEventListener(
        activity: Activity, listener: KeyboardStateEventListener
    ): UnregisterEvent {
        val weakActivity = WeakReference(activity)
        val activityRoot = getActivityRoot(activity)

        val layoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
            private var wasOpened = false

            override fun onGlobalLayout() {
                val activity = weakActivity.get() ?: return
                val isOpen = isKeyboardVisible(activity)

                if (isOpen == wasOpened) return // keyboard state has not changed

                wasOpened = isOpen
                listener.onVisibilityChanged(isOpen)
            }
        }

        activityRoot.viewTreeObserver.addOnGlobalLayoutListener(layoutListener)

        return UnregisterCallback(activity, layoutListener)
    }


}