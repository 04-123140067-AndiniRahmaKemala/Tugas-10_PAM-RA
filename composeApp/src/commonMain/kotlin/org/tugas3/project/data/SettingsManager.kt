package org.tugas3.project.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsManager(private val dataStore: DataStore<Preferences>) {
    private val themeKey = booleanPreferencesKey("is_dark_mode")
    private val sortOrderKey = stringPreferencesKey("sort_order")

    val isDarkMode: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[themeKey] ?: false
    }

    val sortOrder: Flow<String> = dataStore.data.map { preferences ->
        preferences[sortOrderKey] ?: "date_desc"
    }

    suspend fun setTheme(isDarkMode: Boolean) {
        dataStore.edit { preferences ->
            preferences[themeKey] = isDarkMode
        }
    }

    suspend fun setSortOrder(order: String) {
        dataStore.edit { preferences ->
            preferences[sortOrderKey] = order
        }
    }
}
