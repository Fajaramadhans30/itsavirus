package com.test.itsavirustest.util.custom_map

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

class CustomButton : AppCompatButton {
    constructor(context: Context) : super(context) {
        applyCustomFont(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        applyCustomFont(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        applyCustomFont(context)
    }

    private fun applyCustomFont(context: Context) {
        val customFont: Typeface? = FontCache.getTypeface("ArbFONTS-Cairo-Light.ttf", context)
        typeface = customFont
    }
}