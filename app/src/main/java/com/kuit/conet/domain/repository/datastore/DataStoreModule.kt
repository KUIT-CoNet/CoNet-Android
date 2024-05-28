package com.kuit.conet.domain.repository.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class DataStoreModule(
    private val context: Context
) {

    private val dataStoreName: String = "DATA_STORE_NAME"
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = dataStoreName)

    private val accessTokenKey = stringPreferencesKey("ACCESS_TOKEN")
    private val refreshTokenKey = stringPreferencesKey("REFRESH_TOKEN")
    private val isOptionKey = intPreferencesKey("IS_OPTION")

    val accessToken: Flow<String>
        get() = context.dataStore.data
            .catch { exception ->
                if (exception is Exception) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[accessTokenKey] ?: ""
            }

    val bearerAccessToken: Flow<String>
        get() = context.dataStore.data
            .catch { exception ->
                if (exception is Exception) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                if (preferences[accessTokenKey] != null) {
                    "Bearer ${preferences[accessTokenKey]}"
                } else {
                    ""
                }
            }

    val refreshToken: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is Exception) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[refreshTokenKey] ?: ""
        }

    val bearerRefreshToken: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is Exception) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            if (preferences[refreshTokenKey] != null) {
                "Bearer ${preferences[refreshTokenKey]}"
            } else {
                ""
            }
        }

    val isOption: Flow<Int> = context.dataStore.data
        .catch { exception ->
            if (exception is Exception) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[isOptionKey] ?: -1
        }

    suspend fun setAccesToken(accessToken: String) {
        context.dataStore.edit { preferences ->
            preferences[accessTokenKey] = accessToken
        }
    }

    suspend fun setRefreshToken(refreshToken: String) {
        context.dataStore.edit { preferences ->
            preferences[refreshTokenKey] = refreshToken
        }
    }

    suspend fun setIsOption(isOption: Int) {
        context.dataStore.edit { preferences ->
            preferences[isOptionKey] = isOption
        }
    }

    /*// TODO 미사용시 삭제 요망
    private val userNameKey = stringPreferencesKey("USER_NAME")
    private val userImgKey = stringPreferencesKey("USER_IMG")
    private val userEmailKey = stringPreferencesKey("USER_EMAIL")

    val userName: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is Exception) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[userNameKey] ?: ""
        }

    val userImg: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is Exception) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[userImgKey] ?: ""
        }

    val userEmail: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is Exception) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[userEmailKey] ?: ""
        }

    suspend fun setUserName(userName: String) {
        context.dataStore.edit { preferences ->
            preferences[userNameKey] = userName
        }
    }

    suspend fun setUserImg(userImg: String) {
        context.dataStore.edit { preferences ->
            preferences[userImgKey] = userImg
        }
    }

    suspend fun setUserEmail(userEmail: String) {
        context.dataStore.edit { preferences ->
            preferences[userEmailKey] = userEmail
        }
    }*/

}