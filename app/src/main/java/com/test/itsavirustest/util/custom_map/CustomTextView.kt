package com.test.itsavirustest.util.custom_map

import android.content.Context
import android.util.AttributeSet
import com.test.itsavirustest.util.custom_map.FontCache.getTypeface

class CustomTextView : androidx.appcompat.widget.AppCompatTextView {
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
        val customFont = getTypeface("ArbFONTS-Cairo-Light.ttf", context)
        typeface = customFont
    }
}