package com.example.omdbtest.ui

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import java.lang.reflect.Field


open class FontsOverride {
    companion object {
        fun changeDefaultFont(context: Context) {
            setDefaultFont(context, "DEFAULT", "Raleway.ttf")
        }

        private fun setDefaultFont(
            context: Context,
            staticTypefaceFieldName: String,
            fontAssetName: String
        ) {
            val regular = Typeface.createFromAsset(
                context.assets,
                fontAssetName
            )
            replaceFont(staticTypefaceFieldName, regular)
        }

        private fun replaceFont(staticTypefaceFieldName: String, newTypeface: Typeface) {
            if (Build.VERSION.SDK_INT >= 21) {
                val newMap: MutableMap<String, Typeface> = HashMap()
                newMap["sans-serif"] = newTypeface
                try {
                    val staticField: Field = Typeface::class.java.getDeclaredField("sSystemFontMap")
                    staticField.isAccessible = true
                    staticField.set(null, newMap)
                } catch (e: NoSuchFieldException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
            } else {
                try {
                    val staticField: Field =
                        Typeface::class.java.getDeclaredField(staticTypefaceFieldName)
                    staticField.isAccessible = true
                    staticField.set(null, newTypeface)
                } catch (e: NoSuchFieldException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
            }
        }
    }
}