package com.capstone.mobiledevelopment.nutrilens.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")
const val SLEEP_PREFERENCES_NAME = "sleep_preferences"

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun saveSession(user: UserModel) {
        dataStore.edit { preferences ->
            preferences[EMAIL_KEY] = user.email
            preferences[TOKEN_KEY] = user.token
            preferences[IS_LOGIN_KEY] = true
        }
    }

    fun getSession(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            UserModel(
                preferences[EMAIL_KEY] ?: "",
                preferences[TOKEN_KEY] ?: "",
                preferences[IS_LOGIN_KEY] ?: false
            )
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun saveStepCount(stepCount: Int) {
        dataStore.edit { preferences ->
            preferences[STEP_COUNT_KEY] = stepCount
        }
    }

    fun getStepCount(): Flow<Int> {
        return dataStore.data.map { preferences ->
            preferences[STEP_COUNT_KEY] ?: 0
        }
    }

    class SleepSubscriptionStatus(private val dataStore: DataStore<Preferences>) {

        private object PreferencesKeys {
            val SUBSCRIBED_TO_SLEEP_DATA = booleanPreferencesKey("subscribed_to_sleep_data")
        }

        // Observed Flow will notify the observer when the the sleep subscription status has changed.
        val subscribedToSleepDataFlow: Flow<Boolean> = dataStore.data.map { preferences ->
            // Get the subscription value, defaults to false if not set:
            preferences[PreferencesKeys.SUBSCRIBED_TO_SLEEP_DATA] ?: false
        }

        // Updates subscription status.
        suspend fun updateSubscribedToSleepData(subscribedToSleepData: Boolean) {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.SUBSCRIBED_TO_SLEEP_DATA] = subscribedToSleepData
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val EMAIL_KEY = stringPreferencesKey("email")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")
        private val STEP_COUNT_KEY = intPreferencesKey("step_count")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}