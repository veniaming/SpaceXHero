package com.example.spacexhero.data.source.cache

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.spacexhero.domain.model.Launch
import com.example.spacexhero.domain.model.LaunchDetails
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = "launch_cache")

class LaunchCacheManager @Inject constructor(
    private val context: Context
) {
    private val json = Json { ignoreUnknownKeys = true }
    private val CACHE_DURATION_MS = 3600000L // 1 hour in milliseconds

    private fun getCacheLaunchesKey(offset: Int) = stringPreferencesKey("launches_$offset")
    private fun getCacheLaunchesTimestampKey(offset: Int) =
        longPreferencesKey("launches_${offset}_timestamp")

    private fun getCacheLaunchDetailsKey(id: String) = stringPreferencesKey("launch_detail_$id")
    private fun getCacheLaunchDetailsTimestampKey(id: String) =
        longPreferencesKey("launch_detail_${id}_timestamp")

    suspend fun saveLaunches(offset: Int, launches: List<Launch>) {
        context.dataStore.edit { preferences ->
            preferences[getCacheLaunchesKey(offset)] = json.encodeToString(launches)
            preferences[getCacheLaunchesTimestampKey(offset)] = System.currentTimeMillis()
        }
    }

    suspend fun getLaunches(offset: Int): List<Launch>? {
        val preferences = context.dataStore.data.first()

        val timestamp = preferences[getCacheLaunchesTimestampKey(offset)] ?: return null
        val currentTime = System.currentTimeMillis()
        if (currentTime - timestamp > CACHE_DURATION_MS) {
            return null // Cache expired
        }

        val jsonString = preferences[getCacheLaunchesKey(offset)] ?: return null
        return try {
            json.decodeFromString<List<Launch>>(jsonString)
        } catch (_: Exception) {
            null
        }
    }

    suspend fun clearLaunchesCache() {
        context.dataStore.edit { preferences ->
            val launchesKeyNames = preferences.asMap().keys
                .map { it.name }
                .filter { it.startsWith("launches_") }

            launchesKeyNames.forEach { keyName ->
                if (keyName.endsWith("_timestamp")) {
                    preferences.remove(longPreferencesKey(keyName))
                } else {
                    preferences.remove(stringPreferencesKey(keyName))
                }
            }
        }
    }

    suspend fun saveLaunchDetails(id: String, launch: LaunchDetails) {
        context.dataStore.edit { preferences ->
            preferences[getCacheLaunchDetailsKey(id)] = json.encodeToString(launch)
            preferences[getCacheLaunchDetailsTimestampKey(id)] = System.currentTimeMillis()
        }
    }

    suspend fun getLaunchDetails(id: String): LaunchDetails? {
        val preferences = context.dataStore.data.first()

        val timestamp = preferences[getCacheLaunchDetailsTimestampKey(id)] ?: return null
        val currentTime = System.currentTimeMillis()
        if (currentTime - timestamp > CACHE_DURATION_MS) {
            return null // Cache expired
        }

        val jsonString = preferences[getCacheLaunchDetailsKey(id)] ?: return null
        return try {
            json.decodeFromString<LaunchDetails>(jsonString)
        } catch (_: Exception) {
            null
        }
    }

    suspend fun clearAllCache() {
        context.dataStore.edit { it.clear() }
    }
}


