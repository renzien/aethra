package com.renzien.aethra.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.aethraDataStore by preferencesDataStore(
    name = "aethra_preferences"
)

class OnboardingPreferences(context: Context) {
    private val dataStore = context.applicationContext.aethraDataStore

    private val completedKey = booleanPreferencesKey(
        name = "onboarding_completed"
    )

    suspend fun isCompleted(): Boolean {
        return dataStore.data.first()[completedKey] ?: false
    }

    suspend fun complete() {
        dataStore.edit { preferences ->
            preferences[completedKey] = true
        }
    }
}