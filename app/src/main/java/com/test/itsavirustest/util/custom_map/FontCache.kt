package com.test.itsavirustest.util.custom_map

import android.content.Context
import android.graphics.Typeface
import java.util.*

object FontCache {
    private val fontCache =
        HashMap<String, Typeface?>()

    fun getTypeface(fontname: String, context: Context): Typeface? {
        var typeface = fontCache[fontname]
        if (typeface == null) {
            typeface = try {
                Typeface.createFromAsset(context.assets, fontname)
            } catch (e: Exception) {
                return null
            }
            fontCache[fontname] = typeface
        }
        return typeface
    }
}