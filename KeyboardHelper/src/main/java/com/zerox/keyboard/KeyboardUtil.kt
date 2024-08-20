package com.zerox.keyboard

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.text.InputType
import android.text.TextUtils
import android.text.method.ArrowKeyMovementMethod
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment

const val KEYBOARD_MIN_HEIGHT_RATIO = 0.15

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Fragment.openKeyboard(view: View) {
    view.let { activity?.openKeyboard(it) }
}

fun EditText.hideKeyboard() {
    setRawInputType(InputType.TYPE_NULL)
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}

fun EditText.openKeyboard() {
    requestFocus()
    val imm: InputMethodManager =
        context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun Context.openKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(view, 0)
}

fun EditText.showKeyboardWithText(text: String) {
    // Request focus to ensure the EditText is ready for input
    requestFocus()

    // Get the InputMethodManager from the context
    val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager

    // Check if the InputMethodManager is not null, then show the keyboard
    imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

    // Set the appropriate input type flags
    inputType =
        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE

    // Set the text, ensuring it’s trimmed and using SPANNABLE for better text handling
    setText(text.trim(), TextView.BufferType.SPANNABLE)

    // Ensure the cursor is set at the end of the text
    setSelection(text.length)

    // Enable arrow key movement within the EditText (in case of multi-line text)
    movementMethod = ArrowKeyMovementMethod.getInstance()
}

fun EditText.enableKeyboard() {
    if (TextUtils.isEmpty(text.toString())) {
        setRawInputType(InputType.TYPE_CLASS_TEXT)
    } else {
        setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE)
        setText(text.toString().trim(), TextView.BufferType.SPANNABLE)
        text?.length?.let { setSelection(it) }
    }
    movementMethod = ArrowKeyMovementMethod.getInstance()
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun View.hideKeyboard() {
    val inputMethodManager =
        context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    if (inputMethodManager.isActive) inputMethodManager.hideSoftInputFromWindow(this.windowToken, 0)
}

/**
 * Determine if the keyboard is visible.
 *
 * @param activity The Activity.
 * @return Whether the keyboard is visible or not.
 */
fun isKeyboardVisible(activity: Activity): Boolean {
    val r = Rect()
    val activityRoot = getActivityRoot(activity)

    activityRoot.getWindowVisibleDisplayFrame(r)

    val location = IntArray(2)
    getContentRoot(activity).getLocationOnScreen(location)

    val screenHeight = activityRoot.rootView.height
    val heightDiff = screenHeight - r.height() - location[1]

    return heightDiff > screenHeight * KEYBOARD_MIN_HEIGHT_RATIO
}

fun getActivityRoot(activity: Activity): View {
    return getContentRoot(activity).rootView
}

fun getContentRoot(activity: Activity): ViewGroup {
    return activity.findViewById(android.R.id.content)
}
