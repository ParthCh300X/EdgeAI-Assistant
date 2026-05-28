package parth.appdev.edgeaiassistant.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("user_prefs")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        val VOICE_ENABLED       = booleanPreferencesKey("voice_enabled")
        val DARK_MODE_OVERRIDE  = booleanPreferencesKey("dark_mode_override")
        val USE_SYSTEM_THEME    = booleanPreferencesKey("use_system_theme")
    }

    val isOnboardingComplete : Flow<Boolean> =
        context.dataStore.data.map { it[ONBOARDING_COMPLETE] ?: false }

    val isVoiceEnabled : Flow<Boolean> =
        context.dataStore.data.map { it[VOICE_ENABLED] ?: true }

    // true = dark, false = light
    val isDarkMode : Flow<Boolean> =
        context.dataStore.data.map { it[DARK_MODE_OVERRIDE] ?: true }

    // true = follow system, false = use manual override
    val useSystemTheme : Flow<Boolean> =
        context.dataStore.data.map { it[USE_SYSTEM_THEME] ?: true }

    suspend fun setOnboardingComplete(done: Boolean) =
        context.dataStore.edit { it[ONBOARDING_COMPLETE] = done }

    suspend fun setVoiceEnabled(enabled: Boolean) =
        context.dataStore.edit { it[VOICE_ENABLED] = enabled }

    suspend fun setDarkMode(dark: Boolean) {
        context.dataStore.edit {
            it[DARK_MODE_OVERRIDE] = dark
            it[USE_SYSTEM_THEME]   = false   // switching off system follow
        }
    }

    suspend fun setUseSystemTheme(follow: Boolean) =
        context.dataStore.edit { it[USE_SYSTEM_THEME] = follow }
}