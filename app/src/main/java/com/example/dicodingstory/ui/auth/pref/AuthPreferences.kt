package com.example.dicodingstory.ui.auth.pref

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.dicodingstory.data.response.LoginResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class AuthPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    private val USERNAME_KEY = stringPreferencesKey("username")
    private val USER_ID_KEY = stringPreferencesKey("userId")
    private val TOKEN_KEY = stringPreferencesKey("token")

    fun getSession(): Flow<LoginResult> {
        return dataStore.data.map { preferences ->
            LoginResult(
                preferences[USER_ID_KEY] ?: "",
                preferences[USERNAME_KEY] ?: "",
                preferences[TOKEN_KEY] ?: ""
            )
        }.also { Log.d("Authentication", "Session loaded") }
    }

    suspend fun saveSession(token: String, name: String, id: String) {
        dataStore.edit {
            it[TOKEN_KEY] = token
            it[USERNAME_KEY] = name
            it[USER_ID_KEY] = id
        }
        Log.d("Authentication", "Session saved: token=$token, name=$name, id=$id")
    }

    suspend fun clearSession() {
        dataStore.edit {
            it.clear()
        }
        Log.d("Authentication", "Session cleared")
    }

    companion object {
        @Volatile
        private var INSTANCE: AuthPreferences? = null
        fun getInstance(dataStore: DataStore<Preferences>): AuthPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = AuthPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}