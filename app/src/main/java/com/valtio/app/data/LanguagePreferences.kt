package com.valtio.app.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "app_settings"

val Context.languageDataStore by preferencesDataStore(name = DATASTORE_NAME)

object LanguagePreferences {
    private val LANGUAGE_KEY = stringPreferencesKey("language_tag")
    private const val AUTO_TAG = "auto"

    fun languageFlow(context: Context): Flow<String> =
        context.languageDataStore.data.map { prefs ->
            prefs[LANGUAGE_KEY] ?: AUTO_TAG
        }

    suspend fun saveLanguage(context: Context, tag: String) {
        context.languageDataStore.edit { prefs ->
            prefs[LANGUAGE_KEY] = tag
        }
    }
}
