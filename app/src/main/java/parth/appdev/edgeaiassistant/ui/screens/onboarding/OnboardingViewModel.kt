package parth.appdev.edgeaiassistant.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import parth.appdev.edgeaiassistant.data.preferences.UserPreferences
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val prefs: UserPreferences
) : ViewModel() {

    fun completeOnboarding() {
        viewModelScope.launch { prefs.setOnboardingComplete(true) }
    }
}