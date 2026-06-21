package com.valtio.app.util

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object LocaleManager {
    private const val AUTO = "auto"

    fun applyLocale(context: Context, languageTag: String) {
        val locales = if (languageTag == AUTO) {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(languageTag)
        }

        AppCompatDelegate.setApplicationLocales(locales)
    }

    fun currentLanguageTags(): String {
        return AppCompatDelegate.getApplicationLocales().toLanguageTags()
    }
}
