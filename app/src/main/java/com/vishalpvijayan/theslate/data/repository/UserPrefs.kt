package com.vishalpvijayan.theslate.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.vishalpvijayan.theslate.domain.model.UserSession
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPrefs @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val userId = stringPreferencesKey("user_id")
    private val userName = stringPreferencesKey("user_name")
    private val email = stringPreferencesKey("email")
    private val photoUrl = stringPreferencesKey("photo_url")
    private val loggedIn = booleanPreferencesKey("logged_in")

    fun observeSession(): Flow<UserSession> = context.dataStore.data.map { pref ->
        UserSession(
            userId = pref[userId].orEmpty(),
            userName = pref[userName].orEmpty(),
            email = pref[email].orEmpty(),
            photoUrl = pref[photoUrl].orEmpty(),
            isLoggedIn = pref[loggedIn] ?: false
        )
    }

    suspend fun save(session: UserSession) {
        context.dataStore.edit { pref ->
            pref[userId] = session.userId
            pref[userName] = session.userName
            pref[email] = session.email
            pref[photoUrl] = session.photoUrl
            pref[loggedIn] = session.isLoggedIn
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
