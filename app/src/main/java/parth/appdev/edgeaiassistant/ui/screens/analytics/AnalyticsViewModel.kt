package parth.appdev.edgeaiassistant.ui.screens.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import parth.appdev.edgeaiassistant.data.local.model.DailyStat
import parth.appdev.edgeaiassistant.data.local.model.UsageStat
import parth.appdev.edgeaiassistant.data.repository.AnalyticsRepository
import javax.inject.Inject

data class AnalyticsUiState(
    val totalCommands    : Int            = 0,
    val avgExecutionTime : Float          = 0f,
    val usageStats       : List<UsageStat>  = emptyList(),
    val dailyStats       : List<DailyStat>  = emptyList()
)

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val repository: AnalyticsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AnalyticsUiState())
    val state: StateFlow<AnalyticsUiState> = _state

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.value = AnalyticsUiState(
                totalCommands    = repository.getTotalCount(),
                avgExecutionTime = repository.getAverageExecutionTime(),
                usageStats       = repository.getUsageStats(),
                dailyStats       = repository.getDailyStats()
            )
        }
    }
}