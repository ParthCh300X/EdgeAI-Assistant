package parth.appdev.edgeaiassistant.ui.screens.analytics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import parth.appdev.edgeaiassistant.data.local.AppDatabase
import parth.appdev.edgeaiassistant.data.local.model.UsageStat

data class AnalyticsUiState(
    val totalCommands: Int = 0,
    val avgExecutionTime: Float = 0f,
    val usageStats: List<UsageStat> = emptyList()
)

class AnalyticsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val dao = AppDatabase.getInstance(application).analyticsDao()

    private val _state = MutableStateFlow(AnalyticsUiState())
    val state: StateFlow<AnalyticsUiState> = _state

    init {
        loadAnalytics()
    }

    private fun loadAnalytics() {
        CoroutineScope(Dispatchers.IO).launch {

            val total = dao.getTotalCount()
            val avg = dao.getAverageExecutionTime() ?: 0f
            val stats = dao.getUsageStats()

            _state.value = AnalyticsUiState(
                totalCommands = total,
                avgExecutionTime = avg,
                usageStats = stats
            )
        }
    }
}