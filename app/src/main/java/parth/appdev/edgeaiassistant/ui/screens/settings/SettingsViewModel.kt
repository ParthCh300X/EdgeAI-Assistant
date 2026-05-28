package parth.appdev.edgeaiassistant.ui.screens.settings

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import parth.appdev.edgeaiassistant.data.preferences.UserPreferences
import parth.appdev.edgeaiassistant.data.repository.AnalyticsRepository
import parth.appdev.edgeaiassistant.data.repository.NoteRepository
import javax.inject.Inject

data class SettingsUiState(
    val voiceEnabled           : Boolean = true,
    val canScheduleExactAlarms : Boolean = true,
    val isDarkMode             : Boolean = true,
    val useSystemTheme         : Boolean = true
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefs               : UserPreferences,
    private val noteRepository      : NoteRepository,
    private val analyticsRepository : AnalyticsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state

    init {
        viewModelScope.launch {
            combine(
                prefs.isVoiceEnabled,
                prefs.isDarkMode,
                prefs.useSystemTheme
            ) { voice, dark, system ->
                SettingsUiState(
                    voiceEnabled           = voice,
                    isDarkMode             = dark,
                    useSystemTheme         = system,
                    canScheduleExactAlarms = checkExactAlarm()
                )
            }.collect { _state.value = it }
        }
    }

    private fun checkExactAlarm(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return context.getSystemService(AlarmManager::class.java).canScheduleExactAlarms()
        }
        return true
    }

    fun toggleVoice(enabled: Boolean) =
        viewModelScope.launch { prefs.setVoiceEnabled(enabled) }

    fun toggleDarkMode(dark: Boolean) =
        viewModelScope.launch { prefs.setDarkMode(dark) }

    fun toggleSystemTheme(follow: Boolean) =
        viewModelScope.launch { prefs.setUseSystemTheme(follow) }

    fun openExactAlarmSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.startActivity(
                Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }

    fun clearNotes()     = viewModelScope.launch { noteRepository.deleteAllNotes() }
    fun clearAnalytics() = viewModelScope.launch { analyticsRepository.deleteAll() }
}